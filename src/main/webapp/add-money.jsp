<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Money - PayFlow Wallet</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/fragments/sidebar.jsp">
        <jsp:param name="active" value="add-money" />
    </jsp:include>

    <div class="main-wrapper">
        <header class="top-bar">
            <div>
                <h1 class="page-title">Add Simulated Funds</h1>
                <p style="color: #94A3B8; font-size: 0.85rem;">Top up your wallet balance instantly for testing</p>
            </div>
        </header>

        <main class="content-body" style="max-width: 640px;">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--surface-border);">
                    <div>
                        <span style="font-size: 0.82rem; color: #94A3B8; text-transform: uppercase;">Current Balance</span>
                        <div style="font-size: 1.8rem; font-weight: 800; color: #fff;">
                            ₹ <fmt:formatNumber value="${wallet.balance}" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                    <span class="badge badge-success">Simulated Engine</span>
                </div>

                <form action="${pageContext.request.contextPath}/add-money" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <label class="form-label">Select Quick Amount</label>
                    <div class="quick-amounts">
                        <button type="button" class="pill-btn" data-value="100">+ ₹100</button>
                        <button type="button" class="pill-btn" data-value="500">+ ₹500</button>
                        <button type="button" class="pill-btn" data-value="1000">+ ₹1,000</button>
                        <button type="button" class="pill-btn" data-value="5000">+ ₹5,000</button>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="amountInput">Top-Up Amount (₹)</label>
                        <input type="number" step="0.01" min="1" max="50000" id="amountInput" name="amount"
                               class="form-control" placeholder="Enter amount (e.g. 500.00)" required autofocus>
                        <span style="font-size: 0.78rem; color: #64748B; margin-top: 4px; display: block;">
                            Maximum allowed per simulated transaction: ₹50,000.00
                        </span>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="description">Note / Reference (Optional)</label>
                        <input type="text" id="description" name="description" class="form-control"
                               placeholder="e.g. Learning balance test">
                    </div>

                    <button type="submit" class="btn btn-primary btn-block" style="margin-top: 24px;">
                        Confirm & Add Funds
                    </button>
                </form>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
