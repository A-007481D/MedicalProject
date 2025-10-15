package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ConsultationService;
import org.medxpertise.medicaltelexpertise.application.service.QueueService;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/dashboard/generalist")
public class GeneralistDashboardServlet extends HttpServlet {
    
    private final QueueService queueService = new QueueService();
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
            
            if (doctor.getDoctorType() != DoctorType.GENERALIST) {
                resp.sendRedirect(req.getContextPath() + "/waiting");
                return;
            }
            
            req.setAttribute("doctor", doctor);
        }

        List<QueueEntry> waitingPatients = queueService.getWaitingQueue();

        waitingPatients = waitingPatients.stream().peek(e -> {
            if (e.getArrivalTime() != null) {
                try {
                    Date arrival = Date.from(e.getArrivalTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                    e.setDisplayArrivalTime(arrival);
                } catch (Exception ex) {
                    e.setDisplayArrivalTime(null);
                }
            } else {
                e.setDisplayArrivalTime(null);
            }
        }).collect(Collectors.toList());


        if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            
            List<Consultation> allConsultations = consultationService.getConsultationsByGeneralist(doctor.getId());
            
            List<Consultation> todayConsultations = allConsultations.stream()
                    .filter(c -> c.getCreatedAt().toLocalDate().equals(java.time.LocalDate.now()))
                    .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                    .collect(Collectors.toList());
            
            List<ExpertiseRequest> pendingExpertise = allConsultations.stream()
                    .filter(c -> c.getExpertiseRequest() != null && 
                                c.getExpertiseRequest().getStatus() == ExpertiseStatus.PENDING)
                    .map(Consultation::getExpertiseRequest)
                    .collect(Collectors.toList());
            
            req.setAttribute("todayConsultations", todayConsultations);
            req.setAttribute("todayConsultationsCount", todayConsultations.size());
            req.setAttribute("pendingExpertiseCount", pendingExpertise.size());
        }
        
        req.setAttribute("waitingPatients", waitingPatients);
        
        req.getRequestDispatcher("/WEB-INF/views/generalist/dashboard.jsp").forward(req, resp);
    }
}

///  add the vitals signs to access them in the genralist dashboard
