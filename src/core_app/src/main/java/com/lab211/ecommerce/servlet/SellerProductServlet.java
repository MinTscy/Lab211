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

@WebServlet(name = "SellerProductServlet", urlPatterns = {"/seller/product/new"})
public class SellerProductServlet extends HttpServlet {
    private final ShopDAO shopDAO = new ShopDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getSeller(req, resp);
        if (seller == null) return;
        try {
            Shop shop = shopDAO.findByOwner(seller.getId());
            if (shop == null) {
                resp.sendRedirect(req.getContextPath() + "/seller/register");
                return;
            }
            req.setAttribute("shop", shop);
            req.getRequestDispatcher("/WEB-INF/views/seller_product.jsp").forward(req, resp);
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getSeller(req, resp);
        if (seller == null) return;
        try {
            Shop shop = shopDAO.findByOwner(seller.getId());
            if (shop == null) {
                resp.sendRedirect(req.getContextPath() + "/seller/register");
                return;
            }
            Product p = new Product();
            p.setShopId(shop.getId());
            p.setName(req.getParameter("name"));
            p.setDescription(req.getParameter("description"));
            p.setImageUrl(req.getParameter("imageUrl"));
            p.setBasePrice(parseDouble(req.getParameter("basePrice")));
            p.setActive(true);
            int productId = productDAO.create(p);

            ProductVariant v = new ProductVariant();
            v.setProductId(productId);
            v.setColor(req.getParameter("color"));
            v.setSize(req.getParameter("size"));
            v.setStock(parseInt(req.getParameter("stock")));
            v.setPriceDelta(parseDouble(req.getParameter("priceDelta")));
            variantDAO.create(v);

            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/seller_product.jsp").forward(req, resp);
        }
    }

    private User getSeller(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) { resp.sendRedirect(req.getContextPath() + "/login"); return null; }
        Object u = session.getAttribute("user");
        if (!(u instanceof User) || !"SELLER".equals(((User) u).getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return (User) u;
    }

    private int parseInt(String raw) { try { return Integer.parseInt(raw); } catch (Exception e) { return 0; } }
    private double parseDouble(String raw) { try { return Double.parseDouble(raw); } catch (Exception e) { return 0; } }
}
