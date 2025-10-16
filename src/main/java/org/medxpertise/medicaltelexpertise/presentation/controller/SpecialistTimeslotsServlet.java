package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.inject.Inject;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.application.service.SpecialistService;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/specialist/timeslots")
@jakarta.enterprise.context.RequestScoped
public class SpecialistTimeslotsServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(SpecialistTimeslotsServlet.class.getName());
    
    @Inject
    private SpecialistService specialistService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Doctor specialist = validateAndGetSpecialist(req, resp);
            if (specialist == null) return;

            // Get specialist profile
            SpecialistProfile profile = specialistService.getSpecialistProfile(specialist.getId());
            if (profile == null) {
                req.setAttribute("error", "Profil non trouvé. Veuillez configurer votre profil d'abord.");
                req.getRequestDispatcher("/WEB-INF/views/specialist/profile.jsp").forward(req, resp);
                return;
            }

            // Get all timeslots for the specialist
            List<Timeslot> timeslots = specialistService.getAllTimeslots(specialist.getId());
            req.setAttribute("timeslots", timeslots);
            req.setAttribute("profile", profile);

            // Get next 7 days for timeslot creation
            req.setAttribute("next7Days", getNext7Days(LocalDate.now()));

            req.getRequestDispatcher("/WEB-INF/views/specialist/timeslots.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading specialist timeslots", e);
            req.setAttribute("error", "Erreur lors du chargement des créneaux: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Doctor specialist = validateAndGetSpecialist(req, resp);
            if (specialist == null) return;

            String action = req.getParameter("action");
            if (action == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter is required");
                return;
            }

            switch (action) {
                case "create" -> createTimeslot(req, specialist);
                case "delete" -> deleteTimeslot(req, specialist);
                default -> {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action: " + action);
                    return;
                }
            }

            resp.sendRedirect(req.getContextPath() + "/specialist/timeslots");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing timeslot action", e);
            req.setAttribute("error", "Erreur lors du traitement: " + e.getMessage());
            doGet(req, resp);
        }
    }

    private Doctor validateAndGetSpecialist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != Role.SPECIALIST || !(user instanceof Doctor doctor) ||
                doctor.getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return null;
        }

        return (Doctor) user;
    }

    private void createTimeslot(HttpServletRequest req, Doctor specialist) throws Exception {
        String dateStr = req.getParameter("date");
        String startTimeStr = req.getParameter("startTime");
        String endTimeStr = req.getParameter("endTime");

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        SpecialistProfile profile = specialistService.getSpecialistProfile(specialist.getId());
        if (profile == null) {
            throw new IllegalStateException("Specialist profile not found");
        }

        Timeslot timeslot = new Timeslot();
        timeslot.setProfile(profile);
        timeslot.setStart(startDateTime);
        timeslot.setEnd(endDateTime);
        timeslot.free();

        specialistService.createTimeslot(timeslot);
    }

    private void deleteTimeslot(HttpServletRequest req, Doctor specialist) throws Exception {
        String timeslotIdParam = req.getParameter("timeslotId");
        if (timeslotIdParam == null || timeslotIdParam.isEmpty()) {
            throw new IllegalArgumentException("Timeslot ID is required");
        }

        Long timeslotId = Long.parseLong(timeslotIdParam);
        specialistService.deleteTimeslot(timeslotId);
    }

    private List<LocalDate> getNext7Days(LocalDate startDate) {
        return java.util.stream.IntStream.range(0, 7)
                .mapToObj(startDate::plusDays)
                .toList();
    }
}