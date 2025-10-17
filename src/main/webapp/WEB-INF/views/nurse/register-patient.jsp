<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register Patient - Medical TeleXpertise</title>
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
            max-width: 1200px;
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
            max-width: 1200px;
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
        
        .search-section {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
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
        
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
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
        
        .alert-info {
            background: #d1ecf1;
            border-left: 4px solid #17a2b8;
            color: #0c5460;
        }
        
        .patient-info {
            background: #e7f3ff;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        
        .patient-info h3 {
            color: #0066cc;
            margin-bottom: 1rem;
        }
        
        .patient-info p {
            margin: 0.5rem 0;
            color: #333;
        }
        
        .required {
            color: #dc3545;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 2rem;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-content">
            <h1>🏥 Patient Registration</h1>
            <a href="${pageContext.request.contextPath}/dashboard/nurse" class="btn btn-secondary">← Back to Dashboard</a>
        </div>
    </nav>
    
    <div class="container">
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ✗ ${errorMessage}
            </div>
        </c:if>
        
        <c:if test="${not empty searchError}">
            <div class="alert alert-error">
                ✗ ${searchError}
            </div>
        </c:if>
        
        <div class="card">
            <h2>🔍 Step 1: Search for Existing Patient</h2>
            <div class="search-section">
                <form method="post" action="${pageContext.request.contextPath}/nurse/register-patient">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="action" value="search">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="identifier">Search by CIN or Social Security Number</label>
                            <input type="text" id="identifier" name="identifier" 
                                   placeholder="Enter CIN or SSN" required>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">🔍 Search Patient</button>
                </form>
            </div>
        </div>
        
        <c:if test="${searchSuccess and not empty foundPatient}">
            <div class="card">
                <h2>✅ Patient Found</h2>
                <div class="patient-info">
                    <h3>${foundPatient.firstName} ${foundPatient.lastName}</h3>
                    <p><strong>CIN:</strong> ${foundPatient.cin}</p>
                    <p><strong>SSN:</strong> ${foundPatient.socialSecurityNumber}</p>
                    <p><strong>Date of Birth:</strong> ${foundPatient.birthDate}</p>
                    <p><strong>Phone:</strong> ${foundPatient.phone}</p>
                </div>
                
                <div class="alert alert-info">
                    ℹ️ Patient exists in the system. Click below to add new vital signs and add to queue.
                </div>
                
                <a href="${pageContext.request.contextPath}/nurse/vital-signs?patientId=${foundPatient.id}" 
                   class="btn btn-primary">Add Vital Signs & Queue →</a>
            </div>
        </c:if>
        
        <div class="card">
            <h2>➕ Step 2: Register New Patient</h2>
            <p style="color: #666; margin-bottom: 1.5rem;">
                If patient not found, fill in the form below to register a new patient
            </p>
            
            <form method="post" action="${pageContext.request.contextPath}/nurse/register-patient">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <input type="hidden" name="action" value="registerNew">
                
                <h3 style="color: #667eea; margin-bottom: 1rem;">📋 Administrative Information</h3>
                <div class="form-row">
                    <div class="form-group">
                        <label for="cin">CIN <span class="required">*</span></label>
                        <input type="text" id="cin" name="cin" required>
                    </div>
                    <div class="form-group">
                        <label for="ssn">Social Security Number</label>
                        <input type="text" id="ssn" name="ssn">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">First Name <span class="required">*</span></label>
                        <input type="text" id="firstName" name="firstName" required>
                    </div>
                    <div class="form-group">
                        <label for="lastName">Last Name <span class="required">*</span></label>
                        <input type="text" id="lastName" name="lastName" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="birthDate">Date of Birth</label>
                        <input type="date" id="birthDate" name="birthDate">
                    </div>
                    <div class="form-group">
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender">
                            <option value="">Select Gender</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </select>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="phone">Phone</label>
                        <input type="tel" id="phone" name="phone">
                    </div>
                    <div class="form-group">
                        <label for="mutuelle">Health Insurance (Mutuelle)</label>
                        <input type="text" id="mutuelle" name="mutuelle">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="address">Address</label>
                    <input type="text" id="address" name="address">
                </div>
                
                <h3 style="color: #667eea; margin: 2rem 0 1rem;">🏥 Medical Information</h3>
                
                <div class="form-group">
                    <label for="antecedents">Medical History (Antécédents)</label>
                    <textarea id="antecedents" name="antecedents" 
                              placeholder="Previous illnesses, surgeries, chronic conditions..."></textarea>
                </div>
                
                <div class="form-group">
                    <label for="allergies">Allergies</label>
                    <textarea id="allergies" name="allergies" 
                              placeholder="Drug allergies, food allergies..."></textarea>
                </div>
                
                <div class="form-group">
                    <label for="currentTreatments">Current Treatments</label>
                    <textarea id="currentTreatments" name="currentTreatments" 
                              placeholder="Current medications and treatments..."></textarea>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Register Patient & Continue to Vital Signs →</button>
                    <a href="${pageContext.request.contextPath}/dashboard/nurse" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
