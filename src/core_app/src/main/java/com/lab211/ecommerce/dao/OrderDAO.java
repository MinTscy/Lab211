package com.lab211.ecommerce.dao;

import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final VariantDAO variantDAO = new VariantDAO();

    public int createOrder(int userId, double total, double discount, double finalAmount, List<CartItem> items) throws Exception {
        String orderSql = "INSERT INTO orders(user_id, total_amount, discount_amount, final_amount, status) VALUES(?,?,?,?,?)";
        String itemSql = "INSERT INTO order_items(order_id, variant_id, quantity, unit_price) VALUES(?,?,?,?)";




        int attempts = 0;
        while (true) {
            attempts++;
            try (Connection conn = DBUtil.getConnection()) {
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                conn.setAutoCommit(false);

                try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    psOrder.setInt(1, userId);
                    psOrder.setDouble(2, total);
                    psOrder.setDouble(3, discount);
                    psOrder.setDouble(4, finalAmount);
                    psOrder.setString(5, "PAID");
                    psOrder.executeUpdate();

                    int orderId;
                    try (ResultSet rs = psOrder.getGeneratedKeys()) {
                        rs.next();
                        orderId = rs.getInt(1);
                    }

                    try (PreparedStatement psItem = conn.prepareStatement(itemSql)) {
                        for (CartItem item : items) {
                            variantDAO.decrementStock(item.getVariantId(), item.getQuantity(), conn);

                            psItem.setInt(1, orderId);
                            psItem.setInt(2, item.getVariantId());
                            psItem.setInt(3, item.getQuantity());
                            psItem.setDouble(4, item.getUnitPrice());
                            psItem.addBatch();
                        }
                        psItem.executeBatch();
                    }

                    conn.commit();
                    return orderId;
                } catch (IllegalStateException stockEx) {
                    conn.rollback();
                    if (attempts < 3) {
                        continue;
                    }
                    throw stockEx;
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
    }

}


    public boolean hasPurchasedProduct(int userId, int productId) throws Exception {
        String sql = "SELECT 1 FROM orders o " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "JOIN product_variants pv ON oi.variant_id = pv.id " +
                "WHERE o.user_id = ? AND pv.product_id = ? AND o.status IN ('PAID', 'SHIPPED', 'COMPLETED') LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Integer> findPurchasedProductIds(int userId) throws Exception {
        String sql = "SELECT DISTINCT pv.product_id FROM orders o " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "JOIN product_variants pv ON oi.variant_id = pv.id " +
                "WHERE o.user_id = ? AND o.status IN ('PAID', 'SHIPPED', 'COMPLETED')";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }
        return ids;
    }
}


