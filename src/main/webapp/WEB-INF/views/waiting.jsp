<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Pending Approval</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .waiting-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 50px 40px;
            max-width: 500px;
            text-align: center;
            animation: fadeIn 0.5s ease-in;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .icon-container {
            margin-bottom: 30px;
        }
        
        .icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto;
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.05);
            }
        }
        
        .icon svg {
            width: 40px;
            height: 40px;
            fill: white;
        }
        
        h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 15px;
        }
        
        .message {
            color: #666;
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 20px;
        }
        
        .user-info {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin: 25px 0;
        }
        
        .user-info p {
            color: #555;
            margin: 8px 0;
        }
        
        .user-info strong {
            color: #333;
        }
        
        .actions {
            margin-top: 30px;
        }
        
        .btn {
            display: inline-block;
            padding: 12px 30px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 16px;
            font-weight: 600;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            margin-right: 10px;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
        }
        
        .btn-secondary:hover {
            background: #f8f9fa;
        }
        
        .refresh-info {
            margin-top: 20px;
            color: #999;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="waiting-container">
        <div class="icon-container">
            <div class="icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
            </div>
        </div>
        
        <h1>Account Pending Approval</h1>
        
        <p class="message">
            Thank you for registering! Your account has been created successfully, 
            but it requires administrator approval before you can access the system.
        </p>
        
        <div class="user-info">
            <p><strong>Username:</strong> ${sessionScope.user.username}</p>
            <p><strong>Email:</strong> ${sessionScope.user.email}</p>
            <p><strong>Status:</strong> <span style="color: #f39c12;">Awaiting Role Assignment</span></p>
        </div>
        
        <p class="message">
            An administrator will review your account and assign you the appropriate role 
            (Generalist, Specialist, Nurse, or Admin). You will be able to log in once your 
            role has been assigned.
        </p>
        
        <div class="actions">
            <button class="btn btn-primary" onclick="window.location.reload()">
                Check Status
            </button>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">
                Logout
            </a>
        </div>
        
        <p class="refresh-info">
            💡 Tip: Refresh this page to check if your role has been assigned.
        </p>
    </div>
</body>
</html>
