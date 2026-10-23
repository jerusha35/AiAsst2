package com.wallet.servlet;

import com.wallet.dao.TransactionDAO;
import com.wallet.dao.WalletDAO;
import com.wallet.model.Transaction;
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
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final WalletDAO walletDAO = new WalletDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");

        try {
            // Fresh fetch of user's wallet
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            session.setAttribute("loggedWallet", wallet);

            // Fetch metrics
            BigDecimal totalAdded = transactionDAO.getTotalAdded(wallet.getWalletId());
            BigDecimal totalTransferred = transactionDAO.getTotalTransferred(wallet.getWalletId());

            // Fetch 5 most recent transactions
            List<Transaction> recentTransactions = transactionDAO.getRecentTransactions(wallet.getWalletId());

            request.setAttribute("wallet", wallet);
            request.setAttribute("totalAdded", totalAdded);
            request.setAttribute("totalTransferred", totalTransferred);
            request.setAttribute("recentTransactions", recentTransactions);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Dashboard error: " + e.getMessage());
            request.setAttribute("errorMessage", "Unable to load dashboard data. Please try again.");
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
