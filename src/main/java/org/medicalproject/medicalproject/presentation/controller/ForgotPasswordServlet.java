package org.medicalproject.medicalproject.presentation.controller;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medicalproject.medicalproject.application.service.AuthenticationService;
import org.medicalproject.medicalproject.domain.repository.UserRepository;
import org.medicalproject.medicalproject.domain.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthenticationService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the JSP page for GET requests
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle forgot password form submission
        String email = request.getParameter("email");

        // Basic validation
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required.");
            request.setAttribute("showForgotForm", true);
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        try {
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isPresent()) {
                // In a real app, we would generate a reset token and send an email
                // For now, we'll just set a success message
                request.setAttribute("success", "If an account exists with this email, you will receive a password reset link shortly.");
                
                // Store reset token in user's session (temporary solution)
                // In a real app, this would be stored in the database with an expiration
                request.getSession().setAttribute("resetEmail", email);
            } else {
                // Don't reveal that the email doesn't exist (security best practice)
                request.setAttribute("success", "If an account exists with this email, you will receive a password reset link shortly.");
            }
            
            // Show the login form with success message
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred while processing your request. Please try again.");
            request.setAttribute("showForgotForm", true);
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }
}
