package com.lab211.ecommerce.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;

        boolean isStatic = uri.contains("/assets/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg");
        boolean isAuthPage = uri.endsWith("/login") || uri.endsWith("/register") || uri.contains("/seller/register");
        boolean isApi = uri.contains("/api/");
        boolean isPublicPage = path.equals("/") || path.equals("") ||
                path.startsWith("/home") ||
                path.startsWith("/products") ||
                path.startsWith("/product") ||
                path.equals("/favicon.ico");

        if (isStatic || isAuthPage || isApi || isPublicPage) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
