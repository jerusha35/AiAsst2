<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - PayFlow Wallet</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/fragments/sidebar.jsp">
        <jsp:param name="active" value="profile" />
    </jsp:include>

    <div class="main-wrapper">
        <header class="top-bar">
            <div>
                <h1 class="page-title">User Profile & Settings</h1>
                <p style="color: #94A3B8; font-size: 0.85rem;">Manage your wallet account details and security</p>
            </div>
        </header>

        <main class="content-body" style="max-width: 760px;">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">${sessionScope.successMessage}</div>
                <% session.removeAttribute("successMessage"); %>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger">${sessionScope.errorMessage}</div>
                <% session.removeAttribute("errorMessage"); %>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <!-- Profile Summary Card -->
            <div class="card" style="display: flex; align-items: center; gap: 20px;">
                <div style="width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, #3A86FF, #00B4D8); display: flex; align-items: center; justify-content: center; font-size: 1.8rem; font-weight: 700; color: #fff;">
                    ${user.fullName.substring(0, 1).toUpperCase()}
                </div>
                <div>
                    <h2 style="font-size: 1.35rem; font-weight: 700;">${user.fullName}</h2>
                    <p style="color: #94A3B8; font-size: 0.9rem;">${user.email}</p>
                    <div style="display: flex; gap: 12px; margin-top: 6px;">
                        <span class="badge badge-success">Account: ${user.status}</span>
                        <span class="badge badge-credit">Wallet #WLT-${wallet.walletId}</span>
                    </div>
                </div>
            </div>

            <!-- Edit Personal Info -->
            <div class="card">
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 20px;">Personal Details</h3>
                <form action="${pageContext.request.contextPath}/profile" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="form-group">
                        <label class="form-label" for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" class="form-control"
                               value="${user.fullName}" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="email">Email Address (Immutable)</label>
                        <input type="email" id="email" class="form-control"
                               value="${user.email}" readonly disabled style="opacity: 0.6; cursor: not-allowed;">
                        <span style="font-size: 0.75rem; color: #64748B;">For security, email modification requires separate identity verification.</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="phone">Phone Number</label>
                        <input type="tel" id="phone" name="phone" class="form-control"
                               value="${user.phone}">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Member Since</label>
                        <div style="color: #94A3B8; font-size: 0.9rem;">
                            <fmt:formatDate value="${user.createdAt}" pattern="dd MMMM yyyy, hh:mm a" />
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary">
                        Save Profile Changes
                    </button>
                </form>
            </div>

            <!-- Change Password Card -->
            <div class="card">
                <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 20px;">Change Password</h3>
                <form action="${pageContext.request.contextPath}/change-password" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="form-group">
                        <label class="form-label" for="currentPassword">Current Password</label>
                        <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="newPassword">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" class="form-control" minlength="6" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="confirmPassword">Confirm New Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" minlength="6" required>
                    </div>

                    <button type="submit" class="btn btn-secondary">
                        Update Password
                    </button>
                </form>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
