package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.service.OrderService;
import com.lab211.ecommerce.service.OrderService.VariantRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Object u = session != null ? session.getAttribute("user") : null;
        if (!(u instanceof User)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String voucherCode = req.getParameter("voucher");

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
        Map<Integer, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
        return cart;
    }
}