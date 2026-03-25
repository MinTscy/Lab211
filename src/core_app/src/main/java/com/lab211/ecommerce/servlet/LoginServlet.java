package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.UserDAO;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        email = email == null ? "" : email.trim();
        password = password == null ? "" : password.trim();

        try {
            User user = userDAO.findByEmail(email);
            if (user != null && user.getPasswordHash().equals(PasswordUtil.sha256(password))) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);

                String target = "/products";
                if ("SELLER".equals(user.getRole())) {
                    target = "/seller/dashboard";
                } else if ("ADMIN".equals(user.getRole())) {
                    target = "/admin/products";
                }

                resp.sendRedirect(req.getContextPath() + target);
                return;
            }
            req.setAttribute("error", "Invalid email or password");
        } catch (Exception ex) {
            req.setAttribute("error", "Login failed: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }


        req.setAttribute("error", "Invalid email or password");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
=======
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
