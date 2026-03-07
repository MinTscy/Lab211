package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private final VariantDAO variantDAO = new VariantDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object flash = req.getSession(false) != null ? req.getSession(false).getAttribute("cartMessage") : null;
        if (flash instanceof String) {
            req.setAttribute("error", flash);
            req.getSession(false).removeAttribute("cartMessage");
        }
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int variantId = parseInt(req.getParameter("variantId"));
        int quantity = parseInt(req.getParameter("quantity"));

        HttpSession session = req.getSession(true);
        Map<Integer, CartItem> cart = getCart(session);
        String message = null;

        try {
            if ("add".equals(action)) {
                ProductVariant variant = variantDAO.findById(variantId);
                if (variant == null) throw new IllegalArgumentException("Variant not found");
                if (variant.getStock() <= 0) throw new IllegalArgumentException("Item is out of stock");
                Product product = productDAO.findById(variant.getProductId());
                if (product == null || !product.isActive()) throw new IllegalArgumentException("Product is inactive or missing");
                CartItem item = cart.getOrDefault(variantId, new CartItem());
                item.setVariantId(variantId);
                item.setProductId(variant.getProductId());
                item.setProductName(product != null ? product.getName() : "Product");
                item.setColor(variant.getColor());
                item.setSize(variant.getSize());
                double price = (product != null ? product.getBasePrice() : 0) + variant.getPriceDelta();
                item.setUnitPrice(price);
                int requested = item.getQuantity() + Math.max(quantity, 1);
                if (requested > variant.getStock()) {
                    requested = variant.getStock();
                    message = "Quantity capped to available stock.";
                }
                item.setQuantity(requested);
                cart.put(variantId, item);
            } else if ("update".equals(action)) {
                CartItem item = cart.get(variantId);
                if (item != null) {
                    if (quantity <= 0) {
                        cart.remove(variantId);
                    } else {
                        ProductVariant variant = variantDAO.findById(variantId);
                        if (variant == null) throw new IllegalArgumentException("Variant not found");
                        int capped = Math.min(quantity, variant.getStock());
                        if (capped < quantity) {
                            message = "Quantity capped to available stock.";
                        }
                        item.setQuantity(capped);
                    }
                }
            } else if ("remove".equals(action)) {
                cart.remove(variantId);
            }
        } catch (Exception ex) {
            message = "Cart update failed: " + ex.getMessage();
        }

        if (message != null) {
            session.setAttribute("cartMessage", message);
        }
        resp.sendRedirect(req.getContextPath() + "/cart");
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

    private int parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception ex) {
            return 0;
        }
    }
}
