package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.ReviewDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.dao.VoucherDAO;
import com.lab211.ecommerce.dao.OrderDAO;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductReview;
import com.lab211.ecommerce.model.ProductVariant;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.model.Voucher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseInt(req.getParameter("id"));
        if (id <= 0) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }
        try {
            Product product = productDAO.findById(id);
            if (product == null) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }
            List<ProductVariant> variants = variantDAO.findByProduct(id);
            List<Voucher> vouchers = voucherDAO.findActiveByProduct(id);
            List<ProductReview> reviews = reviewDAO.findByProduct(id);
            req.setAttribute("product", product);
            req.setAttribute("variants", variants);
            req.setAttribute("vouchers", vouchers);
            req.setAttribute("reviews", reviews);
            bindReviewState(req, id);
        } catch (Exception ex) {
            req.setAttribute("error", "Load product failed: " + ex.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/product_detail.jsp").forward(req, resp);
    }

    private void bindReviewState(HttpServletRequest req, int productId) throws Exception {
        HttpSession session = req.getSession(false);
        Object userObj = session != null ? session.getAttribute("user") : null;
        if (!(userObj instanceof User)) {
            return;
        }
        User user = (User) userObj;
        boolean canReview = orderDAO.hasPurchasedProduct(user.getId(), productId);
        ProductReview myReview = reviewDAO.findByUserAndProduct(user.getId(), productId);
        req.setAttribute("canReview", canReview);
        req.setAttribute("myReview", myReview);
    }

    private int parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception ex) {
            return 0;
        }
    }
}
