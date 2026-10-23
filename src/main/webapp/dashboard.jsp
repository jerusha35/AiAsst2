<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - PayFlow Wallet</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/fragments/sidebar.jsp">
        <jsp:param name="active" value="dashboard" />
    </jsp:include>

    <div class="main-wrapper">
        <header class="top-bar">
            <div>
                <h1 class="page-title">Wallet Overview</h1>
                <p style="color: #94A3B8; font-size: 0.85rem;">Welcome back, ${sessionScope.loggedUser.fullName} 👋</p>
            </div>
            <button id="mobileNavToggle" class="btn btn-secondary btn-sm" style="display: none;">☰ Menu</button>
        </header>

        <main class="content-body">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">${sessionScope.successMessage}</div>
                <% session.removeAttribute("successMessage"); %>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <!-- Wallet Balance Hero Card -->
            <div class="wallet-hero card">
                <div class="hero-label">Available Balance</div>
                <div class="hero-balance">
                    <span>₹</span> <fmt:formatNumber value="${wallet.balance}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                </div>
                <div class="hero-actions">
                    <a href="${pageContext.request.contextPath}/add-money" class="btn btn-primary">
                        ➕ Add Simulated Money
                    </a>
                    <a href="${pageContext.request.contextPath}/transfer" class="btn btn-secondary">
                        ↗️ Transfer Funds
                    </a>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary">
                        📜 View Full Ledger
                    </a>
                </div>
            </div>

            <!-- Metrics Row -->
            <div class="metrics-grid">
                <div class="metric-card">
                    <div class="metric-icon-wrap icon-credit">📥</div>
                    <div class="metric-info">
                        <h4>Total Money Added</h4>
                        <div class="value">
                            ₹ <fmt:formatNumber value="${totalAdded}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                </div>

                <div class="metric-card">
                    <div class="metric-icon-wrap icon-debit">📤</div>
                    <div class="metric-info">
                        <h4>Total Money Transferred</h4>
                        <div class="value">
                            ₹ <fmt:formatNumber value="${totalTransferred}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                </div>

                <div class="metric-card">
                    <div class="metric-icon-wrap icon-info">💳</div>
                    <div class="metric-info">
                        <h4>Wallet ID</h4>
                        <div class="value" style="font-size: 1.25rem;">
                            #WLT-${wallet.walletId}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent 5 Transactions -->
            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                    <div>
                        <h3 style="font-size: 1.15rem; font-weight: 700;">Recent Transactions</h3>
                        <p style="color: #94A3B8; font-size: 0.85rem;">Your latest 5 simulated activities</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-sm">
                        View All
                    </a>
                </div>

                <c:choose>
                    <c:when test="${empty recentTransactions}">
                        <div style="text-align: center; padding: 40px; color: #64748B;">
                            <div style="font-size: 40px; margin-bottom: 8px;">💳</div>
                            <p>No transactions yet. Click <strong>Add Simulated Money</strong> to start testing!</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="custom-table">
                                <thead>
                                    <tr>
                                        <th>Ref No.</th>
                                        <th>Type</th>
                                        <th>Description</th>
                                        <th>Date & Time</th>
                                        <th>Status</th>
                                        <th style="text-align: right;">Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentTransactions}" var="t">
                                        <tr>
                                            <td style="font-family: monospace; color: #94A3B8;">${t.transactionRef}</td>
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
                                            <td style="color: #94A3B8; font-size: 0.85rem;">
                                                <fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a" />
                                            </td>
                                            <td>
                                                <span class="badge badge-success">${t.transactionStatus}</span>
                                            </td>
                                            <td style="text-align: right;">
                                                <c:choose>
                                                    <c:when test="${t.direction == 'CREDIT'}">
                                                        <span class="amount-credit">+ ₹<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="amount-debit">- ₹<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2" /></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
