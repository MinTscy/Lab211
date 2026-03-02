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
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        int variantId = parseInt(req.getParameter("variantId"));
        int quantity = parseInt(req.getParameter("quantity"));

        HttpSession session = req.getSession(true);
        Map<Integer, CartItem> cart = getCart(session);

        try {
            if ("add".equals(action)) {
                ProductVariant variant = variantDAO.findById(variantId);
                if (variant == null) {
                    throw new IllegalArgumentException("Variant not found");
                }
                Product product = productDAO.findById(variant.getProductId());
                CartItem item = cart.getOrDefault(variantId, new CartItem());
                item.setVariantId(variantId);
                item.setProductId(variant.getProductId());
                item.setProductName(product != null ? product.getName() : "Product");
                item.setColor(variant.getColor());
                item.setSize(variant.getSize());
                double price = (product != null ? product.getBasePrice() : 0) + variant.getPriceDelta();
                item.setUnitPrice(price);
                item.setQuantity(item.getQuantity() + Math.max(quantity, 1));
                cart.put(variantId, item);
            } else if ("update".equals(action)) {
                CartItem item = cart.get(variantId);
                if (item != null) {
                    if (quantity <= 0) {
                        cart.remove(variantId);
                    } else {
                        item.setQuantity(quantity);
                    }
                }
            } else if ("remove".equals(action)) {
                cart.remove(variantId);
            }
        } catch (Exception ex) {
            req.setAttribute("error", "Cart update failed: " + ex.getMessage());
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
