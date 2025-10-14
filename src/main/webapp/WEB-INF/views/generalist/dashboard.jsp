<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Généraliste - Tableau de Bord</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .patient-card {
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .patient-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .waiting-time {
            font-size: 0.9rem;
            color: #6c757d;
        }
        .urgent {
            border-left: 4px solid #dc3545;
        }
        .vital-signs {
            font-size: 0.85rem;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">
                <i class="bi bi-hospital"></i> Télé-Expertise Médicale
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-white me-3">
                    <i class="bi bi-person-circle"></i> 
                    Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right"></i> Déconnexion
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2">
                <div class="list-group">
                    <a href="${pageContext.request.contextPath}/dashboard/generalist" 
                       class="list-group-item list-group-item-action active">
                        <i class="bi bi-list-task"></i> Patients en attente
                    </a>
                    <a href="${pageContext.request.contextPath}/generalist/consultations" 
                       class="list-group-item list-group-item-action">
                        <i class="bi bi-journal-medical"></i> Mes consultations
                    </a>
                    <a href="${pageContext.request.contextPath}/generalist/expertise-requests" 
                       class="list-group-item list-group-item-action">
                        <i class="bi bi-chat-left-dots"></i> Demandes d'expertise
                    </a>
                </div>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="bi bi-people"></i> Patients en Attente</h2>
                    <div class="badge bg-primary fs-5">
                        ${waitingPatients.size()} patient(s)
                    </div>
                </div>

                <c:if test="${empty waitingPatients}">
                    <div class="alert alert-info" role="alert">
                        <i class="bi bi-info-circle"></i> Aucun patient en attente pour le moment.
                    </div>
                </c:if>

                <div class="row">
                    <c:forEach var="entry" items="${waitingPatients}">
                        <div class="col-md-6 col-lg-4 mb-3">
                            <div class="card patient-card h-100">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="card-title mb-0">
                                            <i class="bi bi-person-fill text-primary"></i>
                                            ${entry.patient.firstName} ${entry.patient.lastName}
                                        </h5>
                                        <span class="badge bg-warning text-dark">
                                            ${entry.status}
                                        </span>
                                    </div>

                                    <p class="waiting-time mb-2">
                                        <i class="bi bi-clock"></i> 
                                        Arrivé: <fmt:formatDate value="${entry.displayArrivalTime}" pattern="HH:mm"/>
                                    </p>

                                    <div class="vital-signs mb-3">
                                        <c:if test="${not empty entry.patient.latestVitalSigns}">
                                            <div class="row g-2">
                                                <div class="col-6">
                                                    <i class="bi bi-heart-pulse text-danger"></i> 
                                                    ${entry.patient.latestVitalSigns.bloodPressure}
                                                </div>
                                                <div class="col-6">
                                                    <i class="bi bi-activity text-info"></i> 
                                                    ${entry.patient.latestVitalSigns.heartRate} bpm
                                                </div>
                                                <div class="col-6">
                                                    <i class="bi bi-thermometer text-warning"></i> 
                                                    ${entry.patient.latestVitalSigns.temperature}°C
                                                </div>
                                                <div class="col-6">
                                                    <i class="bi bi-person"></i> 
                                                    ${entry.patient.latestVitalSigns.weight} kg
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="d-grid">
                                        <a href="${pageContext.request.contextPath}/generalist/patient/${entry.patient.id}" 
                                           class="btn btn-primary">
                                            <i class="bi bi-folder-open"></i> Ouvrir le dossier
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
