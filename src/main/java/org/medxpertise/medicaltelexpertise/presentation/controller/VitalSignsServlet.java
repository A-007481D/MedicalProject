package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.PatientService;
import org.medxpertise.medicaltelexpertise.application.service.QueueService;
import org.medxpertise.medicaltelexpertise.application.service.exception.BusinessRuleException;
import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.VitalSign;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/nurse/vital-signs")
public class VitalSignsServlet extends HttpServlet {
    
    private PatientService patientService;
    private QueueService queueService;
    
    @Override
    public void init() throws ServletException {
        this.patientService = new PatientService();
        this.queueService = new QueueService();
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
        if (user.getRole() != Role.NURSE) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String patientIdStr = req.getParameter("patientId");
        if (patientIdStr == null || patientIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/nurse");
            return;
        }
        
        try {
            Long patientId = Long.parseLong(patientIdStr);
            Patient patient = patientService.getPatientById(patientId);
            req.setAttribute("patient", patient);
            req.getRequestDispatcher("/WEB-INF/views/nurse/vital-signs.jsp").forward(req, resp);
        } catch (BusinessRuleException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/nurse?error=" + e.getMessage());
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
        if (user.getRole() != Role.NURSE) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Access denied");
            return;
        }
        
        try {
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            Double temperature = Double.parseDouble(req.getParameter("temperature"));
            Integer pulse = Integer.parseInt(req.getParameter("pulse"));
            String bloodPressure = req.getParameter("bloodPressure");
            Integer respiratoryRate = Integer.parseInt(req.getParameter("respiratoryRate"));
            
            String weightStr = req.getParameter("weight");
            String heightStr = req.getParameter("height");
            Double weight = (weightStr != null && !weightStr.trim().isEmpty()) 
                    ? Double.parseDouble(weightStr) : null;
            Double height = (heightStr != null && !heightStr.trim().isEmpty()) 
                    ? Double.parseDouble(heightStr) : null;
            
            VitalSign vitalSign = patientService.addVitalSigns(
                patientId, temperature, pulse, bloodPressure,
                respiratoryRate, weight, height, user.getId()
            );
            
            queueService.addToQueue(patientId, user.getId());
            
            resp.sendRedirect(req.getContextPath() + "/dashboard/nurse?success=Patient registered and added to queue");
            
        } catch (BusinessRuleException e) {
            req.setAttribute("errorMessage", e.getMessage());
            Long patientId = Long.parseLong(req.getParameter("patientId"));
            Patient patient = patientService.getPatientById(patientId);
            req.setAttribute("patient", patient);
            req.getRequestDispatcher("/WEB-INF/views/nurse/vital-signs.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/nurse/vital-signs.jsp").forward(req, resp);
        }
    }
}
