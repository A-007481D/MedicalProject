<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vital Signs - Medical TeleXpertise</title>
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
            max-width: 1000px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .navbar h1 {
            font-size: 24px;
            font-weight: 600;
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
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        .container {
            max-width: 1000px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .card {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-bottom: 2rem;
        }
        
        .card h2 {
            color: #333;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid #f0f0f0;
        }
        
        .patient-info {
            background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            border-left: 4px solid #667eea;
        }
        
        .patient-info h3 {
            color: #667eea;
            margin-bottom: 0.5rem;
        }
        
        .patient-info p {
            margin: 0.3rem 0;
            color: #555;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #555;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
        }
        
        .alert {
            padding: 1rem 1.5rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        
        .alert-error {
            background: #f8d7da;
            border-left: 4px solid #dc3545;
            color: #721c24;
        }
        
        .required {
            color: #dc3545;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .help-text {
            font-size: 12px;
            color: #999;
            margin-top: 0.3rem;
        }
        
        .vital-icon {
            display: inline-block;
            margin-right: 0.5rem;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>🩺 Record Vital Signs</h1>
            <a href="${pageContext.request.contextPath}/dashboard/nurse" class="btn btn-secondary">← Back to Dashboard</a>
        </div>
    </nav>
    
    <div class="container">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ✗ ${errorMessage}
            </div>
        </c:if>
        
        <div class="card">
            <h2>Patient Information</h2>
            <div class="patient-info">
                <h3>👤 ${patient.firstName} ${patient.lastName}</h3>
                <p><strong>CIN:</strong> ${patient.cin}</p>
                <c:if test="${not empty patient.socialSecurityNumber}">
                    <p><strong>SSN:</strong> ${patient.socialSecurityNumber}</p>
                </c:if>
                <c:if test="${not empty patient.birthDate}">
                    <p><strong>Date of Birth:</strong> ${patient.birthDate}</p>
                </c:if>
            </div>
        </div>
        
        <div class="card">
            <h2>📊 Record Vital Signs</h2>
            <p style="color: #666; margin-bottom: 2rem;">
                Please measure and record all vital signs accurately. After submitting, 
                the patient will be automatically added to the waiting queue for consultation.
            </p>
            
            <form method="post" action="${pageContext.request.contextPath}/nurse/vital-signs">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <input type="hidden" name="patientId" value="${patient.id}">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="temperature">
                            <span class="vital-icon">🌡️</span>
                            Temperature (°C) <span class="required">*</span>
                        </label>
                        <input type="number" id="temperature" name="temperature" 
                               step="0.1" min="35" max="45" required
                               placeholder="37.0">
                        <div class="help-text">Normal: 36.1°C - 37.2°C</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="pulse">
                            <span class="vital-icon">❤️</span>
                            Heart Rate (bpm) <span class="required">*</span>
                        </label>
                        <input type="number" id="pulse" name="pulse" 
                               min="40" max="200" required
                               placeholder="75">
                        <div class="help-text">Normal: 60-100 bpm</div>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="bloodPressure">
                            <span class="vital-icon">🩸</span>
                            Blood Pressure <span class="required">*</span>
                        </label>
                        <input type="text" id="bloodPressure" name="bloodPressure" 
                               required pattern="[0-9]{2,3}/[0-9]{2,3}"
                               placeholder="120/80">
                        <div class="help-text">Format: Systolic/Diastolic (e.g., 120/80)</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="respiratoryRate">
                            <span class="vital-icon">🫁</span>
                            Respiratory Rate (per min) <span class="required">*</span>
                        </label>
                        <input type="number" id="respiratoryRate" name="respiratoryRate" 
                               min="10" max="60" required
                               placeholder="16">
                        <div class="help-text">Normal: 12-20 per minute</div>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="weight">
                            <span class="vital-icon">⚖️</span>
                            Weight (kg)
                        </label>
                        <input type="number" id="weight" name="weight" 
                               step="0.1" min="0" max="500"
                               placeholder="70.5">
                        <div class="help-text">Optional but recommended</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="height">
                            <span class="vital-icon">📏</span>
                            Height (cm)
                        </label>
                        <input type="number" id="height" name="height" 
                               step="0.1" min="0" max="300"
                               placeholder="175">
                        <div class="help-text">Optional but recommended</div>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        ✅ Save Vital Signs & Add to Queue
                    </button>
                    <a href="${pageContext.request.contextPath}/nurse/register-patient" 
                       class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
        
        <div class="card" style="background: #fff9e6; border-left: 4px solid #ffc107;">
            <h3 style="color: #856404; margin-bottom: 1rem;">💡 Important Notes</h3>
            <ul style="color: #856404; padding-left: 1.5rem;">
                <li style="margin: 0.5rem 0;">Ensure all measurements are accurate</li>
                <li style="margin: 0.5rem 0;">Fields marked with <span class="required">*</span> are required</li>
                <li style="margin: 0.5rem 0;">Patient will be automatically added to the waiting queue after submission</li>
                <li style="margin: 0.5rem 0;">A generalist will consult the patient in queue order</li>
            </ul>
        </div>
    </div>
</body>
</html>
