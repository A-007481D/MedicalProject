<%--
  Created by IntelliJ IDEA.
  User: malik
  Date: 10/11/25
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>Register</h2>

<c:if test="${not empty errorMessage}">
    <p style="color:red">${errorMessage}</p>
</c:if>

<form action="${pageContext.request.contextPath}/register" method="post">
    <input type="hidden" name="csrfToken" value="${csrfToken}">
    <label>First Name:</label>
    <input type="text" name="firstName" required/><br/>

    <label>Last Name:</label>
    <input type="text" name="lastName" required/><br/>

    <label>Email:</label>
    <input type="email" name="email" required/><br/>

    <label>Password:</label>
    <input type="password" name="password" required/><br/>

    <label>Confirm Password:</label>
    <input type="password" name="confirmPassword" required/><br/>

    <button type="submit">Register</button>
</form>

<p>Already have an account? <a href="login">Login here</a></p>
</body>
</html>
