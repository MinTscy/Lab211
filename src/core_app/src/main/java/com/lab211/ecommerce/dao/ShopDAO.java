package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.Shop;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO {
    public List<Shop> findAllActive() throws Exception {
        String sql = "SELECT id, name, owner_user_id, rating, active, created_at FROM shops WHERE active = 1";
        List<Shop> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Shop findByOwner(int ownerId) throws Exception {
        String sql = "SELECT id, name, owner_user_id, rating, active, created_at FROM shops WHERE owner_user_id = ? LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Shop findById(int id) throws Exception {
        String sql = "SELECT id, name, owner_user_id, rating, active, created_at FROM shops WHERE id = ? LIMIT 1";
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

    public int create(Shop s) throws Exception {
        String sql = "INSERT INTO shops(name, owner_user_id, rating, active) VALUES(?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setInt(2, s.getOwnerUserId());
            ps.setDouble(3, s.getRating());
            ps.setBoolean(4, s.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Shop map(ResultSet rs) throws Exception {
        Shop s = new Shop();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setOwnerUserId(rs.getInt("owner_user_id"));
        s.setRating(rs.getDouble("rating"));
        s.setActive(rs.getBoolean("active"));
        if (rs.getTimestamp("created_at") != null) {
            s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return s;
    }

}

}


