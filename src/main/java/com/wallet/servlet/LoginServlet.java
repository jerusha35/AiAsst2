package com.wallet.servlet;

import com.wallet.dao.UserDAO;
import com.wallet.dao.WalletDAO;
import com.wallet.model.User;
import com.wallet.model.Wallet;
import com.wallet.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide both email and password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        try {
            User user = userDAO.findByEmail(email);

            if (user == null || !PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                request.setAttribute("errorMessage", "Your account is not active. Please contact support.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Fetch user's wallet
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            if (wallet == null) {
                request.setAttribute("errorMessage", "No active wallet found for this account.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // Create fresh session
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedUser", user);
            session.setAttribute("loggedWallet", wallet);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            request.setAttribute("errorMessage", "A system error occurred during login. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
