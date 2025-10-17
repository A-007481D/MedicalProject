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
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/dashboard/specialist")
public class SpecialistDashboardServlet extends HttpServlet {
    
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
        
        if (user.getRole() != Role.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        if (user instanceof Doctor) {
            Doctor specialist = (Doctor) user;
            
            if (specialist.getDoctorType() == DoctorType.GENERALIST) {
                resp.sendRedirect(req.getContextPath() + "/waiting");
                return;
            }
            
            List<Consultation> consultations = consultationService.getExpertiseRequestsBySpecialist(specialist.getId());
            
            List<ExpertiseRequest> pendingExpertises = consultations.stream()
                    .map(Consultation::getExpertiseRequest)
                    .filter(e -> e != null && e.getStatus() == ExpertiseStatus.PENDING)
                    .collect(Collectors.toList());
            
            List<ExpertiseRequest> completedTodayExpertises = consultations.stream()
                    .map(Consultation::getExpertiseRequest)
                    .filter(e -> e != null && 
                               e.getStatus() == ExpertiseStatus.DONE &&
                               e.getRequestedAt() != null && 
                               e.getRequestedAt().toLocalDate().equals(LocalDate.now()))
                    .collect(Collectors.toList());
            
            req.setAttribute("specialist", specialist);
            req.setAttribute("pendingExpertises", pendingExpertises);
            req.setAttribute("completedTodayExpertises", completedTodayExpertises);
            req.setAttribute("pendingCount", pendingExpertises.size());
            req.setAttribute("completedTodayCount", completedTodayExpertises.size());
        }
        
        req.getRequestDispatcher("/WEB-INF/views/specialist/dashboard.jsp")
           .forward(req, resp);
    }
}
