<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History - PayFlow Wallet</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/fragments/sidebar.jsp">
        <jsp:param name="active" value="transactions" />
    </jsp:include>

    <div class="main-wrapper">
        <header class="top-bar">
            <div>
                <h1 class="page-title">Transaction Ledger</h1>
                <p style="color: #94A3B8; font-size: 0.85rem;">Complete audit log of credits and debits</p>
            </div>
        </header>

        <main class="content-body">
            <!-- Filter Bar -->
            <div class="card" style="padding: 16px 24px; margin-bottom: 20px;">
                <form action="${pageContext.request.contextPath}/transactions" method="GET" style="display: flex; gap: 16px; align-items: center; flex-wrap: wrap;">
                    <label class="form-label" style="margin: 0; white-space: nowrap;">Filter By Type:</label>
                    <select name="type" class="form-control" style="width: auto; padding: 8px 14px; font-size: 0.9rem;" onchange="this.form.submit()">
                        <option value="ALL" ${selectedType == 'ALL' ? 'selected' : ''}>All Transactions</option>
                        <option value="TOPUP" ${selectedType == 'TOPUP' ? 'selected' : ''}>Top-Ups Only</option>
                        <option value="TRANSFER_SENT" ${selectedType == 'TRANSFER_SENT' ? 'selected' : ''}>Transfers Sent</option>
                        <option value="TRANSFER_RECEIVED" ${selectedType == 'TRANSFER_RECEIVED' ? 'selected' : ''}>Transfers Received</option>
                    </select>
                    <span style="margin-left: auto; color: #94A3B8; font-size: 0.85rem;">
                        Showing ${transactions.size()} of ${totalRecords} records
                    </span>
                </form>
            </div>

            <!-- Ledger Table -->
            <div class="card">
                <c:choose>
                    <c:when test="${empty transactions}">
                        <div style="text-align: center; padding: 40px; color: #64748B;">
                            <div style="font-size: 40px; margin-bottom: 8px;">📜</div>
                            <p>No transaction records match the criteria.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="custom-table">
                                <thead>
                                    <tr>
                                        <th>Reference</th>
                                        <th>Date & Time</th>
                                        <th>Type</th>
                                        <th>Description</th>
                                        <th>Status</th>
                                        <th style="text-align: right;">Debit</th>
                                        <th style="text-align: right;">Credit</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${transactions}" var="t">
                                        <tr>
                                            <td style="font-family: monospace; font-size: 0.88rem; color: #94A3B8;">${t.transactionRef}</td>
                                            <td style="color: #94A3B8; font-size: 0.85rem;">
                                                <fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm:ss a" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${t.transactionType == 'TOPUP'}">
                                                        <span class="badge badge-credit">Top-Up</span>
                                                    </c:when>
                                                    <c:when test="${t.transactionType == 'TRANSFER_RECEIVED'}">
                                                        <span class="badge badge-credit">Received</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-debit">Sent</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${t.description}</td>
                                            <td>
                                                <span class="badge badge-success">${t.transactionStatus}</span>
                                            </td>
                                            <td style="text-align: right;">
                                                <c:if test="${t.direction == 'DEBIT'}">
                                                    <span class="amount-debit">- ₹<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                                                </c:if>
                                            </td>
                                            <td style="text-align: right;">
                                                <c:if test="${t.direction == 'CREDIT'}">
                                                    <span class="amount-credit">+ ₹<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination Controls -->
                        <c:if test="${totalPages > 1}">
                            <div class="pagination-wrap">
                                <span style="font-size: 0.85rem; color: #94A3B8;">Page ${currentPage} of ${totalPages}</span>
                                <div class="pagination-controls">
                                    <c:if test="${currentPage > 1}">
                                        <a href="${pageContext.request.contextPath}/transactions?page=${currentPage - 1}&type=${selectedType}" class="btn btn-secondary btn-sm">&larr; Previous</a>
                                    </c:if>
                                    <c:if test="${currentPage < totalPages}">
                                        <a href="${pageContext.request.contextPath}/transactions?page=${currentPage + 1}&type=${selectedType}" class="btn btn-secondary btn-sm">Next &rarr;</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
