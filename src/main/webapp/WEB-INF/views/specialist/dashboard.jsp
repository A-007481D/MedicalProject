<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spécialiste - Tableau de Bord</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .expertise-card {
            transition: transform 0.2s, box-shadow 0.2s;
            border-left: 4px solid #667eea;
        }
        .expertise-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .status-badge {
            font-size: 0.75rem;
            padding: 0.35em 0.65em;
        }
    </style>
</head>
<body class="bg-light">
<nav class="navbar" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
    <div class="container-fluid">
        <h1 class="text-white fs-4 fw-semibold">Medical TeleXpertise - Médecin Spécialiste</h1>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white">Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">Déconnexion</a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <div class="bg-white p-4 rounded shadow-sm mb-4">
        <h2 class="h4 fw-semibold">Bienvenue, Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h2>
        <p class="text-muted">Tableau de bord - Gestion des demandes d'expertise</p>
    </div>

    <div class="row g-4 mb-4">
        <div class="col-md-6">
            <div class="bg-white p-3 rounded shadow-sm" style="border-left: 4px solid #ffc107;">
                <h3 class="text-muted small fw-medium">Expertises en Attente</h3>
                <div class="d-flex justify-content-between align-items-center">
                    <div class="fs-3 fw-bold" style="color: #ffc107;">${not empty pendingCount ? pendingCount : 0}</div>
                    <a href="#pending-expertises" class="btn btn-sm btn-outline-warning">Voir</a>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="bg-white p-3 rounded shadow-sm" style="border-left: 4px solid #28a745;">
                <h3 class="text-muted small fw-medium">Expertises Aujourd'hui</h3>
                <div class="d-flex justify-content-between align-items-center">
                    <div class="fs-3 fw-bold" style="color: #28a745;">${not empty completedTodayCount ? completedTodayCount : 0}</div>
                    <a href="#completed-expertises" class="btn btn-sm btn-outline-success">Voir</a>
                </div>
            </div>
        </div>
    </div>

    <ul class="nav nav-tabs mb-4" id="dashboardTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="pending-tab" data-bs-toggle="tab" data-bs-target="#pending" type="button" role="tab">
                <i class="bi bi-hourglass-split me-2"></i>Expertises en Attente
                <span class="badge bg-warning text-dark ms-2">${not empty pendingCount ? pendingCount : 0}</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" id="completed-tab" data-bs-toggle="tab" data-bs-target="#completed" type="button" role="tab">
                <i class="bi bi-check-circle me-2"></i>Expertises Aujourd'hui
                <span class="badge bg-success ms-2">${not empty completedTodayCount ? completedTodayCount : 0}</span>
            </button>
        </li>
    </ul>

    <div class="tab-content" id="dashboardTabsContent">
        <!-- Pending Expertises Tab -->
        <div class="tab-pane fade show active" id="pending" role="tabpanel">
            <div class="bg-white p-4 rounded shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                    <h2 class="h5 fw-semibold"><i class="bi bi-hourglass-split me-2"></i>Expertises en Attente de Traitement</h2>
                </div>

                <c:choose>
                    <c:when test="${empty pendingExpertises}">
                        <div class="text-center p-5 text-muted">
                            <i class="bi bi-inbox fs-1 opacity-25"></i>
                            <p class="mt-3">Aucune expertise en attente pour le moment</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <c:forEach var="expertise" items="${pendingExpertises}">
                                <div class="col-lg-6">
                                    <div class="expertise-card bg-white p-4 rounded shadow-sm h-100">
                                        <div class="d-flex justify-content-between align-items-start mb-3">
                                            <div>
                                                <h3 class="h5 fw-semibold mb-1">
                                                    <i class="bi bi-person-fill text-primary"></i>
                                                    ${expertise.consultation.patient.firstName} ${expertise.consultation.patient.lastName}
                                                </h3>
                                                <div class="text-muted small mb-2">
                                                    <i class="bi bi-calendar3"></i> Demande le 
                                                    <fmt:formatDate value="${expertise.createdAt}" pattern="dd/MM/yyyy 'à' HH:mm" />
                                                </div>
                                                <div class="mb-2">
                                                    <span class="badge bg-warning text-dark status-badge">
                                                        <i class="bi bi-hourglass-split"></i> En attente
                                                    </span>
                                                    <span class="badge bg-info text-white status-badge">
                                                        ${expertise.specialty}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <div class="fw-medium">Motif :</div>
                                            <p class="mb-2">${expertise.consultation.motif}</p>
                                            <div class="fw-medium">Notes du médecin généraliste :</div>
                                            <p class="mb-0 text-muted">
                                                ${not empty expertise.consultation.notes ? expertise.consultation.notes : 'Aucune note supplémentaire'}
                                            </p>
                                        </div>
                                        
                                        <div class="d-flex gap-2">
                                            <a href="${pageContext.request.contextPath}/specialist/expertise/${expertise.id}" 
                                               class="btn btn-primary flex-grow-1">
                                                <i class="bi bi-pencil-square"></i> Traiter la demande
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Completed Expertises Tab -->
        <div class="tab-pane fade" id="completed" role="tabpanel">
            <div class="bg-white p-4 rounded shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                    <h2 class="h5 fw-semibold"><i class="bi bi-check-circle me-2"></i>Expertises Traitées Aujourd'hui</h2>
                </div>

                <c:choose>
                    <c:when test="${empty completedTodayExpertises}">
                        <div class="text-center p-5 text-muted">
                            <i class="bi bi-check-circle fs-1 opacity-25"></i>
                            <p class="mt-3">Aucune expertise traitée aujourd'hui</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Patient</th>
                                        <th>Spécialité</th>
                                        <th>Terminée à</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="expertise" items="${completedTodayExpertises}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <i class="bi bi-person-circle me-2"></i>
                                                    ${expertise.consultation.patient.firstName} ${expertise.consultation.patient.lastName}
                                                </div>
                                            </td>
                                            <td>
                                                <span class="badge bg-info">
                                                    ${expertise.specialty}
                                                </span>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${expertise.updatedAt}" pattern="HH:mm" />
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/specialist/expertise/${expertise.id}" 
                                                   class="btn btn-sm btn-outline-primary">
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
    // Activate the first tab by default
    document.addEventListener('DOMContentLoaded', function() {
        var firstTabEl = document.querySelector('#dashboardTabs .nav-link');
        var firstTab = new bootstrap.Tab(firstTabEl);
        firstTabEl.addEventListener('click', function (event) {
            event.preventDefault();
            firstTab.show();
        });
    });
</script>
</body>
</html>
