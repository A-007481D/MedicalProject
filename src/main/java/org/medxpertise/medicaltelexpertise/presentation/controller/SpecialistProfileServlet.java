package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.SpecialistService;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/specialist/profile")
public class SpecialistProfileServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SpecialistProfileServlet.class.getName());
    private final SpecialistService specialistService = new SpecialistService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor specialist = (Doctor) user;

        try {
            SpecialistProfile profile = specialistService.getSpecialistProfile(specialist.getId());

            if (profile == null) {
                // Create default profile if none exists
                profile = new SpecialistProfile();
                profile.setSpecialist(specialist);
                profile.setTarif(200.0); // Default rate
                profile.setSlotDurationMinutes(30); // Default slot duration
                specialistService.createOrUpdateProfile(profile);
            }

            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/WEB-INF/views/specialist/profile.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.severe("Error loading specialist profile: " + e.getMessage());
            req.setAttribute("error", "Erreur lors du chargement du profil: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor specialist = (Doctor) user;

        try {
            String tarifStr = req.getParameter("tarif");
            String slotDurationStr = req.getParameter("slotDurationMinutes");

            Double tarif = Double.parseDouble(tarifStr);
            Integer slotDuration = Integer.parseInt(slotDurationStr);

            SpecialistProfile profile = specialistService.getSpecialistProfile(specialist.getId());

            if (profile == null) {
                profile = new SpecialistProfile();
                profile.setSpecialist(specialist);
            }

            profile.setTarif(tarif);
            profile.setSlotDurationMinutes(slotDuration);

            specialistService.createOrUpdateProfile(profile);

            req.setAttribute("success", "Profil mis à jour avec succès");
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/WEB-INF/views/specialist/profile.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Format de données invalide");
            doGet(req, resp);
        } catch (Exception e) {
            logger.severe("Error updating specialist profile: " + e.getMessage());
            req.setAttribute("error", "Erreur lors de la mise à jour du profil: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
