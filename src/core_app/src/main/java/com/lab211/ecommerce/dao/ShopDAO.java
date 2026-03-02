package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.Shop;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
