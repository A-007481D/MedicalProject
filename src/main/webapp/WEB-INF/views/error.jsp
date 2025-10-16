<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Erreur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .error-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            text-align: center;
        }
        .error-details {
            margin-top: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
            text-align: left;
        }
    </style>
</head>
<body>
    <div class="container error-container">
        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">Une erreur est survenue</h4>
            <p>${not empty error ? error : 'Désolé, une erreur inattendue s\'est produite.'}</p>
            <hr>
            <p class="mb-0">Veuillez réessayer ou contacter l'administrateur si le problème persiste.</p>
        </div>
        
        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/dashboard/specialist" class="btn btn-primary">
                Retour au tableau de bord
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                Page de connexion
            </a>
        </div>
        
        <c:if test="${not empty requestScope['jakarta.servlet.error.exception']}">
            <div class="error-details mt-4">
                <h5>Détails de l'erreur :</h5>
                <p>${requestScope['jakarta.servlet.error.exception'].message}</p>
                <pre>${requestScope['jakarta.servlet.error.exception'].stackTrace}</pre>
            </div>
        </c:if>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
