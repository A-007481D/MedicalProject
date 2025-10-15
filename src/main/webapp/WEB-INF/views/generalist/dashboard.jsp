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
</head>
<body class="bg-light">
<nav class="navbar" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
    <div class="container-fluid">
        <h1 class="text-white fs-4 fw-semibold">Medical TeleXpertise - Médecin Généraliste</h1>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white">Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">Logout</a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <div class="bg-white p-4 rounded shadow-sm mb-4">
        <h2 class="h4 fw-semibold">Bienvenue, Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h2>
        <p class="text-muted">Tableau de bord - Gestion des consultations et demandes d'expertise</p>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="bg-white p-3 rounded shadow-sm" style="border-left: 4px solid #667eea;">
                <h3 class="text-muted small fw-medium">Patients en Attente</h3>
                <div class="fs-3 fw-bold" style="color: #667eea;">${waitingPatients.size()}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="bg-white p-3 rounded shadow-sm" style="border-left: 4px solid #ffc107;">
                <h3 class="text-muted small fw-medium">Expertises en Cours</h3>
                <div class="fs-3 fw-bold" style="color: #ffc107;">${not empty pendingExpertiseCount ? pendingExpertiseCount : 0}</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="bg-white p-3 rounded shadow-sm" style="border-left: 4px solid #28a745;">
                <h3 class="text-muted small fw-medium">Consultations Aujourd'hui</h3>
                <div class="fs-3 fw-bold" style="color: #28a745;">${not empty todayConsultationsCount ? todayConsultationsCount : 0}</div>
            </div>
        </div>
    </div>

    <div class="d-flex gap-3 mb-4">
        <a href="${pageContext.request.contextPath}/generalist/consultations" class="flex-fill btn btn-outline-primary d-flex align-items-center gap-2 p-3">
            <i class="bi bi-journal-medical fs-5"></i> Mes Consultations
        </a>
        <a href="${pageContext.request.contextPath}/generalist/expertise-requests" class="flex-fill btn btn-outline-primary d-flex align-items-center gap-2 p-3">
            <i class="bi bi-chat-left-dots fs-5"></i> Demandes d'Expertise
        </a>
    </div>

    <ul class="nav nav-tabs mb-4" id="dashboardTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="waiting-tab" data-bs-toggle="tab" data-bs-target="#waiting" type="button" role="tab">
                <i class="bi bi-people-fill me-2"></i>Patients en Attente
                <span class="badge bg-primary ms-2">${not empty waitingPatients ? waitingPatients.size() : 0}</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="consultations-tab" data-bs-toggle="tab" data-bs-target="#consultations" type="button" role="tab">
                <i class="bi bi-journal-medical me-2"></i>Consultations du Jour
                <span class="badge bg-success ms-2">${not empty todayConsultationsCount ? todayConsultationsCount : 0}</span>
            </button>
        </li>
    </ul>

    <div class="tab-content" id="dashboardTabsContent">
        <div class="tab-pane fade show active" id="waiting" role="tabpanel">
            <div class="bg-white p-4 rounded shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                    <h2 class="h5 fw-semibold"><i class="bi bi-people-fill me-2"></i>Patients en Attente de Consultation</h2>
                </div>

                <c:choose>
                    <c:when test="${empty waitingPatients}">
                        <div class="text-center p-5 text-muted">
                            <i class="bi bi-inbox fs-1 opacity-25"></i>
                            <p class="mt-3">Aucun patient en attente pour le moment</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <c:forEach var="entry" items="${waitingPatients}">
                                <div class="col-lg-4 col-md-6">
                                    <div class="border rounded p-3" style="cursor: pointer; border-color: #e0e0e0 !important;" onclick="window.location.href='${pageContext.request.contextPath}/generalist/patient/${entry.patient.id}'">
                                        <div class="d-flex justify-content-between align-items-start mb-3">
                                            <h3 class="h6 fw-semibold">
                                                <i class="bi bi-person-fill text-primary"></i>
                                                    ${entry.patient.firstName} ${entry.patient.lastName}
                                            </h3>
                                            <span class="badge rounded-pill bg-warning text-dark">EN ATTENTE</span>
                                        </div>

                                        <div class="text-muted small mb-3">
                                            <i class="bi bi-clock"></i>
<%--                                            <c:if test="${not empty entry.waitingDuration}">--%>
<%--                                                Arrivé à: <fmt:formatDate value="${entry.displayArrivalTime}" pattern="HH:mm"/>--%>
<%--                                            </c:if>--%>
                                        </div>

                                        <c:if test="${not empty entry.patient.latestVitalSigns}">
                                            <div class="bg-light rounded p-3 mb-3 row g-2 small text-muted">
                                                <div class="col-6">
                                                    <i class="bi bi-heart-pulse text-danger"></i>
                                                        ${entry.patient.latestVitalSigns.bloodPressure}
                                                </div>
                                                <div class="col-6">
                                                    <i class="bi bi-activity text-info"></i>
                                                        ${entry.patient.latestVitalSigns.pulse} bpm
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

                                        <button class="btn btn-primary w-100" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none;">
                                            <i class="bi bi-folder-open"></i> Ouvrir le Dossier
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="tab-pane fade" id="consultations" role="tabpanel">
            <div class="bg-white p-4 rounded shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                    <h2 class="h5 fw-semibold"><i class="bi bi-journal-medical me-2"></i>Consultations du Jour</h2>
                </div>

                <c:choose>
                    <c:when test="${empty todayConsultations or todayConsultations.isEmpty()}">
                        <div class="text-center p-5 text-muted">
                            <i class="bi bi-calendar-x fs-1 opacity-25"></i>
                            <p class="mt-3">Aucune consultation prévue pour aujourd'hui</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-light">
                                <tr>
                                    <th>Patient</th>
                                    <th>Heure</th>
                                    <th>Motif</th>
                                    <th>Statut</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="consultation" items="${todayConsultations}">
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <i class="bi bi-person-circle me-2"></i>
                                                    ${consultation.patient.firstName} ${consultation.patient.lastName}
                                            </div>
                                        </td>
                                        <td>
                                            <c:if test="${not empty consultation.createdAt}">
                                                <fmt:formatDate value="${consultation.createdAt}" pattern="HH:mm" />
                                            </c:if>
                                        </td>
                                        <td>${consultation.motif}</td>
                                        <td>
                                                <span class="badge bg-${consultation.status == 'COMPLETED' ? 'success' : 'warning'}">
                                                        ${consultation.status}
                                                </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/generalist/consultation/${consultation.id}"
                                               class="btn btn-sm btn-primary">
                                                <i class="bi bi-eye"></i> Voir
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });

        var tabEls = document.querySelectorAll('button[data-bs-toggle="tab"]');
        tabEls.forEach(function(tabEl) {
            tabEl.addEventListener('shown.bs.tab', function (e) {
                console.log('Tab changed to:', e.target.getAttribute('aria-controls'));
            });
        });
    });
</script>
</body>
</html>