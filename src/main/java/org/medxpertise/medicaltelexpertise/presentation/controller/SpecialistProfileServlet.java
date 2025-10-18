package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.medxpertise.medicaltelexpertise.application.service.DoctorService;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.SpecialistProfile;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/specialist/profile/*")
public class SpecialistProfileServlet extends HttpServlet {
    
    private DoctorService doctorService;
    
    @Override
    public void init() {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("MedicalPU");
            getServletContext().setAttribute("emf", emf);
        }
        this.doctorService = new DoctorService(emf);
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
        if (user.getRole() != Role.SPECIALIST) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only specialists can access this page");
            return;
        }
        
        try {
            Long userId = user.getId();
            
            Doctor specialist = doctorService.findDoctorById(userId);
            if (specialist == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "User is not a registered specialist");
                return;
            }
            
            SpecialistProfile profile = doctorService.getSpecialistProfile(userId);
            if (profile == null) {
                profile = new SpecialistProfile();
                profile.setSpecialist(specialist);
                profile.setTarif(0.0);
                profile.setSlotDurationMinutes(30);
                
                EntityManager em = doctorService.getEntityManager();
                try {
                    em.getTransaction().begin();
                    em.persist(profile);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    throw new ServletException("Failed to create specialist profile", e);
                } finally {
                    em.close();
                }
            }
            
            req.setAttribute("profile", profile);
            req.getRequestDispatcher("/WEB-INF/views/specialist/profile.jsp").forward(req, resp);
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading specialist profile");
            return;
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
        if (user.getRole() != Role.SPECIALIST) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only specialists can update their profile");
            return;
        }
        
        try {
            double tarif = Double.parseDouble(req.getParameter("tarif"));
            int slotDuration = Integer.parseInt(req.getParameter("slotDuration"));
            
            Doctor specialist = (Doctor) user;
            SpecialistProfile profile = doctorService.getSpecialistProfile(specialist.getId());
            
            if (profile == null) {
                profile = new SpecialistProfile();
                profile.setSpecialist(specialist);
            }
            
            profile.setTarif(tarif);
            profile.setSlotDurationMinutes(slotDuration);
            
            doctorService.saveSpecialistProfile(profile);
            
            session.setAttribute("successMessage", "Profile updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/specialist/profile");
            
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid input values");
            resp.sendRedirect(req.getContextPath() + "/specialist/profile");
        }
    }
    
    @Override
    public void destroy() {
        
    }
}
