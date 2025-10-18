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
import java.util.Optional;

@WebServlet("/generalist/consultation/*")
public class ViewConsultationServlet extends HttpServlet {
    
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

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Consultation ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[pathParts.length - 1];
            Long consultationId = Long.parseLong(idStr);
            
            Consultation consultation = consultationService.getConsultationWithExpertise(consultationId);
            
            if (consultation == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Consultation not found");
                return;
            }
            
            if (!consultation.getGeneralist().getId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this consultation");
                return;
            }
            
            if (consultation.getPatient() == null || consultation.getGeneralist() == null) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Incomplete consultation data");
                return;
            }
            
            req.setAttribute("consultation", consultation);
            req.setAttribute("patient", consultation.getPatient());
            req.setAttribute("hasExpertise", consultation.getExpertiseRequest() != null);
            
            System.out.println("Consultation status: " + consultation.getStatus());
            System.out.println("Has expertise request: " + (consultation.getExpertiseRequest() != null));
            
            try {
                req.getRequestDispatcher("/WEB-INF/views/generalist/view-consultation.jsp").forward(req, resp);
            } catch (Exception e) {
                System.err.println("Error forwarding to JSP: " + e.getMessage());
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading consultation view");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid consultation ID");
        }
    }
}
