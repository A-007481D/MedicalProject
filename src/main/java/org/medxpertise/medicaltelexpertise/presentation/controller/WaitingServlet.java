package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.UserRepositoryJpa;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/waiting")
public class WaitingServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(WaitingServlet.class.getName());
    private final UserRepositoryJpa userRepository = new UserRepositoryJpa();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // If no session or user not in session, redirect to login
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            User sessionUser = (User) session.getAttribute("user");

            // Get fresh user data from database to ensure role is up-to-date
            User user = userRepository.findById(sessionUser.getId())
                    .orElse(null);
            if (user == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // Update user in session
            session.setAttribute("user", user);

            // Handle role-based redirection
            if (user.getRole() != null && user.getRole() != Role.BASE) {
                String redirectPath = switch (user.getRole()) {
                    case ADMIN -> "/dashboard/admin";
                    case NURSE -> "/dashboard/nurse";
                    case GENERALIST -> "/dashboard/generalist";
                    case SPECIALIST -> "/dashboard/specialist";
                    default -> "/login";
                };

                // Add cache control headers to prevent caching of this page
                resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                resp.setHeader("Pragma", "no-cache");
                resp.setDateHeader("Expires", 0);

                // Only redirect if we're not already on the target page
                if (!req.getRequestURI().endsWith(redirectPath)) {
                    resp.sendRedirect(req.getContextPath() + redirectPath);
                    return;
                }
            }

            // If we get here, either the user has BASE role or there was no redirect
            req.getRequestDispatcher("/WEB-INF/views/waiting.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in WaitingServlet: " + e.getMessage(), e);
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
