package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;
import com.lab211.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminProductServlet", urlPatterns = {"/admin/products"})
public class AdminProductServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req.getSession(false))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            List<Product> products = productDAO.findAll(true);
            req.setAttribute("products", products);
        } catch (Exception ex) {
            req.setAttribute("error", "Load products failed: " + ex.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/admin_products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req.getSession(false))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                Product p = new Product();
                p.setShopId(parseInt(req.getParameter("shopId")));
                if (p.getShopId() <= 0) {
                    p.setShopId(1);
                }
                p.setName(req.getParameter("name"));
                p.setDescription(req.getParameter("description"));
                p.setBasePrice(parseDouble(req.getParameter("basePrice")));
                p.setActive(true);
                productDAO.create(p);
            } else if ("update".equals(action)) {
                Product p = productDAO.findById(parseInt(req.getParameter("id")));
                if (p != null) {
                    int shopId = parseInt(req.getParameter("shopId"));
                    if (shopId > 0) {
                        p.setShopId(shopId);
                    }
                    p.setName(req.getParameter("name"));
                    p.setDescription(req.getParameter("description"));
                    p.setBasePrice(parseDouble(req.getParameter("basePrice")));
                    p.setActive("on".equals(req.getParameter("active")));
                    productDAO.update(p);
                }
            } else if ("addVariant".equals(action)) {
                ProductVariant v = new ProductVariant();
                v.setProductId(parseInt(req.getParameter("productId")));
                v.setColor(req.getParameter("color"));
                v.setSize(req.getParameter("size"));
                v.setStock(parseInt(req.getParameter("stock")));
                v.setPriceDelta(parseDouble(req.getParameter("priceDelta")));
                variantDAO.create(v);
            }
        } catch (Exception ex) {
            req.setAttribute("error", "Admin action failed: " + ex.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        Object user = session.getAttribute("user");
        return user instanceof User && "ADMIN".equals(((User) user).getRole());
    }

    private int parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception ex) {
            return 0;
        }
    }

    private double parseDouble(String raw) {
        try {
            return Double.parseDouble(raw);
        } catch (Exception ex) {
            return 0;
        }
    }
}
