<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar">
    <div class="brand">
        <div class="brand-icon">₹</div>
        <div class="brand-name">PayFlow Wallet</div>
    </div>

    <ul class="nav-menu">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${param.active == 'dashboard' ? 'active' : ''}">
                <i>🏠</i>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/add-money" class="nav-link ${param.active == 'add-money' ? 'active' : ''}">
                <i>➕</i>
                <span>Add Money</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/transfer" class="nav-link ${param.active == 'transfer' ? 'active' : ''}">
                <i>↗️</i>
                <span>Transfer Money</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/transactions" class="nav-link ${param.active == 'transactions' ? 'active' : ''}">
                <i>📜</i>
                <span>Transaction History</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/profile" class="nav-link ${param.active == 'profile' ? 'active' : ''}">
                <i>👤</i>
                <span>My Profile</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div class="user-mini">
            <div class="avatar-sm">
                ${sessionScope.loggedUser.fullName.substring(0, 1).toUpperCase()}
            </div>
            <div>
                <div class="user-meta-name">${sessionScope.loggedUser.fullName}</div>
                <div class="user-meta-email">${sessionScope.loggedUser.email}</div>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/logout" title="Log Out" style="color: #EF4444; font-size: 1.1rem; padding: 4px;">
            🚪
        </a>
    </div>
</aside>
