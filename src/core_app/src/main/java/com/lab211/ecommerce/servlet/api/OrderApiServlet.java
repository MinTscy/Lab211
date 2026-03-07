package com.lab211.ecommerce.servlet.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lab211.ecommerce.service.OrderService;
import com.lab211.ecommerce.service.OrderService.VariantRequest;
import com.lab211.ecommerce.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "OrderApiServlet", urlPatterns = {"/api/orders"})
public class OrderApiServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final OrderService orderService = new OrderService();

    private static class OrderRequest {
        String voucherCode;
        List<VariantRequest> items;
    }

    private static class ApiResponse {
        boolean success;
        String message;
        Integer orderId;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        ApiResponse out = new ApiResponse();
        try (BufferedReader reader = req.getReader(); PrintWriter writer = resp.getWriter()) {
            HttpSession session = req.getSession(false);
            Object u = session != null ? session.getAttribute("user") : null;
            if (!(u instanceof User)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.success = false;
                out.message = "Authentication required";
                writer.write(gson.toJson(out));
                return;
            }
            User user = (User) u;

            OrderRequest body = gson.fromJson(reader, OrderRequest.class);
            if (body == null || body.items == null || body.items.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.success = false;
                out.message = "Invalid payload";
            } else {
                int orderId = orderService.placeOrder(user, body.voucherCode, body.items);
                out.success = true;
                out.orderId = orderId;
                out.message = "Created";
            }
            writer.write(gson.toJson(out));
        } catch (JsonSyntaxException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter writer = resp.getWriter()) {
                out.success = false;
                out.message = "Invalid JSON";
                writer.write(gson.toJson(out));
            }
        } catch (IllegalArgumentException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter writer = resp.getWriter()) {
                out.success = false;
                out.message = ex.getMessage();
                writer.write(gson.toJson(out));
            }
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter writer = resp.getWriter()) {
                out.success = false;
                out.message = ex.getMessage();
                writer.write(gson.toJson(out));
            }
        }
    }
}
