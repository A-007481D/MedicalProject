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

@WebServlet("/generalist/consultation/create")
public class CreateConsultationServlet extends HttpServlet {
    
    private final ConsultationService consultationService = new ConsultationService();
    private final PatientService patientService = new PatientService();
    private final QueueService queueService = new QueueService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // Check if user is a generalist
        if (user.getRole() != Role.GENERALIST || !(user instanceof Doctor)) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor generalist = (Doctor) user;

        try {
            // Get form parameters
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            String motif = req.getParameter("motif");
            String observations = req.getParameter("observations");
            String clinicalExam = req.getParameter("clinicalExam");

            // Get patient
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                req.setAttribute("error", "Patient non trouvé");
                resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                return;
            }

            // Create consultation
            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setGeneralist(generalist);
            consultation.setMotif(motif);
            consultation.setObservations(observations);
            consultation.setClinicalExam(clinicalExam);
            consultation.setCreatedAt(LocalDateTime.now());
            consultation.setStatus(ConsultationStatus.IN_PROGRESS);

            // Save consultation
            Consultation savedConsultation = consultationService.createConsultation(consultation);

            // Remove patient from queue (they are now being consulted)
            queueService.removeFromQueue(patientId);

            // Redirect to consultation detail page
            resp.sendRedirect(req.getContextPath() + "/generalist/consultation/" + savedConsultation.getId());

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Données invalides");
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors de la création de la consultation: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
        }
    }
}
