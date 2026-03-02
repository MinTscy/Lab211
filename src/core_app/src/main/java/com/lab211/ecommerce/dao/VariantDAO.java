package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.ProductVariant;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VariantDAO {
    public List<ProductVariant> findByProduct(int productId) throws Exception {
        String sql = "SELECT id, product_id, color, size, stock, price_delta FROM product_variants WHERE product_id = ?";
        List<ProductVariant> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public ProductVariant findById(int id) throws Exception {
        String sql = "SELECT id, product_id, color, size, stock, price_delta FROM product_variants WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public int create(ProductVariant v) throws Exception {
        String sql = "INSERT INTO product_variants(product_id, color, size, stock, price_delta) VALUES(?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getProductId());
            ps.setString(2, v.getColor());
            ps.setString(3, v.getSize());
            ps.setInt(4, v.getStock());
            ps.setDouble(5, v.getPriceDelta());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void update(ProductVariant v) throws Exception {
        String sql = "UPDATE product_variants SET color = ?, size = ?, stock = ?, price_delta = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getColor());
            ps.setString(2, v.getSize());
            ps.setInt(3, v.getStock());
            ps.setDouble(4, v.getPriceDelta());
            ps.setInt(5, v.getId());
            ps.executeUpdate();
        }
    }

    public void decrementStock(int variantId, int qty, Connection conn) throws Exception {
        String sql = "UPDATE product_variants SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, variantId);
            ps.setInt(3, qty);
            if (ps.executeUpdate() == 0) {
                throw new IllegalStateException("Insufficient stock for variant " + variantId);
            }
        }
    }

    private ProductVariant map(ResultSet rs) throws Exception {
        ProductVariant v = new ProductVariant();
        v.setId(rs.getInt("id"));
        v.setProductId(rs.getInt("product_id"));
        v.setColor(rs.getString("color"));
        v.setSize(rs.getString("size"));
        v.setStock(rs.getInt("stock"));
        v.setPriceDelta(rs.getDouble("price_delta"));
        return v;
    }
}
