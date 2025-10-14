package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.PatientService;
import org.medxpertise.medicaltelexpertise.application.service.VitalSignsService;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.VitalSign;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.List;

@WebServlet("/generalist/patient/*")
public class PatientDossierServlet extends HttpServlet {
    
    private final PatientService patientService = new PatientService();
    private final VitalSignsService vitalSignsService = new VitalSignsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        if (user.getRole() != Role.GENERALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        // Extract patient ID from URL path
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
            return;
        }

        try {
            Long patientId = Long.parseLong(pathInfo.substring(1));
            
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                req.setAttribute("error", "Patient non trouvé");
                resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                return;
            }

            List<VitalSign> vitalSignsHistory = vitalSignsService.getVitalSignsByPatient(patientId);
            
            req.setAttribute("patient", patient);
            req.setAttribute("vitalSignsHistory", vitalSignsHistory);
            req.setAttribute("doctor", user);
            
            req.getRequestDispatcher("/WEB-INF/views/generalist/patient-dossier.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
        }
    }
}
