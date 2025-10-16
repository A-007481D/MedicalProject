<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Spécialiste - Medical TeleXpertise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .request-card {
            border-left: 4px solid #ffc107;
            transition: transform 0.2s;
        }
        .request-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .timeslot-badge {
            background-color: #28a745;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8em;
        }
        .profile-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/specialist/timeslots">
                            <i class="fas fa-calendar-alt me-1"></i>Mes Créneaux
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
                <h2><i class="fas fa-user-md text-primary me-2"></i>Bienvenue, Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h2>
                <p class="text-muted">Spécialiste en ${sessionScope.user.specialty}</p>
            </div>
        </div>

        <!-- Profile Section -->
        <c:if test="${not empty profile}">
            <div class="row mb-4">
                <div class="col-12">
                    <div class="profile-section">
                        <h4><i class="fas fa-id-card text-primary me-2"></i>Informations du Profil</h4>
                        <div class="row">
                            <div class="col-md-3">
                                <strong>Tarif de consultation:</strong>
                                <span class="text-success fs-5">${profile.tarif} DH</span>
                            </div>
                            <div class="col-md-3">
                                <strong>Durée du créneau:</strong>
                                <span class="timeslot-badge">${profile.slotDurationMinutes} min</span>
                            </div>
                            <div class="col-md-3">
                                <strong>Spécialité:</strong>
                                <span class="badge bg-primary">${sessionScope.user.specialty}</span>
                            </div>
                            <div class="col-md-3">
                                <a href="${pageContext.request.contextPath}/specialist/profile/edit" class="btn btn-outline-primary">
                                    <i class="fas fa-edit me-1"></i>Modifier le Profil
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Stats Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <i class="fas fa-clock fa-2x mb-2"></i>
                    <h3>${fn:length(todaySlots)}</h3>
                    <p>Créneaux aujourd'hui</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <i class="fas fa-exclamation-triangle fa-2x mb-2"></i>
                    <h3>${fn:length(pendingRequests)}</h3>
                    <p>Demandes en attente</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <i class="fas fa-check-circle fa-2x mb-2"></i>
                    <h3>${fn:length(recentConsultations)}</h3>
                    <p>Consultations récentes</p>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card text-center">
                    <i class="fas fa-star fa-2x mb-2"></i>
                    <h3>${profile.tarif} DH</h3>
                    <p>Tarif par consultation</p>
                </div>
            </div>
        </div>

        <!-- Pending Expertise Requests -->
        <c:if test="${not empty pendingRequests}">
            <div class="row mb-4">
                <div class="col-12">
                    <h4><i class="fas fa-clipboard-list text-warning me-2"></i>Demandes d'Expertise en Attente</h4>
                    <div class="row">
                        <c:forEach var="request" items="${pendingRequests}">
                            <div class="col-md-6 mb-3">
                                <div class="card request-card">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="card-title">
                                                    <i class="fas fa-user-circle text-primary me-1"></i>
                                                    Consultation #${request.consultation.id}
                                                </h6>
                                                <p class="card-text">
                                                    <strong>Patient:</strong> ${request.consultation.patient.firstName} ${request.consultation.patient.lastName}<br>
                                                    <strong>Généraliste:</strong> Dr. ${request.consultation.generalist.firstName} ${request.consultation.generalist.lastName}<br>
                                                    <strong>Motif:</strong> ${request.consultation.motif}<br>
                                                    <strong>Date:</strong> <fmt:formatDate value="${request.requestedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </p>
                                                <c:if test="${not empty request.description}">
                                                    <p class="text-muted"><strong>Description:</strong> ${request.description}</p>
                                                </c:if>
                                            </div>
                                            <div class="text-end">
                                                <a href="${pageContext.request.contextPath}/specialist/expertise/${request.id}/respond"
                                                   class="btn btn-warning btn-sm">
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

        <!-- Today's Timeslots -->
        <div class="row mb-4">
            <div class="col-12">
                <h4><i class="fas fa-calendar-day text-success me-2"></i>Créneaux d'Aujourd'hui</h4>
                <c:choose>
                    <c:when test="${not empty todaySlots}">
                        <div class="row">
                            <c:forEach var="slot" items="${todaySlots}">
                                <div class="col-md-3 mb-3">
                                    <div class="card border-${slot.available ? 'success' : 'danger'}">
                                        <div class="card-body text-center">
                                            <h6><fmt:formatDate value="${slot.startTime}" pattern="HH:mm"/> -
                                                <fmt:formatDate value="${slot.endTime}" pattern="HH:mm"/></h6>
                                            <c:choose>
                                                <c:when test="${slot.available}">
                                                    <span class="badge bg-success">Disponible</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger">Occupé</span>
                                                    <br><small class="text-muted">Réservé</small>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Aucun créneau défini pour aujourd'hui.
                            <a href="${pageContext.request.contextPath}/specialist/timeslots" class="alert-link">
                                Gérez vos créneaux horaires
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Recent Consultations -->
        <c:if test="${not empty recentConsultations}">
            <div class="row">
                <div class="col-12">
                    <h4><i class="fas fa-history text-info me-2"></i>Consultations Récentes</h4>
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Patient</th>
                                    <th>Généraliste</th>
                                    <th>Date</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="consultation" items="${recentConsultations}">
                                    <tr>
                                        <td>${consultation.id}</td>
                                        <td>${consultation.patient.firstName} ${consultation.patient.lastName}</td>
                                        <td>Dr. ${consultation.generalist.firstName} ${consultation.generalist.lastName}</td>
                                        <td><fmt:formatDate value="${consultation.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${consultation.status == 'IN_PROGRESS'}">
                                                    <span class="badge bg-warning">En cours</span>
                                                </c:when>
                                                <c:when test="${consultation.status == 'WAITING_EXPERTISE'}">
                                                    <span class="badge bg-info">Expertise demandée</span>
                                                </c:when>
                                                <c:when test="${consultation.status == 'CLOSED'}">
                                                    <span class="badge bg-success">Fermée</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${consultation.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/specialist/consultation/${consultation.id}"
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye me-1"></i>Voir
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
