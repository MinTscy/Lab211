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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();
    private final ShopDAO shopDAO = new ShopDAO();
    private final UserDAO userDAO = new UserDAO();

    public int placeOrder(User user, String voucherCode, List<VariantRequest> requests) throws Exception {
        OrderSummary summary = previewOrder(user, voucherCode, requests);
        return orderDAO.createOrder(user.getId(), summary.getTotal(), summary.getDiscount(), summary.getFinalAmount(), summary.getItems());
    }

    public OrderSummary previewOrder(User user, String voucherCode, List<VariantRequest> requests) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User session missing");
        }
        if (userDAO.findById(user.getId()) == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<CartItem> items = new ArrayList<>();
        double total = 0;
        double eligibleSubtotal = 0;
        Set<Integer> productIds = new LinkedHashSet<>();

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

            double lineTotal = price * item.getQuantity();
            total += lineTotal;
            productIds.add(product.getId());
        }

        double discount = 0;
        Voucher appliedVoucher = null;
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            Voucher v = voucherDAO.findValidByCode(voucherCode.trim());
            if (v == null) {
                throw new IllegalArgumentException("Voucher is invalid or expired");
            }

            if (v.getProductId() == null) {
                eligibleSubtotal = total;
            } else if (productIds.contains(v.getProductId())) {
                for (CartItem item : items) {
                    if (item.getProductId() == v.getProductId()) {
                        eligibleSubtotal += item.getUnitPrice() * item.getQuantity();
                    }
                }
            } else {
                throw new IllegalArgumentException("Voucher does not apply to products in your cart");
            }

            if (eligibleSubtotal >= v.getMinOrder()) {
                if ("PERCENT".equalsIgnoreCase(v.getDiscountType())) {
                    discount = eligibleSubtotal * v.getDiscountValue() / 100.0;
                } else {
                    discount = v.getDiscountValue();
                }
                if (v.getMaxDiscount() > 0) {
                    discount = Math.min(discount, v.getMaxDiscount());
                }
                appliedVoucher = v;
            } else {
                throw new IllegalArgumentException("Voucher requires minimum order of " + v.getMinOrder());
            }
        }

        double finalAmount = Math.max(total - discount, 0);
        OrderSummary summary = new OrderSummary();
        summary.setItems(items);
        summary.setTotal(total);
        summary.setDiscount(discount);
        summary.setFinalAmount(finalAmount);
        summary.setEligibleSubtotal(eligibleSubtotal);
        summary.setAppliedVoucher(appliedVoucher);
        return summary;
    }

    public static class VariantRequest {
        public int variantId;
        public int quantity;
    }

    public static class OrderSummary {
        private List<CartItem> items;
        private double total;
        private double discount;
        private double finalAmount;
        private double eligibleSubtotal;
        private Voucher appliedVoucher;

        public List<CartItem> getItems() { return items; }
        public void setItems(List<CartItem> items) { this.items = items; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
        public double getFinalAmount() { return finalAmount; }
        public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
        public double getEligibleSubtotal() { return eligibleSubtotal; }
        public void setEligibleSubtotal(double eligibleSubtotal) { this.eligibleSubtotal = eligibleSubtotal; }
        public Voucher getAppliedVoucher() { return appliedVoucher; }
        public void setAppliedVoucher(Voucher appliedVoucher) { this.appliedVoucher = appliedVoucher; }
    }
}
