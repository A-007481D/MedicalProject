package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ConsultationService;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.List;

@WebServlet("/generalist/consultations")
public class GeneralistConsultationsServlet extends HttpServlet {
    
    private final ConsultationService consultationService = new ConsultationService();

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

        if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            
            // Get all consultations for this generalist
            List<Consultation> consultations = consultationService.getConsultationsByGeneralist(doctor.getId());
            
            req.setAttribute("consultations", consultations);
            req.setAttribute("doctor", doctor);
        }
        
        req.getRequestDispatcher("/WEB-INF/views/generalist/consultations.jsp").forward(req, resp);
    }
}
