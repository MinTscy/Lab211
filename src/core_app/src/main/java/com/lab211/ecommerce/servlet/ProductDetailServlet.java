package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.VariantDAO;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.ProductVariant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();

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
            req.setAttribute("product", product);
            req.setAttribute("variants", variants);
        } catch (Exception ex) {
            req.setAttribute("error", "Load product failed: " + ex.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/product_detail.jsp").forward(req, resp);
    }

    private int parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception ex) {
            return 0;
        }
    }
}
