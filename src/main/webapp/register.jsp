<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Your Wallet - PayFlow Simulator</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="auth-wrapper">
    <div class="auth-box">
        <div class="auth-header">
            <div class="brand-icon" style="margin: 0 auto;">₹</div>
            <h2>Create Account</h2>
            <p>Register to generate your simulated digital wallet</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form id="registerForm" action="${pageContext.request.contextPath}/register" method="POST">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="form-group">
                <label class="form-label" for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control"
                       placeholder="e.g. Rahul Sharma" value="${fullName}" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="name@example.com" value="${email}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" class="form-control"
                       placeholder="e.g. 9876543210" value="${phone}">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 6 characters" minlength="6" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       placeholder="Re-enter password" minlength="6" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="margin-top: 24px;">
                Create Free Wallet
            </button>
        </form>

        <div style="text-align: center; margin-top: 24px; font-size: 0.9rem; color: #94A3B8;">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login.jsp" style="font-weight: 600; color: #3A86FF;">
                Log In
            </a>
        </div>
        <div style="text-align: center; margin-top: 12px; font-size: 0.85rem;">
            <a href="${pageContext.request.contextPath}/index.jsp" style="color: #64748B;">&larr; Back to Home</a>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
