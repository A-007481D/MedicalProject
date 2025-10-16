<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil - Medical TeleXpertise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .profile-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
        }
        .form-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 30px;
            margin-bottom: 20px;
        }
        .info-card {
            background-color: #e9ecef;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/specialist/profile">
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
                <h2><i class="fas fa-user-md text-primary me-2"></i>Mon Profil de Spécialiste</h2>
                <p class="text-muted">Gérez vos informations professionnelles et paramètres de consultation</p>
            </div>
        </div>

        <!-- Current Information Card -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="profile-card">
                    <div class="row">
                        <div class="col-md-8">
                            <h3><i class="fas fa-user-circle me-2"></i>Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h3>
                            <p><i class="fas fa-stethoscope me-2"></i>Spécialiste en ${sessionScope.user.specialty}</p>
                            <p><i class="fas fa-phone me-2"></i>${sessionScope.user.phone}</p>
                            <p><i class="fas fa-envelope me-2"></i>${sessionScope.user.email}</p>
                        </div>
                        <div class="col-md-4 text-end">
                            <h4 class="text-warning">
                                <i class="fas fa-coins me-1"></i>
                                ${profile.tarif} DH
                            </h4>
                            <p>Tarif de consultation</p>
                            <p><i class="fas fa-clock me-1"></i>${profile.slotDurationMinutes} min par créneau</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty success}">
            <div class="row mb-3">
                <div class="col-12">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="row mb-3">
                <div class="col-12">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Profile Management Form -->
        <div class="row">
            <div class="col-lg-8">
                <div class="form-section">
                    <h4><i class="fas fa-edit text-primary me-2"></i>Modifier les Paramètres</h4>

                    <form method="post" action="${pageContext.request.contextPath}/specialist/profile">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="tarif" class="form-label">
                                    <i class="fas fa-coins me-1"></i>Tarif de Consultation (DH)
                                </label>
                                <div class="input-group">
                                    <span class="input-group-text">DH</span>
                                    <input type="number" class="form-control" id="tarif" name="tarif"
                                           value="${profile.tarif}" min="0" step="0.01" required>
                                </div>
                                <div class="form-text">Montant facturé par consultation</div>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="slotDurationMinutes" class="form-label">
                                    <i class="fas fa-clock me-1"></i>Durée du Créneau (minutes)
                                </label>
                                <select class="form-select" id="slotDurationMinutes" name="slotDurationMinutes" required>
                                    <option value="15" ${profile.slotDurationMinutes == 15 ? 'selected' : ''}>15 minutes</option>
                                    <option value="30" ${profile.slotDurationMinutes == 30 ? 'selected' : ''}>30 minutes</option>
                                    <option value="45" ${profile.slotDurationMinutes == 45 ? 'selected' : ''}>45 minutes</option>
                                    <option value="60" ${profile.slotDurationMinutes == 60 ? 'selected' : ''}>1 heure</option>
                                </select>
                                <div class="form-text">Durée standard de chaque créneau de consultation</div>
                            </div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save me-2"></i>Enregistrer les Modifications
                            </button>
                            <a href="${pageContext.request.contextPath}/specialist/dashboard" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Retour au Tableau de Bord
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Information Panel -->
            <div class="col-lg-4">
                <div class="info-card">
                    <h5><i class="fas fa-info-circle text-info me-2"></i>Informations</h5>
                    <ul class="list-unstyled">
                        <li class="mb-2">
                            <i class="fas fa-check text-success me-2"></i>
                            Votre profil détermine votre tarif et la durée de vos consultations
                        </li>
                        <li class="mb-2">
                            <i class="fas fa-check text-success me-2"></i>
                            Ces paramètres sont utilisés pour calculer les coûts des consultations
                        </li>
                        <li class="mb-2">
                            <i class="fas fa-check text-success me-2"></i>
                            Les modifications sont appliquées immédiatement
                        </li>
                    </ul>
                </div>

                <div class="info-card">
                    <h5><i class="fas fa-chart-line text-primary me-2"></i>Statistiques Rapides</h5>
                    <div class="d-flex justify-content-between">
                        <span>Tarif actuel:</span>
                        <strong class="text-success">${profile.tarif} DH</strong>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span>Créneau:</span>
                        <strong>${profile.slotDurationMinutes} min</strong>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
