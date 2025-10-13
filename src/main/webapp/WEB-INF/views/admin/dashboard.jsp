<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        table { border-collapse: collapse; width: 80%; margin: 20px auto; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
        form { display: inline; }
    </style>
</head>
<body>
<h2 style="text-align:center;">Admin Dashboard</h2>

<table>
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Email</th>
        <th>Role</th>
        <th>Actions</th>
    </tr>
    <c: var="user" items="${users}">
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/admin/promote">
                    <input type="hidden" name="userId" value="${user.id}" />
                    <select name="role">
                        <c: var="r" items="${['ADMIN','GENERALIST','SPECIALIST','NURSE']}">
                            <option value="${r}" ${user.role == r ? 'selected' : ''}>${r}</option>
                        </c:>
                    </select>
                    <button type="submit">Change</button>
                </form>
                <form method="get" action="${pageContext.request.contextPath}/admin/logs">
                    <input type="hidden" name="userId" value="${user.id}" />
                    <button type="submit">View Logs</button>
                </form>
            </td>
        </tr>
    </c:>
</table>

</body>
</html>