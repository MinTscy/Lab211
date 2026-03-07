package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.UserDAO;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            if (userDAO.findByEmail(email) != null) {
                req.setAttribute("error", "Email already exists");
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.sha256(password));
            user.setRole("CUSTOMER");
            userDAO.create(user);
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception ex) {
            req.setAttribute("error", "Register failed: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
