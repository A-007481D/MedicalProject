package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.PatientService;
import org.medxpertise.medicaltelexpertise.application.service.QueueService;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard/nurse")
public class NurseDashboardServlet extends HttpServlet {
    
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
        
        List<Patient> todayPatients = patientService.getTodayPatients();
        req.setAttribute("todayPatients", todayPatients);
        
        List<QueueEntry> waitingQueue = queueService.getWaitingQueue();
        req.setAttribute("waitingQueue", waitingQueue);
        
        req.getRequestDispatcher("/WEB-INF/views/nurse/dashboard.jsp").forward(req, resp);
    }
}
