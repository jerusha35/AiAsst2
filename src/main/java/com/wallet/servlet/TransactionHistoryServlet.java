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
import java.util.List;

@WebServlet("/transactions")
public class TransactionHistoryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private final WalletDAO walletDAO = new WalletDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("loggedUser");

        String typeFilter = request.getParameter("type");
        String pageStr = request.getParameter("page");

        int page = 1;
        if (pageStr != null) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        try {
            Wallet wallet = walletDAO.findByUserId(user.getUserId());
            int totalRecords = transactionDAO.countFilteredTransactions(wallet.getWalletId(), typeFilter);
            int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;

            int offset = (page - 1) * PAGE_SIZE;

            List<Transaction> transactions = transactionDAO.getFilteredTransactions(
                    wallet.getWalletId(), typeFilter, offset, PAGE_SIZE);

            request.setAttribute("wallet", wallet);
            request.setAttribute("transactions", transactions);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("selectedType", typeFilter != null ? typeFilter : "ALL");

            request.getRequestDispatcher("/transactions.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Transaction history error: " + e.getMessage());
            request.setAttribute("errorMessage", "Error fetching transaction history.");
            request.getRequestDispatcher("/dashboard").forward(request, response);
        }
    }
}
