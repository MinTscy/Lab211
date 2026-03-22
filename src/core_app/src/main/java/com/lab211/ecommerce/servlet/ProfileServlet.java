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

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getSessionUser(req, resp);
        if (user == null) return;
        req.setAttribute("profileUser", user);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = getSessionUser(req, resp);
        if (sessionUser == null) return;

        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String address = trim(req.getParameter("address"));
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");

        try {
            if (name.isEmpty() || email.isEmpty()) {
                throw new IllegalArgumentException("Name and email are required");
            }
            if (userDAO.emailExistsForOtherUser(email, sessionUser.getId())) {
                throw new IllegalArgumentException("Email already exists");
            }
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (currentPassword == null || !sessionUser.getPasswordHash().equals(PasswordUtil.sha256(currentPassword))) {
                    throw new IllegalArgumentException("Current password is incorrect");
                }
                sessionUser.setPasswordHash(PasswordUtil.sha256(newPassword.trim()));
            }

            sessionUser.setName(name);
            sessionUser.setEmail(email);
            sessionUser.setPhone(phone);
            sessionUser.setAddress(address);
            userDAO.updateProfile(sessionUser);

            User freshUser = userDAO.findById(sessionUser.getId());
            req.getSession().setAttribute("user", freshUser);
            req.setAttribute("profileUser", freshUser);
            req.setAttribute("success", "Profile updated successfully");
        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("profileUser", sessionUser);
        }

        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    private User getSessionUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Object user = session != null ? session.getAttribute("user") : null;
        if (!(user instanceof User)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (User) user;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
