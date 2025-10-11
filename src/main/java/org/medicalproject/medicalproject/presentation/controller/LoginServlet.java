package org.medicalproject.medicalproject.presentation.controller;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medicalproject.medicalproject.application.service.AuthenticationService;
import org.medicalproject.medicalproject.domain.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Inject
    private AuthenticationService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to the JSP page for GET requests
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle login form submission
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("remember-me");

        // Basic validation
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        try {
            // Authenticate user
            Optional<User> userOpt = authService.authenticate(email, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Store user in session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().name());
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);
                
                // Redirect to dashboard based on role
                String redirectPath = request.getContextPath() + "/dashboard";
                response.sendRedirect(redirectPath);
            } else {
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }
}
