package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.ProductReview;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public List<ProductReview> findByProduct(int productId) throws Exception {
        String sql = "SELECT r.id, r.product_id, r.user_id, u.name AS user_name, r.rating, r.comment, r.created_at, r.updated_at " +
                "FROM product_reviews r JOIN users u ON r.user_id = u.id WHERE r.product_id = ? ORDER BY r.updated_at DESC";
        List<ProductReview> list = new ArrayList<>();
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

    public ProductReview findByUserAndProduct(int userId, int productId) throws Exception {
        String sql = "SELECT r.id, r.product_id, r.user_id, u.name AS user_name, r.rating, r.comment, r.created_at, r.updated_at " +
                "FROM product_reviews r JOIN users u ON r.user_id = u.id WHERE r.user_id = ? AND r.product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public int create(ProductReview review) throws Exception {
        String sql = "INSERT INTO product_reviews(product_id, user_id, rating, comment) VALUES(?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getProductId());
            ps.setInt(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void update(ProductReview review) throws Exception {
        String sql = "UPDATE product_reviews SET rating = ?, comment = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getId());
            ps.executeUpdate();
        }
    }

    private ProductReview map(ResultSet rs) throws Exception {
        ProductReview review = new ProductReview();
        review.setId(rs.getInt("id"));
        review.setProductId(rs.getInt("product_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setUserName(rs.getString("user_name"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        if (rs.getTimestamp("created_at") != null) {
            review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            review.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return review;
    }
}
