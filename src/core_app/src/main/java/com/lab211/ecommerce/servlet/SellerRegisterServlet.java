package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ShopDAO;
import com.lab211.ecommerce.dao.UserDAO;
import com.lab211.ecommerce.model.Shop;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SellerRegisterServlet", urlPatterns = {"/seller/register"})
public class SellerRegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final ShopDAO shopDAO = new ShopDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/seller_register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String shopName = req.getParameter("shopName");

        try {
            if (userDAO.findByEmail(email) != null) {
                req.setAttribute("error", "Email already exists");
                req.getRequestDispatcher("/WEB-INF/views/seller_register.jsp").forward(req, resp);
                return;
            }
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.sha256(password));
            user.setRole("SELLER");
            int userId = userDAO.create(user);

            Shop shop = new Shop();
            shop.setName(shopName);
            shop.setOwnerUserId(userId);
            shop.setRating(4.5);
            shop.setActive(true);
            shopDAO.create(shop);

            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } catch (Exception ex) {
            req.setAttribute("error", "Registration failed: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/seller_register.jsp").forward(req, resp);
        }
    }
}
