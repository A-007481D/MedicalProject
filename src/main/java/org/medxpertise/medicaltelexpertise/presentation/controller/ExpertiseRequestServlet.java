package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ConsultationService;
import org.medxpertise.medicaltelexpertise.application.service.DoctorService;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.PriorityLevel;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebServlet("/expertise-request/*")
public class ExpertiseRequestServlet extends HttpServlet {
    
    private DoctorService doctorService;
    private ConsultationService consultationService;
    
    @Override
    public void init() {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        this.doctorService = new DoctorService(emf);
        this.consultationService = new ConsultationService();
    }
    
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
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only generalists can request expertise");
            return;
        }
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Consultation ID is required");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }
        
        try {
            Long consultationId = Long.parseLong(pathParts[1]);
            Optional<Consultation> consultationOpt = consultationService.getConsultationById(consultationId);
            
            if (consultationOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Consultation not found");
                return;
            }
            
            Consultation consultation = consultationOpt.get();
            
            if (!consultation.getGeneralist().getId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to request expertise for this consultation");
                return;
            }
            
            if (pathParts.length > 2 && pathParts[2].equals("specialists")) {
                if (pathParts.length > 3) {
                    try {
                        Specialty specialty = Specialty.valueOf(pathParts[3]);
                        List<Doctor> specialists = doctorService.findSpecialistsBySpecialty(specialty);
                        
                        req.setAttribute("consultation", consultation);
                        req.setAttribute("specialty", specialty);
                        req.setAttribute("specialists", specialists);
                        req.getRequestDispatcher("/WEB-INF/views/generalist/select-specialist.jsp").forward(req, resp);
                        return;
                    } catch (IllegalArgumentException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid specialty");
                        return;
                    }
                } else {
                    req.setAttribute("consultation", consultation);
                    req.setAttribute("specialties", Specialty.values());
                    req.getRequestDispatcher("/WEB-INF/views/generalist/select-specialty.jsp").forward(req, resp);
                    return;
                }
            }
            
            if (pathParts.length > 4 && pathParts[2].equals("specialist") && pathParts[4].equals("availability")) {
                try {
                    Long specialistId = Long.parseLong(pathParts[3]);
                    Doctor specialist = doctorService.findDoctorWithProfileAndTimeslots(specialistId);
                    
                    if (specialist == null || !specialist.isSpecialist()) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Specialist not found");
                        return;
                    }
                    
                    req.setAttribute("consultation", consultation);
                    req.setAttribute("specialist", specialist);
                    req.getRequestDispatcher("/WEB-INF/views/generalist/view-specialist-availability.jsp").forward(req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid specialist ID");
                    return;
                }
            }
            
            req.setAttribute("consultation", consultation);
            req.setAttribute("specialties", Specialty.values());
            req.getRequestDispatcher("/WEB-INF/views/generalist/select-specialty.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid consultation ID");
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
        if (user.getRole() != Role.GENERALIST) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only generalists can request expertise");
            return;
        }
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Consultation ID is required");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }
        
        try {
            Long consultationId = Long.parseLong(pathParts[1]);
            Optional<Consultation> consultationOpt = consultationService.getConsultationById(consultationId);
            
            if (consultationOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Consultation not found");
                return;
            }
            
            Consultation consultation = consultationOpt.get();
            
            if (!consultation.getGeneralist().getId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to request expertise for this consultation");
                return;
            }
            
            if (pathParts.length > 2 && pathParts[2].equals("submit")) {
                String specialistIdStr = req.getParameter("specialistId");
                String question = req.getParameter("question");
                String priorityStr = req.getParameter("priority");
                String timeslotStartStr = req.getParameter("timeslotStart");
                String timeslotEndStr = req.getParameter("timeslotEnd");
                
                if (specialistIdStr == null || question == null || priorityStr == null || 
                    timeslotStartStr == null || timeslotEndStr == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
                    return;
                }
                
                try {
                    Long specialistId = Long.parseLong(specialistIdStr);
                    PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
                    LocalDateTime timeslotStart = LocalDateTime.parse(timeslotStartStr);
                    LocalDateTime timeslotEnd = LocalDateTime.parse(timeslotEndStr);
                    
                    Doctor specialist = doctorService.findDoctorWithProfileAndTimeslots(specialistId);
                    if (specialist == null || !specialist.isSpecialist()) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Specialist not found");
                        return;
                    }
                    
                    ExpertiseRequest expertiseRequest = new ExpertiseRequest();
                    expertiseRequest.setConsultation(consultation);
                    
                    if (specialist.getSpecialty() != null) {
                        expertiseRequest.setSpecializationNeeded(specialist.getSpecialty().name());
                    } else {
                        expertiseRequest.setSpecializationNeeded(Specialty.GENERAL_PRACTICE.name());
                    }
                    
                    expertiseRequest.setQuestion(question);
                    expertiseRequest.setPriority(priority);
                    expertiseRequest.setScheduledSlotStart(timeslotStart);
                    expertiseRequest.setScheduledSlotEnd(timeslotEnd);
                    expertiseRequest.setStatus(ExpertiseStatus.PENDING);
                    expertiseRequest.setSpecialistAssigned(specialist);
                    
                    consultation.setExpertiseRequest(expertiseRequest);
                    consultationService.updateConsultation(consultation);
                    
                    session.setAttribute("successMessage", "Expertise request submitted successfully");
                    resp.sendRedirect(req.getContextPath() + "/consultations/" + consultationId);
                    
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid specialist ID");
                } catch (IllegalArgumentException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid priority value");
                }
                
                return;
            }
            
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid consultation ID");
        }
    }
}
