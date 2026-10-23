package com.wallet.servlet;

import com.wallet.dao.UserDAO;
import com.wallet.model.User;
import com.wallet.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = (User) session.getAttribute("loggedUser");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null ||
            currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "All password fields are required.");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.getSession().setAttribute("errorMessage", "New password and confirmation do not match.");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if (newPassword.length() < 6) {
            request.getSession().setAttribute("errorMessage", "New password must be at least 6 characters.");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        try {
            User user = userDAO.findById(sessionUser.getUserId());
            if (!PasswordUtil.checkPassword(currentPassword, user.getPasswordHash())) {
                request.getSession().setAttribute("errorMessage", "Current password is incorrect.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            boolean updated = userDAO.updatePassword(user.getUserId(), newHash);

            if (updated) {
                request.getSession().setAttribute("successMessage", "Password changed successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update password. Try again.");
            }
            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "System error changing password: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
}
