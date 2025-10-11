package org.medicalproject.medicalproject.presentation.controller;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medicalproject.medicalproject.domain.model.User;
import org.medicalproject.medicalproject.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Set user-specific attributes
        request.setAttribute("username", user.getUsername());
        request.setAttribute("role", user.getRole().name());
        
        // Forward to appropriate dashboard based on role
        String dashboardPage = "/WEB-INF/dashboard/" + user.getRole().name().toLowerCase() + ".jsp";
        request.getRequestDispatcher(dashboardPage).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
