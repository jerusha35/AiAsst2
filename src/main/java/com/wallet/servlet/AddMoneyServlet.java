package com.wallet.servlet;

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

@WebServlet("/add-money")
public class AddMoneyServlet extends HttpServlet {

    private static final BigDecimal MAX_TOPUP_AMOUNT = new BigDecimal("50000.00");
    private final WalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");

        try {
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            request.setAttribute("wallet", wallet);
            request.setAttribute("maxTopup", MAX_TOPUP_AMOUNT);
            request.getRequestDispatcher("/add-money.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Could not load wallet details.");
            request.getRequestDispatcher("/dashboard").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");

        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");

        if (amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please specify an amount to add.");
            doGet(request, response);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid amount format. Please enter numbers only.");
            doGet(request, response);
            return;
        }

        // Amount validation
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            request.setAttribute("errorMessage", "Amount must be strictly greater than zero.");
            doGet(request, response);
            return;
        }

        if (amount.scale() > 2) {
            request.setAttribute("errorMessage", "Amount cannot have more than 2 decimal places.");
            doGet(request, response);
            return;
        }

        if (amount.compareTo(MAX_TOPUP_AMOUNT) > 0) {
            request.setAttribute("errorMessage", "Maximum simulated top-up allowed per transaction is ₹" + MAX_TOPUP_AMOUNT);
            doGet(request, response);
            return;
        }

        try {
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            if (wallet == null) {
                request.setAttribute("errorMessage", "Wallet not found.");
                doGet(request, response);
                return;
            }

            String txnRef = walletDAO.addMoney(wallet.getWalletId(), amount, description);

            if (txnRef != null) {
                // Fetch fresh wallet
                Wallet updatedWallet = walletDAO.findByUserId(user.getUserId());
                session.setAttribute("loggedWallet", updatedWallet);

                request.getSession().setAttribute("successMessage",
                        "Successfully added ₹" + amount.toPlainString() + " to your wallet! Transaction Ref: " + txnRef);
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("errorMessage", "Top-up failed. Please try again.");
                doGet(request, response);
            }

        } catch (Exception e) {
            System.err.println("Top-up transaction error: " + e.getMessage());
            request.setAttribute("errorMessage", "Transaction error: " + e.getMessage());
            doGet(request, response);
        }
    }
}
