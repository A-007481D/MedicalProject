<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demandes d'Expertise - Medical TeleXpertise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .request-card {
            border-left: 4px solid #ffc107;
            transition: transform 0.2s;
        }
        .request-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .status-badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8em;
        }
        .status-pending {
            background-color: #ffc107;
            color: #212529;
        }
        .status-completed {
            background-color: #28a745;
            color: white;
        }
        .status-cancelled {
            background-color: #dc3545;
            color: white;
        }
        .form-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 30px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/specialist/dashboard">
                <i class="fas fa-stethoscope me-2"></i>Medical TeleXpertise - Spécialiste
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/specialist/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Tableau de Bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/specialist/profile">
                            <i class="fas fa-user-md me-1"></i>Mon Profil
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/specialist/expertise">
                            <i class="fas fa-clipboard-list me-1"></i>Demandes d'Expertise
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i>Déconnexion
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <h2><i class="fas fa-clipboard-list text-warning me-2"></i>Demandes d'Expertise</h2>
                <p class="text-muted">Gérez vos demandes d'expertise et fournissez vos avis médicaux</p>
            </div>
        </div>

        <!-- Pending Requests Section -->
        <c:if test="${not empty pendingRequests}">
            <div class="row mb-4">
                <div class="col-12">
                    <h4><i class="fas fa-clock text-warning me-2"></i>Demandes en Attente (${pendingRequests.size()})</h4>
                    <div class="row">
                        <c:forEach var="request" items="${pendingRequests}">
                            <div class="col-lg-6 mb-3">
                                <div class="card request-card">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div class="flex-grow-1">
                                                <h6 class="card-title">
                                                    <i class="fas fa-user-circle text-primary me-1"></i>
                                                    Consultation #${request.consultation.id}
                                                </h6>
                                                <p class="card-text mb-2">
                                                    <strong>Patient:</strong> ${request.consultation.patient.firstName} ${request.consultation.patient.lastName}<br>
                                                    <strong>Généraliste:</strong> Dr. ${request.consultation.generalist.firstName} ${request.consultation.generalist.lastName}<br>
                                                    <strong>Motif:</strong> ${request.consultation.motif}<br>
                                                    <strong>Date demande:</strong> <fmt:formatDate value="${request.requestedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </p>
                                                <c:if test="${not empty request.description}">
                                                    <div class="alert alert-info py-2">
                                                        <strong>Description:</strong><br>${request.description}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="text-end ms-3">
                                                <a href="${pageContext.request.contextPath}/specialist/expertise/${request.id}/respond"
                                                   class="btn btn-warning">
                                                    <i class="fas fa-reply me-1"></i>Répondre
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- All Requests Section -->
        <div class="row">
            <div class="col-12">
                <h4><i class="fas fa-list text-primary me-2"></i>Toutes les Demandes</h4>

                <c:choose>
                    <c:when test="${not empty allRequests}">
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Patient</th>
                                        <th>Généraliste</th>
                                        <th>Date demande</th>
                                        <th>Statut</th>
                                        <th>Date réponse</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="request" items="${allRequests}">
                                        <tr>
                                            <td>${request.id}</td>
                                            <td>${request.consultation.patient.firstName} ${request.consultation.patient.lastName}</td>
                                            <td>Dr. ${request.consultation.generalist.firstName} ${request.consultation.generalist.lastName}</td>
                                            <td><fmt:formatDate value="${request.requestedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${request.status == 'PENDING'}">
                                                        <span class="status-badge status-pending">
                                                            <i class="fas fa-clock me-1"></i>En attente
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${request.status == 'COMPLETED'}">
                                                        <span class="status-badge status-completed">
                                                            <i class="fas fa-check me-1"></i>Terminée
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${request.status == 'CANCELLED'}">
                                                        <span class="status-badge status-cancelled">
                                                            <i class="fas fa-times me-1"></i>Annulée
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${request.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${request.status == 'COMPLETED' && not empty request.completedAt}">
                                                    <fmt:formatDate value="${request.completedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${request.status == 'PENDING'}">
                                                        <a href="${pageContext.request.contextPath}/specialist/expertise/${request.id}/respond"
                                                           class="btn btn-sm btn-warning">
                                                            <i class="fas fa-reply me-1"></i>Répondre
                                                        </a>
                                                    </c:when>
                                                    <c:when test="${request.status == 'COMPLETED'}">
                                                        <a href="${pageContext.request.contextPath}/specialist/expertise/${request.id}"
                                                           class="btn btn-sm btn-info">
                                                            <i class="fas fa-eye me-1"></i>Voir réponse
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">N/A</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Aucune demande d'expertise trouvée.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
