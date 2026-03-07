package com.lab211.ecommerce.service;

import com.lab211.ecommerce.dao.OrderDAO;
import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.ShopDAO;
import com.lab211.ecommerce.dao.UserDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.dao.VoucherDAO;
import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;
import com.lab211.ecommerce.model.Shop;
import com.lab211.ecommerce.model.Voucher;
import com.lab211.ecommerce.model.User;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final UserDAO userDAO = new UserDAO();

    public int placeOrder(User user, String voucherCode, List<VariantRequest> requests) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User session missing");
        }
        if (userDAO.findById(user.getId()) == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<CartItem> items = new ArrayList<>();
        double total = 0;

        for (VariantRequest req : requests) {
            ProductVariant variant = variantDAO.findById(req.variantId);
            if (variant == null) throw new IllegalArgumentException("Variant not found: " + req.variantId);
            if (variant.getStock() <= 0) throw new IllegalArgumentException("Variant out of stock: " + req.variantId);
            Product product = productDAO.findById(variant.getProductId());
            if (product == null) throw new IllegalStateException("Product missing for variant " + req.variantId);
            if (!product.isActive()) throw new IllegalArgumentException("Product is inactive: " + product.getName());
            Shop shop = shopDAO.findById(product.getShopId());
            if (shop == null || !shop.isActive()) {
                throw new IllegalArgumentException("Shop is inactive or missing for product: " + product.getName());
            }

            double price = product.getBasePrice() + variant.getPriceDelta();
            CartItem item = new CartItem();
            item.setVariantId(req.variantId);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setColor(variant.getColor());
            item.setSize(variant.getSize());
            item.setUnitPrice(price);
            int requestedQty = Math.max(1, req.quantity);
            if (requestedQty > variant.getStock()) {
                throw new IllegalArgumentException("Requested quantity exceeds stock for " + product.getName());
            }
            item.setQuantity(requestedQty);
            items.add(item);

            total += price * item.getQuantity();
        }

        double discount = 0;
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            Voucher v = voucherDAO.findValidByCode(voucherCode.trim());
            if (v != null && total >= v.getMinOrder()) {
                if ("PERCENT".equalsIgnoreCase(v.getDiscountType())) {
                    discount = total * v.getDiscountValue() / 100.0;
                } else {
                    discount = v.getDiscountValue();
                }
                if (v.getMaxDiscount() > 0) {
                    discount = Math.min(discount, v.getMaxDiscount());
                }
            }
        }

        double finalAmount = Math.max(total - discount, 0);
        return orderDAO.createOrder(user.getId(), total, discount, finalAmount, items);
    }

    public static class VariantRequest {
        public int variantId;
        public int quantity;
    }
}
