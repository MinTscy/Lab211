package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.OrderDAO;
import com.lab211.ecommerce.dao.ReviewDAO;
import com.lab211.ecommerce.model.ProductReview;
import com.lab211.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review"})
public class ReviewServlet extends HttpServlet {
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Object userObj = session != null ? session.getAttribute("user") : null;
        if (!(userObj instanceof User)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) userObj;
        int productId = parseInt(req.getParameter("productId"));
        int rating = Math.max(1, Math.min(5, parseInt(req.getParameter("rating"))));
        String comment = req.getParameter("comment") != null ? req.getParameter("comment").trim() : "";

        try {
            if (productId <= 0) {
                throw new IllegalArgumentException("Invalid product");
            }
            if (comment.isEmpty()) {
                throw new IllegalArgumentException("Comment is required");
            }
            if (!orderDAO.hasPurchasedProduct(user.getId(), productId)) {
                throw new IllegalArgumentException("You can only review products you have purchased");
            }

            ProductReview existing = reviewDAO.findByUserAndProduct(user.getId(), productId);
            if (existing == null) {
                ProductReview review = new ProductReview();
                review.setProductId(productId);
                review.setUserId(user.getId());
                review.setRating(rating);
                review.setComment(comment);
                reviewDAO.create(review);
            } else {
                existing.setRating(rating);
                existing.setComment(comment);
                reviewDAO.update(existing);
            }
            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&review=success");
            return;
        } catch (Exception ex) {
            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "&reviewError=" + encode(ex.getMessage()));
        }
    }

    private int parseInt(String raw) {
        try { return Integer.parseInt(raw); } catch (Exception ex) { return 0; }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
