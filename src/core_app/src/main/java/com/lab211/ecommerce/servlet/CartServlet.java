package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.model.CartItem;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;
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

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private final VariantDAO variantDAO = new VariantDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object flash = req.getSession(false) != null ? req.getSession(false).getAttribute("cartMessage") : null;

        HttpSession session = req.getSession(false);
        Object flash = session != null ? session.getAttribute("cartMessage") : null;
        if (flash instanceof String) {
            req.setAttribute("error", flash);
            session.removeAttribute("cartMessage");
        }

        if (session != null) {
            Object voucherFlash = session.getAttribute("cartVoucherMessage");
            if (voucherFlash instanceof String) {
                req.setAttribute("voucherMessage", voucherFlash);
                session.removeAttribute("cartVoucherMessage");
            }
            populateCartSummary(req, session);
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

                if (variant == null) {
                    throw new IllegalArgumentException("Variant not found");
                }
                if (variant.getStock() <= 0) {
                    throw new IllegalArgumentException("Item is out of stock");
                }

                Product product = productDAO.findById(variant.getProductId());
                if (product == null || !product.isActive()) {
                    throw new IllegalArgumentException("Product is inactive or missing");
                }


                if (variant == null) throw new IllegalArgumentException("Variant not found");
                if (variant.getStock() <= 0) throw new IllegalArgumentException("Item is out of stock");
                Product product = productDAO.findById(variant.getProductId());
                if (product == null || !product.isActive()) throw new IllegalArgumentException("Product is inactive or missing");

                CartItem item = cart.getOrDefault(variantId, new CartItem());
                item.setVariantId(variantId);
                item.setProductId(variant.getProductId());
                item.setProductName(product.getName());
                item.setColor(variant.getColor());
                item.setSize(variant.getSize());

                double price = product.getBasePrice() + variant.getPriceDelta();
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
                        if (variant == null) {
                            throw new IllegalArgumentException("Variant not found");
                        }

                        int capped = Math.min(quantity, variant.getStock());
                        if (capped < quantity) {
                            message = "Quantity capped to available stock.";
                        }
                        item.setQuantity(capped);
                    }
                }

            } else if ("remove".equals(action)) {
                cart.remove(variantId);
            } else if ("applyVoucher".equals(action)) {
                String voucherCode = req.getParameter("voucher");
                voucherCode = voucherCode == null ? "" : voucherCode.trim();
                if (voucherCode.isEmpty()) {
                    session.removeAttribute("cartVoucherCode");
                    session.setAttribute("cartVoucherMessage", "Voucher has been cleared.");
                } else {
                    Object userObj = session.getAttribute("user");
                    if (!(userObj instanceof User)) {
                        throw new IllegalArgumentException("Please log in before applying a voucher.");
                    }
                    orderService.previewOrder((User) userObj, voucherCode, toRequests(cart));
                    session.setAttribute("cartVoucherCode", voucherCode);
                    session.setAttribute("cartVoucherMessage", "Voucher applied successfully.");
                }
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


    private void populateCartSummary(HttpServletRequest req, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        req.setAttribute("cartVoucherCode", session.getAttribute("cartVoucherCode"));
        if (cart.isEmpty()) {
            session.removeAttribute("cartVoucherCode");
            return;
        }

        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        req.setAttribute("cartTotal", total);

        Object userObj = session.getAttribute("user");
        if (!(userObj instanceof User)) {
            return;
        }

        String voucherCode = (String) session.getAttribute("cartVoucherCode");
        try {
            req.setAttribute("cartSummary", orderService.previewOrder((User) userObj, voucherCode, toRequests(cart)));
        } catch (Exception ex) {
            req.setAttribute("voucherError", ex.getMessage());
        }
    }

    private List<VariantRequest> toRequests(Map<Integer, CartItem> cart) {
        List<VariantRequest> requests = new ArrayList<>();
        for (CartItem item : cart.values()) {
            VariantRequest request = new VariantRequest();
            request.variantId = item.getVariantId();
            request.quantity = item.getQuantity();
            requests.add(request);
        }
        return requests;
    }
}


