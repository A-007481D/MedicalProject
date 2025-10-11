package org.medicalproject.medicalproject.presentation.controller;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medicalproject.medicalproject.application.service.AuthenticationService;
import org.medicalproject.medicalproject.domain.model.User;
import org.medicalproject.medicalproject.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Inject
    private AuthenticationService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String userType = request.getParameter("userType");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String terms = request.getParameter("terms");

        if (firstName == null || lastName == null || email == null || userType == null || password == null || confirmPassword == null) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        if (terms == null) {
            request.setAttribute("error", "You must agree to the terms and conditions.");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        try {
            Role role;
            try {
                role = Role.valueOf(userType.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid user type selected.");
                request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                return;
            }
            
            String username = email.split("@")[0];
            
            User user = authService.registerUser(
                username,
                firstName,
                lastName,
                email,
                password,
                role
            );
            
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
            
        } catch (Exception e) {
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
        }
    }
}
