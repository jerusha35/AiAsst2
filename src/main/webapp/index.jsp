<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Digital Wallet Simulator - Modern Educational FinTech Platform</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body style="background: radial-gradient(circle at 50% 10%, #1E3A8A 0%, #070D1E 60%);">

    <!-- Landing Navigation -->
    <header style="padding: 24px 40px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.08);">
        <div style="display: flex; align-items: center; gap: 12px;">
            <div class="brand-icon">₹</div>
            <h1 style="font-size: 1.35rem; font-weight: 800; color: #fff;">PayFlow <span style="color: #3A86FF;">Wallet</span></h1>
        </div>
        <div style="display: flex; gap: 16px;">
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary">Log In</a>
            <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">Create Free Account</a>
        </div>
    </header>

    <!-- Hero Section -->
    <main style="max-width: 1100px; margin: 60px auto; padding: 0 24px; text-align: center;">
        <c:if test="${param.loggedOut == 'true'}">
            <div class="alert alert-success" style="max-width: 600px; margin: 0 auto 30px auto;">
                You have been logged out securely. Thank you for using PayFlow Wallet.
            </div>
        </c:if>

        <span style="display: inline-block; padding: 6px 16px; background: rgba(58, 134, 255, 0.15); border: 1px solid rgba(58, 134, 255, 0.3); border-radius: 20px; color: #60A5FA; font-weight: 600; font-size: 0.85rem; margin-bottom: 20px;">
            ✦ Full-Stack Java • Servlets • Oracle JDBC
        </span>

        <h2 style="font-size: 3.2rem; font-weight: 800; line-height: 1.2; margin-bottom: 20px; color: #fff;">
            Simulate Digital Payments with <br><span style="background: linear-gradient(135deg, #3A86FF, #00B4D8); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">Total Architectural Precision</span>
        </h2>

        <p style="font-size: 1.15rem; color: #94A3B8; max-width: 720px; margin: 0 auto 36px auto;">
            Experience a realistic fintech wallet dashboard. Test atomic top-ups, peer-to-peer transfers with Oracle row-locking deadlock prevention, and comprehensive transaction ledgers without real money.
        </p>

        <div style="display: flex; justify-content: center; gap: 18px; margin-bottom: 60px;">
            <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary" style="padding: 14px 32px; font-size: 1.05rem;">
                Get Started Now &rarr;
            </a>
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary" style="padding: 14px 32px; font-size: 1.05rem;">
                Sign In to Dashboard
            </a>
        </div>

        <!-- Feature Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 24px; text-align: left;">
            <div class="card" style="margin-bottom: 0;">
                <div style="font-size: 28px; margin-bottom: 12px; color: #3A86FF;">⚡</div>
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">Atomic Transfers</h3>
                <p style="color: #94A3B8; font-size: 0.92rem;">
                    Full ACID compliance using JDBC transactions and Oracle row locking (<code>FOR UPDATE</code>) with ordered lock resolution to prevent deadlocks.
                </p>
            </div>

            <div class="card" style="margin-bottom: 0;">
                <div style="font-size: 28px; margin-bottom: 12px; color: #10B981;">🛡️</div>
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">Enterprise Security</h3>
                <p style="color: #94A3B8; font-size: 0.92rem;">
                    Secure BCrypt password hashing, CSRF token validation on sensitive state-changing forms, and session-based authorization filters.
                </p>
            </div>

            <div class="card" style="margin-bottom: 0;">
                <div style="font-size: 28px; margin-bottom: 12px; color: #00B4D8;">📊</div>
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">Ledger & Pagination</h3>
                <p style="color: #94A3B8; font-size: 0.92rem;">
                    Complete transaction history with credit/debit indicators, type filtering, and clean pagination directly backed by Oracle SQL.
                </p>
            </div>
        </div>
    </main>

    <footer style="text-align: center; padding: 40px 20px; border-top: 1px solid rgba(255,255,255,0.08); color: #64748B; font-size: 0.85rem;">
        PayFlow Digital Wallet Simulator &bull; Educational Project &bull; Built with Java, Servlets, JSP, JDBC, Oracle SQL
    </footer>

</body>
</html>
