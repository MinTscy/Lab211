package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.Voucher;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {
    private static volatile Boolean voucherProductColumnAvailable;

    public Voucher findValidByCode(String code) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = buildVoucherSelect(conn) +
                    " WHERE v.code = ? AND v.active = 1 AND (v.start_at IS NULL OR v.start_at <= NOW()) AND (v.end_at IS NULL OR v.end_at >= NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Voucher> findActiveByProduct(int productId) throws Exception {
        List<Voucher> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = buildVoucherSelect(conn) +
                    " WHERE v.active = 1 " +
                    buildProductScopeClause(conn) +
                    " AND (v.start_at IS NULL OR v.start_at <= NOW()) AND (v.end_at IS NULL OR v.end_at >= NOW()) " +
                    "ORDER BY " + buildProductOrderClause(conn);
            PreparedStatement ps = conn.prepareStatement(sql);
            if (hasVoucherProductColumn(conn)) {
                ps.setInt(1, productId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Voucher> findAll() throws Exception {
        List<Voucher> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = buildVoucherSelect(conn) + " ORDER BY v.id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int create(Voucher voucher) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            String sql;
            if (hasVoucherProductColumn(conn)) {
                sql = "INSERT INTO vouchers(code, product_id, discount_type, discount_value, max_discount, min_order, start_at, end_at, active) VALUES(?,?,?,?,?,?,?,?,?)";
            } else {
                sql = "INSERT INTO vouchers(code, discount_type, discount_value, max_discount, min_order, start_at, end_at, active) VALUES(?,?,?,?,?,?,?,?)";
            }
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bind(ps, voucher);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void update(Voucher voucher) throws Exception {
        try (Connection conn = DBUtil.getConnection()) {
            String sql;
            if (hasVoucherProductColumn(conn)) {
                sql = "UPDATE vouchers SET code = ?, product_id = ?, discount_type = ?, discount_value = ?, max_discount = ?, min_order = ?, start_at = ?, end_at = ?, active = ? WHERE id = ?";
            } else {
                sql = "UPDATE vouchers SET code = ?, discount_type = ?, discount_value = ?, max_discount = ?, min_order = ?, start_at = ?, end_at = ?, active = ? WHERE id = ?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            bind(ps, voucher);
            if (hasVoucherProductColumn(conn)) {
                ps.setInt(10, voucher.getId());
            } else {
                ps.setInt(9, voucher.getId());
            }
            ps.executeUpdate();
        }
    }

    public boolean codeExistsForOtherVoucher(String code, int voucherId) throws Exception {
        String sql = "SELECT 1 FROM vouchers WHERE code = ? AND id <> ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void bind(PreparedStatement ps, Voucher voucher) throws Exception {
        boolean supportsProductScope = supportsProductScope(ps.getConnection());

        ps.setString(1, voucher.getCode());
        int index = 2;
        if (supportsProductScope) {
            if (voucher.getProductId() == null || voucher.getProductId() <= 0) {
                ps.setNull(index++, java.sql.Types.INTEGER);
            } else {
                ps.setInt(index++, voucher.getProductId());
            }
        }
        ps.setString(index++, voucher.getDiscountType());
        ps.setDouble(index++, voucher.getDiscountValue());
        ps.setDouble(index++, voucher.getMaxDiscount());
        ps.setDouble(index++, voucher.getMinOrder());
        if (voucher.getStartAt() == null) {
            ps.setNull(index++, java.sql.Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index++, Timestamp.valueOf(voucher.getStartAt()));
        }
        if (voucher.getEndAt() == null) {
            ps.setNull(index++, java.sql.Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index++, Timestamp.valueOf(voucher.getEndAt()));
        }
        ps.setBoolean(index, voucher.isActive());
    }

    private Voucher map(ResultSet rs) throws Exception {
        Voucher v = new Voucher();
        v.setId(rs.getInt("id"));
        v.setCode(rs.getString("code"));
        if (hasColumn(rs, "product_id")) {
            int productId = rs.getInt("product_id");
            if (!rs.wasNull()) {
                v.setProductId(productId);
            }
            if (hasColumn(rs, "product_name")) {
                v.setProductName(rs.getString("product_name"));
            }
        }
        v.setDiscountType(rs.getString("discount_type"));
        v.setDiscountValue(rs.getDouble("discount_value"));
        v.setMaxDiscount(rs.getDouble("max_discount"));
        v.setMinOrder(rs.getDouble("min_order"));
        if (rs.getTimestamp("start_at") != null) {
            v.setStartAt(rs.getTimestamp("start_at").toLocalDateTime());
        }
        if (rs.getTimestamp("end_at") != null) {
            v.setEndAt(rs.getTimestamp("end_at").toLocalDateTime());
        }
        v.setActive(rs.getBoolean("active"));
        return v;
    }

    private String buildVoucherSelect(Connection conn) throws Exception {
        if (hasVoucherProductColumn(conn)) {
            return "SELECT v.id, v.code, v.product_id, p.name AS product_name, v.discount_type, v.discount_value, v.max_discount, v.min_order, v.start_at, v.end_at, v.active " +
                    "FROM vouchers v LEFT JOIN products p ON v.product_id = p.id";
        }
        return "SELECT v.id, v.code, v.discount_type, v.discount_value, v.max_discount, v.min_order, v.start_at, v.end_at, v.active " +
                "FROM vouchers v";
    }

    private String buildProductScopeClause(Connection conn) throws Exception {
        return hasVoucherProductColumn(conn)
                ? "AND (v.product_id IS NULL OR v.product_id = ?) "
                : "";
    }

    private String buildProductOrderClause(Connection conn) throws Exception {
        return hasVoucherProductColumn(conn)
                ? "v.product_id IS NULL, v.id ASC"
                : "v.id ASC";
    }

    private boolean supportsProductScope(Connection conn) throws Exception {
        return hasVoucherProductColumn(conn);
    }

    private boolean hasVoucherProductColumn(Connection conn) throws Exception {
        Boolean cached = voucherProductColumnAvailable;
        if (cached != null) {
            return cached;
        }
        synchronized (VoucherDAO.class) {
            if (voucherProductColumnAvailable != null) {
                return voucherProductColumnAvailable;
            }
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = conn.getCatalog();
            try (ResultSet rs = metaData.getColumns(schema, null, "vouchers", "product_id")) {
                voucherProductColumnAvailable = rs.next();
            }
            return voucherProductColumnAvailable;
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
