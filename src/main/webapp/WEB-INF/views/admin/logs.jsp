<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>User Logs</title></head>
<body>
<h3>User Activity Logs</h3>
<table border="1">
    <tr><th>Date</th><th>Action</th><th>Details</th></tr>
    <c:forEach var="log" items="${logs}">
        <tr>
            <td>${log.timestamp}</td>
            <td>${log.action}</td>
            <td>${log.details}</td>
        </tr>
    </c:forEach>
</table>

<a href="${pageContext.request.contextPath}/dashboard/admin">Back</a>
</body>
</html>