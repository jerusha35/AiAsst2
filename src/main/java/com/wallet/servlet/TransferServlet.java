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
import java.math.BigDecimal;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");

        try {
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            request.setAttribute("wallet", wallet);

            // Optional AJAX receiver lookup check
            String lookupEmail = request.getParameter("lookupEmail");
            if (lookupEmail != null && !lookupEmail.trim().isEmpty()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                User receiver = userDAO.findByEmail(lookupEmail.trim());
                if (receiver != null && !receiver.getUserId().equals(user.getUserId()) && "ACTIVE".equalsIgnoreCase(receiver.getStatus())) {
                    response.getWriter().write("{\"found\": true, \"fullName\": \"" + escapeJson(receiver.getFullName()) + "\"}");
                } else {
                    response.getWriter().write("{\"found\": false}");
                }
                return;
            }

            request.getRequestDispatcher("/transfer.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transfer page.");
            request.getRequestDispatcher("/dashboard").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sender = (User) session.getAttribute("loggedUser");

        String receiverEmail = request.getParameter("receiverEmail");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");

        if (receiverEmail == null || receiverEmail.trim().isEmpty() ||
            amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Receiver email and amount are required.");
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
            return;
        }

        receiverEmail = receiverEmail.trim().toLowerCase();

        // 1. Cannot transfer to oneself
        if (receiverEmail.equalsIgnoreCase(sender.getEmail())) {
            request.setAttribute("errorMessage", "You cannot transfer money to your own wallet.");
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
            return;
        }

        // 2. Validate amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid amount entered.");
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            request.setAttribute("errorMessage", "Transfer amount must be strictly greater than zero.");
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
            return;
        }

        if (amount.scale() > 2) {
            request.setAttribute("errorMessage", "Amount cannot have more than 2 decimal places.");
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
            return;
        }

        try {
            // 3. Find receiver
            User receiver = userDAO.findByEmail(receiverEmail);
            if (receiver == null || !"ACTIVE".equalsIgnoreCase(receiver.getStatus())) {
                request.setAttribute("errorMessage", "Receiver account not found or is not active.");
                forwardBackWithValues(request, response, receiverEmail, amountStr, description);
                return;
            }

            // 4. Find sender and receiver wallets
            Wallet senderWallet = walletDAO.findByUserId(sender.getUserId());
            Wallet receiverWallet = walletDAO.findByUserId(receiver.getUserId());

            if (senderWallet == null || receiverWallet == null) {
                request.setAttribute("errorMessage", "Could not locate sender or receiver wallet.");
                forwardBackWithValues(request, response, receiverEmail, amountStr, description);
                return;
            }

            // 5. Pre-check balance (row lock will verify again inside transaction)
            if (senderWallet.getBalance().compareTo(amount) < 0) {
                request.setAttribute("errorMessage", "Insufficient balance! Your current balance is ₹" + senderWallet.getBalance());
                forwardBackWithValues(request, response, receiverEmail, amountStr, description);
                return;
            }

            // 6. Execute atomic transfer
            String txnRef = walletDAO.transferMoney(senderWallet.getWalletId(), receiverWallet.getWalletId(), amount, description);

            // 7. Update session wallet
            Wallet updatedSenderWallet = walletDAO.findByUserId(sender.getUserId());
            session.setAttribute("loggedWallet", updatedSenderWallet);

            request.getSession().setAttribute("successMessage",
                    "Transfer of ₹" + amount.toPlainString() + " to " + receiver.getFullName() +
                    " (" + receiver.getEmail() + ") was successful! Reference: " + txnRef);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            System.err.println("Transfer error: " + e.getMessage());
            request.setAttribute("errorMessage", "Transfer failed: " + e.getMessage());
            forwardBackWithValues(request, response, receiverEmail, amountStr, description);
        }
    }

    private void forwardBackWithValues(HttpServletRequest request, HttpServletResponse response,
                                      String receiverEmail, String amount, String description)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");
        try {
            request.setAttribute("wallet", walletDAO.findByUserId(user.getUserId()));
        } catch (Exception ignored) {}
        request.setAttribute("receiverEmail", receiverEmail);
        request.setAttribute("amount", amount);
        request.setAttribute("description", description);
        request.getRequestDispatcher("/transfer.jsp").forward(request, response);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "").replace("\r", "");
    }
}
