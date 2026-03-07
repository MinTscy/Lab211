package com.lab211.ecommerce.servlet;

<<<<<<< HEAD
import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.service.OrderService;
import com.lab211.ecommerce.service.OrderService.VariantRequest;
=======
import com.lab211.ecommerce.dao.OrderDAO;
import com.lab211.ecommerce.dao.VoucherDAO;
import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.model.Voucher;
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.LinkedHashMap;
=======
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
import java.util.List;
import java.util.Map;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {
<<<<<<< HEAD
    private final OrderService orderService = new OrderService();
=======
    private final OrderDAO orderDAO = new OrderDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
<<<<<<< HEAD
        Object u = session != null ? session.getAttribute("user") : null;
        if (!(u instanceof User)) {
=======
        if (session == null || session.getAttribute("user") == null) {
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String voucherCode = req.getParameter("voucher");
<<<<<<< HEAD

        try {
            User user = (User) u;
            List<VariantRequest> requests = new ArrayList<>();
            for (CartItem item : cart.values()) {
                VariantRequest vr = new VariantRequest();
                vr.variantId = item.getVariantId();
                vr.quantity = item.getQuantity();
                requests.add(vr);
            }
            int orderId = orderService.placeOrder(user, voucherCode, requests);
            cart.clear();
            req.setAttribute("success", "Order placed successfully. Order ID: " + orderId);
        } catch (IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
=======
        double total = cart.values().stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
        double discount = 0;

        try {
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
            User user = (User) session.getAttribute("user");
            List<CartItem> items = new ArrayList<>(cart.values());
            int orderId = orderDAO.createOrder(user.getId(), total, discount, finalAmount, items);
            cart.clear();
            req.setAttribute("success", "Order placed successfully. Order ID: " + orderId);
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
        } catch (Exception ex) {
            req.setAttribute("error", "Checkout failed: " + ex.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpSession session) {
        Object obj = session.getAttribute("cart");
        if (obj instanceof Map) {
            return (Map<Integer, CartItem>) obj;
        }
<<<<<<< HEAD
        Map<Integer, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
=======
        return new java.util.LinkedHashMap<>();
>>>>>>> 74c45db33ad1038a823f96d3912f1d93cb62d95d
    }
}
