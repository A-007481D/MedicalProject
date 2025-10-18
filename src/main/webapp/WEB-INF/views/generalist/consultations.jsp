<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Consultations</title>
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

        .consultation-table {
            width: 100%;
            border-collapse: collapse;
        }

        .consultation-table thead {
            background: #f8f9fa;
        }

        .consultation-table th {
            padding: 1rem;
            text-align: left;
            font-weight: 600;
            color: #555;
            border-bottom: 2px solid #e0e0e0;
        }

        .consultation-table td {
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;
            vertical-align: middle;
        }

        .consultation-table tbody tr {
            transition: background 0.2s ease;
        }

        .consultation-table tbody tr:hover {
            background: #f8f9ff;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
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

        .btn-view {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 14px;
            text-decoration: none;
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
            <h1>📋 Mes Consultations</h1>
            <a href="${pageContext.request.contextPath}/dashboard/generalist" class="btn btn-back">
                <i class="bi bi-arrow-left"></i> Retour au tableau de bord
            </a>
        </div>
    </nav>

    <div class="container">
        <div class="section">
            <div class="section-header">
                <h2>Historique des Consultations</h2>
            </div>

            <c:choose>
                <c:when test="${empty consultations}">
                    <div class="empty-state">
                        <i class="bi bi-journal-medical" style="font-size: 48px; opacity: 0.3;"></i>
                        <p style="margin-top: 1rem;">Aucune consultation enregistrée</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="consultation-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Patient</th>
                                <th>Motif</th>
                                <th>Statut</th>
                                <th>Expertise</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="consultation" items="${consultations}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${consultation.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm" type="both"/>
                                    </td>
                                    <td>
                                        <strong>${consultation.patient.firstName} ${consultation.patient.lastName}</strong>
                                    </td>
                                    <td>${consultation.motif}</td>
                                    <td>
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
                                                <span class="status-badge status-waiting">Attente expertise</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge">${consultation.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty consultation.expertiseRequest}">
                                                <i class="bi bi-check-circle-fill" style="color: #28a745;"></i> Oui
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-dash-circle" style="color: #6c757d;"></i> Non
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/generalist/consultation/${consultation.id}" 
                                           class="btn-view">
                                            <i class="bi bi-eye"></i> Voir
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
