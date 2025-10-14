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

        .navbar .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
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

        .btn-logout {
            background: rgba(255,255,255,0.2);
            color: white;
            border: 1px solid white;
        }

        .btn-logout:hover {
            background: rgba(255,255,255,0.3);
            color: white;
        }

        .container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .welcome-section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        .welcome-section h2 {
            color: #333;
            margin-bottom: 0.5rem;
        }

        .welcome-section p {
            color: #666;
            margin: 0;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            border-left: 4px solid #667eea;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-card h3 {
            color: #666;
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }

        .stat-card .number {
            font-size: 32px;
            font-weight: 700;
            color: #667eea;
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

        .patient-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 1.5rem;
        }

        .patient-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 10px;
            padding: 1.5rem;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .patient-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
            border-color: #667eea;
        }

        .patient-header {
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
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-waiting {
            background: #fff3cd;
            color: #856404;
        }

        .waiting-time {
            color: #666;
            font-size: 14px;
            margin-bottom: 1rem;
        }

        .vital-signs {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 0.5rem;
            margin-bottom: 1rem;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 8px;
        }

        .vital-item {
            font-size: 13px;
            color: #555;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            width: 100%;
            padding: 10px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }

        .alert {
            padding: 1rem 1.5rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }

        .alert-info {
            background: #d1ecf1;
            border-left: 4px solid #0c5460;
            color: #0c5460;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #999;
        }

        .quick-actions {
            display: flex;
            gap: 1rem;
            margin-bottom: 2rem;
        }

        .quick-action-btn {
            flex: 1;
            padding: 1rem;
            background: white;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            text-decoration: none;
            color: #333;
            font-weight: 600;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .quick-action-btn:hover {
            border-color: #667eea;
            background: #f8f9ff;
            color: #667eea;
            transform: translateY(-2px);
        }

        .quick-action-btn i {
            font-size: 20px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>🏥 Medical TeleXpertise - Médecin Généraliste</h1>
            <div class="user-info">
                <span style="color: white;">Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-logout">
                    <i class="bi bi-box-arrow-right"></i> Déconnexion
                </a>
            </div>
        </div>
    </nav>

    <div class="container">
        <!-- Welcome Section -->
        <div class="welcome-section">
            <h2>Bienvenue, Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h2>
            <p>Tableau de bord - Gestion des consultations et demandes d'expertise</p>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Patients en Attente</h3>
                <div class="number">${waitingPatients.size()}</div>
            </div>
            <div class="stat-card" style="border-left-color: #28a745;">
                <h3>Consultations Aujourd'hui</h3>
                <div class="number" style="color: #28a745;">${not empty todayConsultationsCount ? todayConsultationsCount : 0}</div>
            </div>
            <div class="stat-card" style="border-left-color: #ffc107;">
                <h3>Expertises en Cours</h3>
                <div class="number" style="color: #ffc107;">${not empty pendingExpertiseCount ? pendingExpertiseCount : 0}</div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="quick-actions">
            <a href="${pageContext.request.contextPath}/generalist/consultations" class="quick-action-btn">
                <i class="bi bi-journal-medical"></i>
                <span>Mes Consultations</span>
            </a>
            <a href="${pageContext.request.contextPath}/generalist/expertise-requests" class="quick-action-btn">
                <i class="bi bi-chat-left-dots"></i>
                <span>Demandes d'Expertise</span>
            </a>
        </div>

        <!-- Waiting Patients Section -->
        <div class="section">
            <div class="section-header">
                <h2>📋 Patients en Attente de Consultation</h2>
            </div>

            <c:choose>
                <c:when test="${empty waitingPatients}">
                    <div class="empty-state">
                        <i class="bi bi-inbox" style="font-size: 48px; opacity: 0.3;"></i>
                        <p style="margin-top: 1rem;">Aucun patient en attente pour le moment</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="patient-grid">
                        <c:forEach var="entry" items="${waitingPatients}">
                            <div class="patient-card" onclick="window.location.href='${pageContext.request.contextPath}/generalist/patient/${entry.patient.id}'">
                                <div class="patient-header">
                                    <h3 class="patient-name">
                                        <i class="bi bi-person-fill" style="color: #667eea;"></i>
                                        ${entry.patient.firstName} ${entry.patient.lastName}
                                    </h3>
                                    <span class="status-badge status-waiting">EN ATTENTE</span>
                                </div>

                                <div class="waiting-time">
                                    <i class="bi bi-clock"></i> 
                                    Arrivé à: <fmt:formatDate value="${entry.displayArrivalTime}" pattern="HH:mm"/>
                                </div>

                                <c:if test="${not empty entry.patient.latestVitalSigns}">
                                    <div class="vital-signs">
                                        <div class="vital-item">
                                            <i class="bi bi-heart-pulse" style="color: #dc3545;"></i> 
                                            ${entry.patient.latestVitalSigns.bloodPressure}
                                        </div>
                                        <div class="vital-item">
                                            <i class="bi bi-activity" style="color: #17a2b8;"></i> 
                                            ${entry.patient.latestVitalSigns.heartRate} bpm
                                        </div>
                                        <div class="vital-item">
                                            <i class="bi bi-thermometer" style="color: #ffc107;"></i> 
                                            ${entry.patient.latestVitalSigns.temperature}°C
                                        </div>
                                        <div class="vital-item">
                                            <i class="bi bi-person"></i> 
                                            ${entry.patient.latestVitalSigns.weight} kg
                                        </div>
                                    </div>
                                </c:if>

                                <button class="btn btn-primary">
                                    <i class="bi bi-folder-open"></i> Ouvrir le Dossier
                                </button>
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
