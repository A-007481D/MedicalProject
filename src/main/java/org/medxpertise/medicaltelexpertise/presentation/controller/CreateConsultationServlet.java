package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ConsultationService;
import org.medxpertise.medicaltelexpertise.application.service.PatientService;
import org.medxpertise.medicaltelexpertise.application.service.QueueService;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/generalist/consultation/create")
public class CreateConsultationServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CreateConsultationServlet.class.getName());

    private final ConsultationService consultationService = new ConsultationService();
    private final PatientService patientService = new PatientService();
    private final QueueService queueService = new QueueService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.info("=== CreateConsultationServlet START ===");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.warning("No session or user found, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        logger.info("User found: " + user.getUsername() + ", role: " + user.getRole());

        if (user.getRole() != Role.GENERALIST || !(user instanceof Doctor)) {
            logger.warning("User is not a generalist doctor, redirecting to waiting");
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor generalist = (Doctor) user;

        try {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            String motif = req.getParameter("motif");
            String observations = req.getParameter("observations");
            String clinicalExam = req.getParameter("clinicalExam");

            logger.info("Request parameters - patientId: " + patientId + ", motif: " + motif);

            // Get patient
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                logger.warning("Patient not found with ID: " + patientId);
                req.setAttribute("error", "Patient non trouvé");
                resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                return;
            }

            logger.info("Patient found: " + patient.getFirstName() + " " + patient.getLastName());

            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setGeneralist(generalist);
            consultation.setMotif(motif);
            consultation.setObservations(observations);
            consultation.setClinicalExam(clinicalExam);
            consultation.setCreatedAt(LocalDateTime.now());
            consultation.setStatus(ConsultationStatus.IN_PROGRESS);

            logger.info("Consultation object created, calling consultationService.createConsultation()");

            Consultation savedConsultation = consultationService.createConsultation(consultation);

            logger.info("Consultation service returned: " + (savedConsultation != null ? "NOT NULL" : "NULL"));

            queueService.removeFromQueue(patientId);
            if(savedConsultation == null) {
                logger.severe("Saved consultation is NULL - consultation creation failed!");
                req.setAttribute("error", "Erreur lors de la création de la consultation");
                resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                return;
            }

            logger.info("Consultation saved successfully with ID: " + savedConsultation.getId());
            logger.info("Redirecting to consultation details: /generalist/consultation/" + savedConsultation.getId());

            resp.sendRedirect(req.getContextPath() + "/generalist/consultation/" + savedConsultation.getId());

        } catch (NumberFormatException e) {
            logger.severe("NumberFormatException: " + e.getMessage());
            req.setAttribute("error", "Données invalides");
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
        } catch (Exception e) {
            logger.severe("Exception during consultation creation: " + e.getMessage());
            req.setAttribute("error", "Erreur lors de la création de la consultation: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
        } finally {
            logger.info("=== CreateConsultationServlet END ===");
        }
    }
}
