package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> findAll(boolean includeInactive) throws Exception {
        String sql = includeInactive
                ? "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                  "FROM products p LEFT JOIN shops s ON p.shop_id = s.id"
                : "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                  "FROM products p LEFT JOIN shops s ON p.shop_id = s.id WHERE p.active = 1";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Product findById(int id) throws Exception {
        String sql = "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                "FROM products p LEFT JOIN shops s ON p.shop_id = s.id WHERE p.id = ?";
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

    public List<Product> findByShop(int shopId) throws Exception {
        String sql = "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                "FROM products p LEFT JOIN shops s ON p.shop_id = s.id WHERE p.shop_id = ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int create(Product p) throws Exception {
        String sql = "INSERT INTO products(shop_id, name, description, base_price, active) VALUES(?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getShopId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setDouble(4, p.getBasePrice());
            ps.setBoolean(5, p.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void update(Product p) throws Exception {
        String sql = "UPDATE products SET shop_id = ?, name = ?, description = ?, base_price = ?, active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getShopId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setDouble(4, p.getBasePrice());
            ps.setBoolean(5, p.isActive());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    private Product map(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setShopId(rs.getInt("shop_id"));
        p.setShopName(rs.getString("shop_name"));
        p.setShopRating(rs.getDouble("shop_rating"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setBasePrice(rs.getDouble("base_price"));
        p.setActive(rs.getBoolean("active"));
        return p;
    }

    public List<Product> findFeaturedByShopRating(int limit) throws Exception {
        String sql = "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                "FROM products p LEFT JOIN shops s ON p.shop_id = s.id " +
                "WHERE p.active = 1 ORDER BY s.rating DESC, p.id ASC LIMIT ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Product> searchByName(String query) throws Exception {
        String sql = "SELECT p.id, p.shop_id, s.name AS shop_name, s.rating AS shop_rating, p.name, p.description, p.base_price, p.active " +
                "FROM products p LEFT JOIN shops s ON p.shop_id = s.id " +
                "WHERE p.active = 1 AND (p.name LIKE ? OR p.description LIKE ?) " +
                "ORDER BY p.id ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }
}
