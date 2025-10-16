package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ExpertiseRequestService;
import org.medxpertise.medicaltelexpertise.application.service.SpecialistService;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/specialist/dashboard")
public class SpecialistDashboardServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SpecialistDashboardServlet.class.getName());

    private final SpecialistService specialistService = new SpecialistService();
    private final ExpertiseRequestService expertiseRequestService = new ExpertiseRequestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("=== SpecialistDashboardServlet START ===");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warning("No session or user found, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        logger.info("User found: " + user.getUsername() + ", role: " + user.getRole());

        if (user.getRole() != Role.SPECIALIST || !(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            logger.warning("User is not a specialist doctor, redirecting to waiting");
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor specialist = (Doctor) user;

        try {
            // Get specialist profile
            SpecialistProfile profile = specialistService.getSpecialistProfile(specialist.getId());
            if (profile != null) {
                req.setAttribute("profile", profile);
                req.setAttribute("tarif", profile.getTarif());
                req.setAttribute("slotDuration", profile.getSlotDurationMinutes());
            }

            List<ExpertiseRequest> pendingRequests = expertiseRequestService.getRequestsBySpecialistAndStatus(
                specialist.getId(), ExpertiseStatus.PENDING);
            req.setAttribute("pendingRequests", pendingRequests);

            List<org.medxpertise.medicaltelexpertise.domain.model.Timeslot> todaySlots =
                specialistService.getTodayTimeslots(specialist.getId());
            req.setAttribute("todaySlots", todaySlots);

            List<org.medxpertise.medicaltelexpertise.domain.model.Consultation> recentConsultations =
                specialistService.getRecentConsultations(specialist.getId(), 10);
            req.setAttribute("recentConsultations", recentConsultations);

            logger.info("Forwarding to specialist dashboard JSP");
            req.getRequestDispatcher("/WEB-INF/views/specialist/dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading specialist dashboard: " + e.getMessage(), e);
            req.setAttribute("error", "Erreur lors du chargement du tableau de bord: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        } finally {
            logger.info("=== SpecialistDashboardServlet END ===");
        }
    }
}
