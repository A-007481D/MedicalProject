<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Admin Dashboard</title>
</head>
<body>
<h2>Admin Dashboard</h2>

<table border="1">
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Email</th>
        <th>Role</th>
        <th>Actions</th>
    </tr>

    <c:forEach var="user" items="${users}">
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.email}</td>
            <td>
                <c:choose>
                    <c:when test="${empty user.role}">No Role</c:when>
                    <c:when test="${user.role.name() == 'BASE'}">
                        <span style="color: #f39c12; font-weight: bold;">⏳ Awaiting Assignment</span>
                    </c:when>
                    <c:otherwise>${user.role}</c:otherwise>
                </c:choose>
            </td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/admin/promote">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="userId" value="${user.id}" />
                    <select name="role" required>
                        <option value="" disabled ${empty user.role || user.role.name() == 'BASE' ? 'selected' : ''}>-- Select Role --</option>
                        <c:forEach var="r" items="${['ADMIN','GENERALIST','SPECIALIST','NURSE']}">
                            <option value="${r}" ${not empty user.role && user.role.name() == r ? 'selected' : ''}>${r}</option>
                        </c:forEach>
                    </select>
                    <button type="submit">Change</button>
                </form>
            </td>
        </tr>
    </c:forEach>

</table>
</body>
</html>
