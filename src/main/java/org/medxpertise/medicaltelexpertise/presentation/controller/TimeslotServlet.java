package org.medxpertise.medicaltelexpertise.presentation.controller;

import org.json.JSONObject;
import org.json.JSONTokener;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.DoctorService;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.Timeslot;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Enumeration;

@WebServlet("/api/timeslots")
public class TimeslotServlet extends HttpServlet {
    
    private DoctorService doctorService;
    
    @Override
    public void init() {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        this.doctorService = new DoctorService(emf);
    }
    
    private boolean isValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("No active session found");
            return false;
        }
        
        String sessionToken = (String) session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
        if (sessionToken == null) {
            System.out.println("No CSRF token found in session");
            return false;
        }
        
        String requestToken = request.getHeader("X-CSRF-TOKEN");
        System.out.println("CSRF token from header: " + requestToken);
        
        if (requestToken == null || requestToken.isEmpty()) {
            requestToken = request.getParameter("_csrf");
            System.out.println("CSRF token from parameter: " + requestToken);
        }
        
        if (requestToken == null || requestToken.isEmpty()) {
            System.out.println("No CSRF token found in request");
            return false;
        }
        
        boolean isValid = sessionToken.equals(requestToken);
        if (!isValid) {
            System.out.println("CSRF token validation failed. Expected: " + sessionToken + ", Got: " + requestToken);
        } else {
            System.out.println("CSRF token validation successful");
        }
        
        return isValid;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        System.out.println("=== Request Headers ===");
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + req.getHeader(headerName));
        }
        System.out.println("======================");
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authenticated");
            return;
        }
        
        if (!isValidCsrfToken(req)) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\":\"CSRF token validation failed\"}");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (user.getRole() != Role.SPECIALIST) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only specialists can create timeslots");
            return;
        }
        
        try {
            JSONObject jsonRequest = new JSONObject(new JSONTokener(req.getReader()));
            
            String dateStr = jsonRequest.getString("date");
            String startTimeStr = jsonRequest.getString("startTime");
            String endTimeStr = jsonRequest.getString("endTime");
            
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            
            SpecialistProfile profile = doctorService.getSpecialistProfile(user.getId());
            if (profile == null) {
                throw new RuntimeException("Specialist profile not found");
            }
            
            Timeslot timeslot = new Timeslot();
            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
            
            try {
                java.lang.reflect.Field startField = Timeslot.class.getDeclaredField("start");
                java.lang.reflect.Field endField = Timeslot.class.getDeclaredField("end");
                java.lang.reflect.Field profileField = Timeslot.class.getDeclaredField("profile");
                
                startField.setAccessible(true);
                endField.setAccessible(true);
                profileField.setAccessible(true);
                
                startField.set(timeslot, startDateTime);
                endField.set(timeslot, endDateTime);
                profileField.set(timeslot, profile);
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to set timeslot fields", e);
            }
            
            profile.getTimeslots().add(timeslot);
            doctorService.saveSpecialistProfile(profile);
            
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            JSONObject response = new JSONObject();
            response.put("message", "Timeslot created successfully");
            resp.getWriter().write(response.toString());
                
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject error = new JSONObject();
            error.put("error", "Error creating timeslot: " + e.getMessage());
            resp.getWriter().write(error.toString());
        }
    }
}
