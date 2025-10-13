<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nurse Dashboard - Medical TeleXpertise</title>
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
        
        .btn-primary {
            background: white;
            color: #667eea;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        
        .btn-secondary {
            background: rgba(255,255,255,0.2);
            color: white;
            border: 1px solid white;
        }
        
        .btn-secondary:hover {
            background: rgba(255,255,255,0.3);
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
        
        .alert {
            padding: 1rem 1.5rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        
        .alert-success {
            background: #d4edda;
            border-left: 4px solid #28a745;
            color: #155724;
        }
        
        .alert-error {
            background: #f8d7da;
            border-left: 4px solid #dc3545;
            color: #721c24;
        }
        
        .section {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            margin-bottom: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
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
            color: #333;
            font-size: 20px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th {
            background: #f8f9fa;
            padding: 12px;
            text-align: left;
            font-weight: 600;
            color: #555;
            border-bottom: 2px solid #dee2e6;
        }
        
        td {
            padding: 12px;
            border-bottom: 1px solid #dee2e6;
            color: #333;
        }
        
        tr:hover {
            background: #f8f9fa;
        }
        
        .badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .badge-waiting {
            background: #fff3cd;
            color: #856404;
        }
        
        .badge-progress {
            background: #cfe2ff;
            color: #084298;
        }
        
        .badge-done {
            background: #d1e7dd;
            color: #0f5132;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #999;
        }
        
        .empty-state svg {
            width: 80px;
            height: 80px;
            margin-bottom: 1rem;
            opacity: 0.3;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>🏥 Medical TeleXpertise - Nurse Dashboard</h1>
            <div class="user-info">
                <span>Welcome, ${sessionScope.user.firstName} ${sessionScope.user.lastName}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Logout</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">
                ✓ ${param.success}
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-error">
                ✗ ${param.error}
            </div>
        </c:if>
        
        <div class="welcome-section">
            <h2>Welcome to Your Dashboard</h2>
            <p>Manage patient registration, vital signs, and queue efficiently</p>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Patients Today</h3>
                <div class="number">${todayPatients.size()}</div>
            </div>
            <div class="stat-card">
                <h3>Waiting in Queue</h3>
                <div class="number">${waitingQueue.size()}</div>
            </div>
            <div class="stat-card">
                <h3>Quick Actions</h3>
                <a href="${pageContext.request.contextPath}/nurse/register-patient" class="btn btn-primary" style="margin-top: 0.5rem;">➕ Register Patient</a>
            </div>
        </div>
        
        <div class="section">
            <div class="section-header">
                <h2>⏱️ Current Waiting Queue</h2>
            </div>
            
            <c:choose>
                <c:when test="${not empty waitingQueue}">
                    <table>
                        <thead>
                            <tr>
                                <th>Position</th>
                                <th>Patient Name</th>
                                <th>CIN</th>
                                <th>Arrival Time</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="entry" items="${waitingQueue}" varStatus="status">
                                <tr>
                                    <td><strong>#${status.index + 1}</strong></td>
                                    <td>${entry.patient.firstName} ${entry.patient.lastName}</td>
                                    <td>${entry.patient.cin}</td>
                                    <td>
                                        <fmt:formatDate value="${entry.arrivalTime}" pattern="HH:mm" type="time"/>
                                    </td>
                                    <td><span class="badge badge-waiting">WAITING</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No patients currently waiting in queue</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <div class="section">
            <div class="section-header">
                <h2>📋 Today's Patients</h2>
            </div>
            
            <c:choose>
                <c:when test="${not empty todayPatients}">
                    <table>
                        <thead>
                            <tr>
                                <th>CIN</th>
                                <th>Name</th>
                                <th>SSN</th>
                                <th>Registered At</th>
                                <th>Latest Vitals</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="patient" items="${todayPatients}">
                                <tr>
                                    <td>${patient.cin}</td>
                                    <td>${patient.firstName} ${patient.lastName}</td>
                                    <td>${patient.socialSecurityNumber}</td>
                                    <td>
                                        <fmt:formatDate value="${patient.registeredAt}" pattern="HH:mm" type="time"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${patient.latestVitals.present}">
                                                T: ${patient.latestVitals.get().temperature}°C, 
                                                BP: ${patient.latestVitals.get().bloodPressure}
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #999;">No vitals recorded</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No patients registered today</p>
                        <a href="${pageContext.request.contextPath}/nurse/register-patient" class="btn btn-primary" style="margin-top: 1rem;">Register First Patient</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
