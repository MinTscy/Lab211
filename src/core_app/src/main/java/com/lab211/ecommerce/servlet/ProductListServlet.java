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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductListServlet", urlPatterns = {"/products"})
public class ProductListServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final VariantDAO variantDAO = new VariantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String query = req.getParameter("q");
            List<Product> products;
            if (query != null && !query.trim().isEmpty()) {
                products = productDAO.searchByName(query.trim());
                req.setAttribute("searchQuery", query.trim());
                req.setAttribute("searchCount", products.size());
            } else {
                products = productDAO.findFeaturedByShopRating(12);
            }
            Map<Integer, List<ProductVariant>> variantsByProduct = new HashMap<>();
            for (Product p : products) {
                variantsByProduct.put(p.getId(), variantDAO.findByProduct(p.getId()));
            }
            req.setAttribute("products", products);
            req.setAttribute("variantsByProduct", variantsByProduct);
        } catch (Exception ex) {
            req.setAttribute("error", "Load products failed: " + ex.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
    }
}
