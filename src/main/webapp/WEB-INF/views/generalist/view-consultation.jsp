<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consultation - ${consultation.id}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }

        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .navbar-content {
            max-width: 1400px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .navbar h1 {
            font-size: 24px;
            font-weight: 600;
            color: white;
            margin: 0;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }

        .btn-back {
            background: rgba(255,255,255,0.2);
            color: white;
            border: 1px solid white;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.3);
            color: white;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-bottom: 2rem;
            overflow: hidden;
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .card-header h2 {
            margin: 0;
            font-size: 1.5rem;
        }

        .card-body {
            padding: 2rem;
        }

        .section {
            margin-bottom: 2.5rem;
        }

        .section-title {
            font-size: 1.25rem;
            color: #444;
            margin-bottom: 1.5rem;
            padding-bottom: 0.75rem;
            border-bottom: 2px solid #f0f0f0;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .info-item {
            background: #f8f9fa;
            padding: 1.25rem;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }

        .info-label {
            font-size: 0.85rem;
            color: #666;
            margin-bottom: 0.5rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .info-value {
            font-size: 1.1rem;
            font-weight: 500;
            color: #333;
        }

        .status-badge {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            display: inline-block;
        }

        .status-pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-in-progress {
            background: #cfe2ff;
            color: #084298;
        }

        .status-completed {
            background: #d1e7dd;
            color: #0f5132;
        }

        .status-waiting {
            background: #f8d7da;
            color: #842029;
        }

        .btn-action {
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 500;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.2s;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .btn-outline-secondary {
            background: white;
            color: #6c757d;
            border: 1px solid #dee2e6;
        }

        .btn-outline-secondary:hover {
            background: #f8f9fa;
            color: #495057;
        }

        .action-buttons {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid #eee;
        }

        .expertise-section {
            background: #f8f9ff;
            border-left: 4px solid #667eea;
            padding: 1.5rem;
            border-radius: 8px;
            margin-top: 2rem;
        }

        .expertise-section h4 {
            color: #667eea;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .expertise-section p {
            margin-bottom: 0.5rem;
            color: #555;
        }

        .expertise-details {
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px dashed #dee2e6;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>📋 Consultation #${consultation.id}</h1>
            <a href="${pageContext.request.contextPath}/generalist/consultations" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Retour aux consultations
            </a>
        </div>
    </nav>

    <div class="container">
        <div class="card">
            <div class="card-header">
                <h2>Détails de la consultation</h2>
                <div>
                    <c:choose>
                        <c:when test="${consultation.status == 'PENDING'}">
                            <span class="status-badge status-pending">En attente</span>
                        </c:when>
                        <c:when test="${consultation.status == 'IN_PROGRESS'}">
                            <span class="status-badge status-in-progress">En cours</span>
                        </c:when>
                        <c:when test="${consultation.status == 'COMPLETED'}">
                            <span class="status-badge status-completed">Terminée</span>
                        </c:when>
                        <c:when test="${consultation.status == 'WAITING_SPECIALIST_OPINION'}">
                            <span class="status-badge status-waiting">En attente d'expertise</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-badge">${consultation.status}</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="card-body">
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Date de la consultation</div>
                        <div class="info-value">
                            <fmt:formatDate value="${consultation.createdAtAsDate}" pattern="EEEE dd MMMM yyyy 'à' HH'h'mm" type="both" />
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Patient</div>
                        <div class="info-value">
                            ${consultation.patient.firstName} ${consultation.patient.lastName}
                            <c:if test="${not empty consultation.patient.phone}">
                                <div style="font-size: 0.9rem; color: #666; margin-top: 4px;">
                                    ${consultation.patient.phone}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Médecin généraliste</div>
                        <div class="info-value">
                            Dr. ${consultation.generalist.firstName} ${consultation.generalist.lastName}
                        </div>
                    </div>
                </div>

                <div class="section">
                    <h3 class="section-title">Motif de consultation</h3>
                    <div style="background: #f8f9fa; padding: 1.5rem; border-radius: 8px;">
                        ${consultation.motif}
                    </div>
                </div>

                <c:if test="${not empty consultation.observations}">
                    <div class="section">
                        <h3 class="section-title">Observations</h3>
                        <div style="background: #f8f9fa; padding: 1.5rem; border-radius: 8px; white-space: pre-line;">
                            ${consultation.observations}
                        </div>
                    </div>
                </c:if>



                <c:if test="${hasExpertise}">
                    <div class="expertise-section">
                        <h4><i class="bi bi-stars"></i> Demande d'expertise</h4>
                        <div class="expertise-details">
                            <p><strong>Spécialité :</strong> ${consultation.expertiseRequest.specialty}</p>
                            <p><strong>Question :</strong> ${consultation.expertiseRequest.question}</p>
                            <p><strong>Priorité :</strong> ${consultation.expertiseRequest.priority}</p>
                            <p><strong>Statut :</strong> 
                                <c:choose>
                                    <c:when test="${consultation.expertiseRequest.status == 'PENDING'}">
                                        <span class="status-badge status-pending">En attente</span>
                                    </c:when>
                                    <c:when test="${consultation.expertiseRequest.status == 'IN_PROGRESS'}">
                                        <span class="status-badge status-in-progress">En cours</span>
                                    </c:when>
                                    <c:when test="${consultation.expertiseRequest.status == 'COMPLETED'}">
                                        <span class="status-badge status-completed">Terminée</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${consultation.expertiseRequest.status}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            
                            <c:if test="${not empty consultation.expertiseRequest.response}">
                                <div style="margin-top: 1rem; padding: 1rem; background: white; border-radius: 6px; border: 1px solid #eee;">
                                    <h5 style="font-size: 1rem; margin-bottom: 0.75rem; color: #444;">Réponse du spécialiste :</h5>
                                    <div style="white-space: pre-line;">${consultation.expertiseRequest.response}</div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>

                <div class="action-buttons">
                    <!-- Debug information (can be removed later) -->
                    <div style="display: none;">
                        Status: ${consultation.status}<br>
                        Has expertise: ${not empty consultation.expertiseRequest}
                    </div>
                    
                    <c:if test="${consultation.status != 'COMPLETED' and consultation.status != 'WAITING_SPECIALIST_OPINION'}">
                        <a href="${pageContext.request.contextPath}/generalist/consultations/${consultation.id}/edit" class="btn btn-primary">
                            <i class="bi bi-pencil"></i> Modifier
                        </a>
                        
                        <c:if test="${empty consultation.expertiseRequest}">
                            <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}" class="btn btn-outline-secondary">
                                <i class="bi bi-stars"></i> Demander une expertise
                            </a>
                        </c:if>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/generalist/consultations" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Retour à la liste
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
