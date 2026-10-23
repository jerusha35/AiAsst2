<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transfer Funds - PayFlow Wallet</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/fragments/sidebar.jsp">
        <jsp:param name="active" value="transfer" />
    </jsp:include>

    <div class="main-wrapper">
        <header class="top-bar">
            <div>
                <h1 class="page-title">Transfer Funds</h1>
                <p style="color: #94A3B8; font-size: 0.85rem;">Send money securely to another wallet by email</p>
            </div>
        </header>

        <main class="content-body" style="max-width: 640px;">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid var(--surface-border);">
                    <div>
                        <span style="font-size: 0.82rem; color: #94A3B8; text-transform: uppercase;">Available for Transfer</span>
                        <div style="font-size: 1.8rem; font-weight: 800; color: #fff;">
                            ₹ <fmt:formatNumber value="${wallet.balance}" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                    <span class="badge badge-credit">ACID Row Locked</span>
                </div>

                <form id="transferForm" action="${pageContext.request.contextPath}/transfer" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="form-group">
                        <label class="form-label" for="receiverEmail">Recipient Email Address</label>
                        <input type="email" id="receiverEmail" name="receiverEmail" class="form-control"
                               placeholder="e.g. friend@example.com" value="${receiverEmail}" required autofocus>
                        <div id="receiverFeedback" style="margin-top: 6px; min-height: 20px;"></div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="amountInput">Transfer Amount (₹)</label>
                        <input type="number" step="0.01" min="1" max="${wallet.balance}" id="amountInput" name="amount"
                               class="form-control" placeholder="0.00" value="${amount}" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="description">Purpose / Note (Optional)</label>
                        <input type="text" id="description" name="description" class="form-control"
                               placeholder="e.g. Dinner bill split" value="${description}">
                    </div>

                    <button type="submit" class="btn btn-primary btn-block" style="margin-top: 24px;">
                        Proceed to Send Transfer
                    </button>
                </form>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
