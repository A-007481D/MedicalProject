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
import org.medxpertise.medicaltelexpertise.domain.model.enums.Gender;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet("/nurse/register-patient")
public class PatientRegistrationServlet extends HttpServlet {
    
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
        
        req.getRequestDispatcher("/WEB-INF/views/nurse/register-patient.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        Nurse nurse = (Nurse) session.getAttribute("user");
        
        try {
            String action = req.getParameter("action");
            
            if ("search".equals(action)) {
                String identifier = req.getParameter("identifier");
                Optional<Patient> patientOpt = patientService.searchPatient(identifier);
                
                if (patientOpt.isPresent()) {
                    req.setAttribute("foundPatient", patientOpt.get());
                    req.setAttribute("searchSuccess", true);
                } else {
                    req.setAttribute("searchError", "Patient not found");
                }
                req.getRequestDispatcher("/WEB-INF/views/nurse/register-patient.jsp").forward(req, resp);
                
            } else if ("registerNew".equals(action)) {
                String cin = req.getParameter("cin");
                String firstName = req.getParameter("firstName");
                String lastName = req.getParameter("lastName");
                String birthDateStr = req.getParameter("birthDate");
                String genderStr = req.getParameter("gender");
                String phone = req.getParameter("phone");
                String address = req.getParameter("address");
                String ssn = req.getParameter("ssn");
                String mutuelle = req.getParameter("mutuelle");
                String antecedents = req.getParameter("antecedents");
                String allergies = req.getParameter("allergies");
                String currentTreatments = req.getParameter("currentTreatments");
                
                LocalDate birthDate = birthDateStr != null && !birthDateStr.isEmpty() 
                        ? LocalDate.parse(birthDateStr) : null;
                Gender gender = genderStr != null && !genderStr.isEmpty() 
                        ? Gender.valueOf(genderStr) : null;
                
                Patient patient = patientService.registerNewPatient(
                    cin, firstName, lastName, birthDate, gender,
                    phone, address, ssn, mutuelle, antecedents,
                    allergies, currentTreatments, nurse.getId()
                );
                
                resp.sendRedirect(req.getContextPath() + "/nurse/vital-signs?patientId=" + patient.getId());
                
            } else if ("addVitals".equals(action)) {
                Long patientId = Long.parseLong(req.getParameter("patientId"));
                Double temperature = Double.parseDouble(req.getParameter("temperature"));
                Integer pulse = Integer.parseInt(req.getParameter("pulse"));
                String bloodPressure = req.getParameter("bloodPressure");
                Integer respiratoryRate = Integer.parseInt(req.getParameter("respiratoryRate"));
                Double weight = req.getParameter("weight") != null && !req.getParameter("weight").isEmpty() 
                        ? Double.parseDouble(req.getParameter("weight")) : null;
                Double height = req.getParameter("height") != null && !req.getParameter("height").isEmpty() 
                        ? Double.parseDouble(req.getParameter("height")) : null;
                
                VitalSign vitalSign = patientService.addVitalSigns(
                    patientId, temperature, pulse, bloodPressure,
                    respiratoryRate, weight, height, nurse.getId()
                );
                
                queueService.addToQueue(patientId, nurse.getId());
                
                resp.sendRedirect(req.getContextPath() + "/dashboard/nurse?success=Patient added to queue");
            }
            
        } catch (BusinessRuleException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/nurse/register-patient.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/nurse/register-patient.jsp").forward(req, resp);
        }
    }
}
