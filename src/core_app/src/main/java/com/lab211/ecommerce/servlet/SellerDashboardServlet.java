package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.ShopDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;
import com.lab211.ecommerce.model.Shop;
import com.lab211.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SellerDashboardServlet", urlPatterns = {"/seller/dashboard"})
public class SellerDashboardServlet extends HttpServlet {
    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getUser(req, resp);
        if (user == null) return;
        try {
            Shop shop = ensureShop(user);
            List<Product> products = productDAO.findByShop(shop.getId());
            req.setAttribute("shop", shop);
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/seller_dashboard.jsp").forward(req, resp);
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getUser(req, resp);
        if (user == null) return;
        try {
            Shop shop = ensureShop(user);
            String action = req.getParameter("action");
            if ("addProduct".equals(action)) {
                Product p = new Product();
                p.setShopId(shop.getId());
                p.setName(req.getParameter("name"));
                p.setDescription(req.getParameter("description"));
                p.setBasePrice(parseDouble(req.getParameter("basePrice")));
                p.setActive(true);
                int productId = productDAO.create(p);
                // Optional first variant
                ProductVariant v = new ProductVariant();
                v.setProductId(productId);
                v.setColor(req.getParameter("color"));
                v.setSize(req.getParameter("size"));
                v.setStock(parseInt(req.getParameter("stock")));
                v.setPriceDelta(parseDouble(req.getParameter("priceDelta")));
                variantDAO.create(v);
            }
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private User getUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) { resp.sendRedirect(req.getContextPath() + "/login"); return null; }
        Object u = session.getAttribute("user");
        if (!(u instanceof User) || !"SELLER".equals(((User) u).getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return (User) u;
    }

    private Shop ensureShop(User user) throws Exception {
        Shop shop = shopDAO.findByOwner(user.getId());
        if (shop != null) return shop;
        Shop s = new Shop();
        s.setName(user.getName() + " Shop");
        s.setOwnerUserId(user.getId());
        s.setRating(4.5);
        s.setActive(true);
        int id = shopDAO.create(s);
        s.setId(id);
        return s;
    }

    private int parseInt(String raw) {
        try { return Integer.parseInt(raw); } catch (Exception e) { return 0; }
    }
    private double parseDouble(String raw) {
        try { return Double.parseDouble(raw); } catch (Exception e) { return 0; }
    }
}
