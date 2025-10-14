<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demandes d'Expertise</title>
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
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-bottom: 2rem;
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid #f0f0f0;
        }

        .section-header h2 {
            font-size: 24px;
            color: #333;
            margin: 0;
        }

        .expertise-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
            gap: 1.5rem;
        }

        .expertise-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 10px;
            padding: 1.5rem;
            transition: all 0.3s ease;
        }

        .expertise-card:hover {
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
            border-color: #667eea;
        }

        .expertise-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 1rem;
        }

        .patient-name {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin: 0;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-answered {
            background: #d1e7dd;
            color: #0f5132;
        }

        .status-scheduled {
            background: #cfe2ff;
            color: #084298;
        }

        .expertise-info {
            margin-bottom: 1rem;
            color: #666;
            font-size: 14px;
        }

        .expertise-info div {
            margin-bottom: 0.5rem;
        }

        .question-box {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
        }

        .question-box h4 {
            font-size: 14px;
            color: #555;
            margin-bottom: 0.5rem;
        }

        .question-box p {
            font-size: 13px;
            color: #666;
            margin: 0;
        }

        .btn-view {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            width: 100%;
            padding: 10px;
            border-radius: 6px;
            font-weight: 600;
            text-align: center;
            transition: all 0.3s ease;
        }

        .btn-view:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #999;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>💬 Demandes d'Expertise</h1>
            <a href="${pageContext.request.contextPath}/dashboard/generalist" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Retour au tableau de bord
            </a>
        </div>
    </nav>

    <div class="container">
        <div class="section">
            <div class="section-header">
                <h2>Suivi des Demandes d'Expertise</h2>
            </div>

            <c:choose>
                <c:when test="${empty expertiseRequests}">
                    <div class="empty-state">
                        <i class="bi bi-chat-left-dots" style="font-size: 48px; opacity: 0.3;"></i>
                        <p style="margin-top: 1rem;">Aucune demande d'expertise enregistrée</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="expertise-grid">
                        <c:forEach var="expertise" items="${expertiseRequests}">
                            <div class="expertise-card">
                                <div class="expertise-header">
                                    <h3 class="patient-name">
                                        <i class="bi bi-person-fill" style="color: #667eea;"></i>
                                        ${expertise.consultation.patient.firstName} ${expertise.consultation.patient.lastName}
                                    </h3>
                                    <c:choose>
                                        <c:when test="${expertise.status == 'PENDING'}">
                                            <span class="status-badge status-pending">En attente</span>
                                        </c:when>
                                        <c:when test="${expertise.status == 'SCHEDULED'}">
                                            <span class="status-badge status-scheduled">Planifiée</span>
                                        </c:when>
                                        <c:when test="${expertise.status == 'ANSWERED'}">
                                            <span class="status-badge status-answered">Répondue</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge">${expertise.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="expertise-info">
                                    <div>
                                        <i class="bi bi-calendar"></i>
                                        <strong>Date de demande:</strong> 
                                        <fmt:formatDate value="${expertise.requestedAt}" pattern="dd/MM/yyyy" type="date"/>
                                    </div>
                                    <div>
                                        <i class="bi bi-star"></i>
                                        <strong>Spécialité:</strong> ${expertise.specializationNeeded}
                                    </div>
                                    <c:if test="${not empty expertise.specialistAssigned}">
                                        <div>
                                            <i class="bi bi-person-badge"></i>
                                            <strong>Spécialiste:</strong> 
                                            Dr. ${expertise.specialistAssigned.firstName} ${expertise.specialistAssigned.lastName}
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty expertise.scheduledSlotStart}">
                                        <div>
                                            <i class="bi bi-clock"></i>
                                            <strong>Créneau:</strong> 
                                            <fmt:formatDate value="${expertise.scheduledSlotStart}" pattern="dd/MM/yyyy HH:mm" type="both"/>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="question-box">
                                    <h4>Question posée:</h4>
                                    <p>${expertise.question}</p>
                                </div>

                                <c:if test="${expertise.status == 'ANSWERED' && not empty expertise.expertOpinion}">
                                    <div class="question-box" style="background: #d1f2eb;">
                                        <h4>Avis du spécialiste:</h4>
                                        <p>${expertise.expertOpinion}</p>
                                    </div>
                                </c:if>

                                <a href="${pageContext.request.contextPath}/generalist/consultation/${expertise.consultation.id}" 
                                   class="btn-view">
                                    <i class="bi bi-folder-open"></i> Voir la Consultation
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
