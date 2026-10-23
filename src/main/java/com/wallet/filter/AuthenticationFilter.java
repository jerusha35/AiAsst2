package com.wallet.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Filter to enforce authentication on protected pages and protect against CSRF attacks.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/",
            "/index.jsp",
            "/login",
            "/login.jsp",
            "/register",
            "/register.jsp",
            "/logout"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI().substring(contextPath.length());

        // Allow static assets (css, js, images, fonts)
        if (uri.startsWith("/assets/") || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".ico")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(true);

        // Ensure CSRF token is present in session
        if (session.getAttribute("csrfToken") == null) {
            byte[] bytes = new byte[24];
            new SecureRandom().nextBytes(bytes);
            session.setAttribute("csrfToken", Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        }

        // Check if path is public
        boolean isPublic = PUBLIC_PATHS.contains(uri);

        Object userObj = session.getAttribute("loggedUser");

        if (!isPublic && userObj == null) {
            // User not authenticated, redirect to login page with return message
            session.setAttribute("errorMessage", "Please log in to access this page.");
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // CSRF verification for POST requests on sensitive actions
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String formCsrf = request.getParameter("csrfToken");
            String sessionCsrf = (String) session.getAttribute("csrfToken");

            // Allow login and register forms without strict CSRF or check if token matches
            if (formCsrf != null && !formCsrf.equals(sessionCsrf)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF security token.");
                return;
            }
        }

        // Prevent browser caching of sensitive authenticated pages
        if (userObj != null) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }

        chain.doFilter(req, res);
    }
}
