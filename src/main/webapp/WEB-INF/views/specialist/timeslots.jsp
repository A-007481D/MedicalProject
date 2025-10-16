<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Créneaux - Medical TeleXpertise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .timeslot-card {
            border-left: 4px solid #28a745;
            transition: transform 0.2s;
        }
        .timeslot-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
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
        .available-badge {
            background-color: #28a745;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8em;
        }
        .unavailable-badge {
            background-color: #dc3545;
            color: white;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8em;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/specialist/timeslots">
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
                <h2><i class="fas fa-calendar-alt text-primary me-2"></i>Mes Créneaux Horaires</h2>
                <p class="text-muted">Gérez vos disponibilités pour les consultations</p>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty param.success}">
            <div class="row mb-3">
                <div class="col-12">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${param.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="row mb-3">
                <div class="col-12">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Create Timeslot Form -->
        <div class="row mb-4">
            <div class="col-lg-8">
                <div class="form-section">
                    <h4><i class="fas fa-plus-circle text-success me-2"></i>Ajouter un Créneau</h4>

                    <form method="post" action="${pageContext.request.contextPath}/specialist/timeslots">
                        <input type="hidden" name="action" value="create">

                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="date" class="form-label">
                                    <i class="fas fa-calendar me-1"></i>Date
                                </label>
                                <input type="date" class="form-control" id="date" name="date"
                                       min="${next7Days[0]}" max="${next7Days[6]}" required>
                                <div class="form-text">Sélectionnez une date dans les 7 prochains jours</div>
                            </div>

                            <div class="col-md-3 mb-3">
                                <label for="startTime" class="form-label">
                                    <i class="fas fa-clock me-1"></i>Heure de début
                                </label>
                                <input type="time" class="form-control" id="startTime" name="startTime"
                                       min="08:00" max="18:00" required>
                            </div>

                            <div class="col-md-3 mb-3">
                                <label for="endTime" class="form-label">
                                    <i class="fas fa-clock me-1"></i>Heure de fin
                                </label>
                                <input type="time" class="form-control" id="endTime" name="endTime"
                                       min="08:30" max="18:30" required>
                            </div>

                            <div class="col-md-2 mb-3 d-flex align-items-end">
                                <button type="submit" class="btn btn-success w-100">
                                    <i class="fas fa-plus me-1"></i>Ajouter
                                </button>
                            </div>
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
                            Créez vos créneaux de disponibilité
                        </li>
                        <li class="mb-2">
                            <i class="fas fa-check text-success me-2"></i>
                            Chaque créneau dure ${profile.slotDurationMinutes} minutes
                        </li>
                        <li class="mb-2">
                            <i class="fas fa-check text-success me-2"></i>
                            Les généralistes peuvent réserver vos créneaux
                        </li>
                    </ul>
                </div>

                <div class="info-card">
                    <h5><i class="fas fa-chart-line text-primary me-2"></i>Statistiques</h5>
                    <div class="d-flex justify-content-between">
                        <span>Total créneaux:</span>
                        <strong>${timeslots.size()}</strong>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span>Créneaux disponibles:</span>
                        <strong class="text-success">
                            ${timeslots.stream().filter(t -> t.available).count()}
                        </strong>
                    </div>
                </div>
            </div>
        </div>

        <!-- Timeslots List -->
        <div class="row">
            <div class="col-12">
                <h4><i class="fas fa-list text-primary me-2"></i>Mes Créneaux</h4>

                <c:choose>
                    <c:when test="${not empty timeslots}">
                        <div class="row">
                            <c:forEach var="timeslot" items="${timeslots}" varStatus="loop">
                                <div class="col-lg-6 col-xl-4 mb-3">
                                    <div class="card timeslot-card">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-start">
                                                <div>
                                                    <h6 class="card-title">
                                                        <i class="fas fa-calendar-day text-primary me-1"></i>
                                                        <fmt:formatDate value="${timeslot.startTime}" pattern="dd/MM/yyyy"/>
                                                    </h6>
                                                    <p class="card-text">
                                                        <i class="fas fa-clock me-1"></i>
                                                        <fmt:formatDate value="${timeslot.startTime}" pattern="HH:mm"/> -
                                                        <fmt:formatDate value="${timeslot.endTime}" pattern="HH:mm"/>
                                                    </p>
                                                    <c:choose>
                                                        <c:when test="${timeslot.available}">
                                                            <span class="available-badge">
                                                                <i class="fas fa-check-circle me-1"></i>Disponible
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="unavailable-badge">
                                                                <i class="fas fa-times-circle me-1"></i>Occupé
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="text-end">
                                                    <form method="post" action="${pageContext.request.contextPath}/specialist/timeslots"
                                                          onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer ce créneau ?')">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="timeslotId" value="${timeslot.id}">
                                                        <button type="submit" class="btn btn-outline-danger btn-sm">
                                                            <i class="fas fa-trash me-1"></i>Supprimer
                                                        </button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Aucun créneau défini. Utilisez le formulaire ci-dessus pour ajouter vos premiers créneaux de disponibilité.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        // Set default times when page loads
        document.addEventListener('DOMContentLoaded', function() {
            const startTimeInput = document.getElementById('startTime');
            const endTimeInput = document.getElementById('endTime');

            if (startTimeInput && !startTimeInput.value) {
                startTimeInput.value = '09:00';
            }
            if (endTimeInput && !endTimeInput.value) {
                endTimeInput.value = '17:00';
            }

            // Set minimum end time based on start time
            startTimeInput.addEventListener('change', function() {
                const startTime = this.value;
                if (startTime && endTimeInput) {
                    endTimeInput.min = startTime;
                    if (endTimeInput.value && endTimeInput.value <= startTime) {
                        endTimeInput.value = '';
                    }
                }
            });
        });
    </script>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
