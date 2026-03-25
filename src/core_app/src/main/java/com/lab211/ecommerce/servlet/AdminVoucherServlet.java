package com.lab211.ecommerce.servlet;

import com.lab211.ecommerce.dao.ProductDAO;
import com.lab211.ecommerce.dao.VoucherDAO;
import com.lab211.ecommerce.model.Product;
import com.lab211.ecommerce.model.User;
import com.lab211.ecommerce.model.Voucher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "AdminVoucherServlet", urlPatterns = {"/admin/vouchers"})
public class AdminVoucherServlet extends HttpServlet {
    private final VoucherDAO voucherDAO = new VoucherDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req.getSession(false))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        loadData(req);
        req.getRequestDispatcher("/WEB-INF/views/admin_vouchers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req.getSession(false))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = req.getParameter("action");
        try {
            Voucher voucher = buildVoucher(req);
            if (voucherDAO.codeExistsForOtherVoucher(voucher.getCode(), "update".equals(action) ? voucher.getId() : 0)) {
                throw new IllegalArgumentException("Voucher code already exists");
            }
            if ("create".equals(action)) {
                voucherDAO.create(voucher);
            } else if ("update".equals(action)) {
                voucherDAO.update(voucher);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
            return;
        } catch (Exception ex) {
            req.setAttribute("error", "Voucher action failed: " + ex.getMessage());
            loadData(req);
            req.getRequestDispatcher("/WEB-INF/views/admin_vouchers.jsp").forward(req, resp);
        }
    }

    private void loadData(HttpServletRequest req) throws ServletException {
        try {
            List<Voucher> vouchers = voucherDAO.findAll();
            List<Product> products = productDAO.findAll(false);
            req.setAttribute("vouchers", vouchers);
            req.setAttribute("products", products);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private Voucher buildVoucher(HttpServletRequest req) {
        Voucher voucher = new Voucher();
        voucher.setId(parseInt(req.getParameter("id")));
        voucher.setCode(trim(req.getParameter("code")).toUpperCase());
        int productId = parseInt(req.getParameter("productId"));
        voucher.setProductId(productId > 0 ? productId : null);
        voucher.setDiscountType("FIXED".equalsIgnoreCase(req.getParameter("discountType")) ? "FIXED" : "PERCENT");
        voucher.setDiscountValue(parseDouble(req.getParameter("discountValue")));
        voucher.setMaxDiscount(parseDouble(req.getParameter("maxDiscount")));
        voucher.setMinOrder(parseDouble(req.getParameter("minOrder")));
        voucher.setStartAt(parseDateTime(req.getParameter("startAt")));
        voucher.setEndAt(parseDateTime(req.getParameter("endAt")));
        voucher.setActive("on".equals(req.getParameter("active")) || "create".equals(req.getParameter("action")));
        return voucher;
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        Object user = session.getAttribute("user");
        return user instanceof User && "ADMIN".equals(((User) user).getRole());
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String raw) {
        try { return Integer.parseInt(raw); } catch (Exception ex) { return 0; }
    }

    private double parseDouble(String raw) {
        try { return Double.parseDouble(raw); } catch (Exception ex) { return 0; }
    }

    private LocalDateTime parseDateTime(String raw) {
        try {
            return raw == null || raw.trim().isEmpty() ? null : LocalDateTime.parse(raw.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
