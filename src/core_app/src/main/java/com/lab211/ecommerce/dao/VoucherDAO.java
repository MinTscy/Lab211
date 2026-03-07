package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.Voucher;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VoucherDAO {
    public Voucher findValidByCode(String code) throws Exception {
        String sql = "SELECT id, code, discount_type, discount_value, max_discount, min_order, start_at, end_at, active " +
                "FROM vouchers WHERE code = ? AND active = 1 AND (start_at IS NULL OR start_at <= NOW()) AND (end_at IS NULL OR end_at >= NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getInt("id"));
                    v.setCode(rs.getString("code"));
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
            }
        }
        return null;
    }
}
