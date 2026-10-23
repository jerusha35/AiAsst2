package com.wallet.servlet;

import com.wallet.dao.UserDAO;
import com.wallet.dao.WalletDAO;
import com.wallet.model.User;
import com.wallet.model.Wallet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("loggedUser");

        try {
            User user = userDAO.findById(sessionUser.getUserId());
            Wallet wallet = walletDAO.findByUserId(user.getUserId());

            request.setAttribute("user", user);
            request.setAttribute("wallet", wallet);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading profile.");
            request.getRequestDispatcher("/dashboard").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("loggedUser");

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name cannot be blank.");
            doGet(request, response);
            return;
        }

        try {
            boolean updated = userDAO.updateProfile(sessionUser.getUserId(), fullName.trim(), phone);
            if (updated) {
                User freshUser = userDAO.findById(sessionUser.getUserId());
                session.setAttribute("loggedUser", freshUser);
                request.setAttribute("successMessage", "Profile details updated successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to update profile.");
            }
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            doGet(request, response);
        }
    }
}
