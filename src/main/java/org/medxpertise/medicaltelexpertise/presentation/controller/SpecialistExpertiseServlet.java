package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.ExpertiseRequestService;
import org.medxpertise.medicaltelexpertise.application.service.SpecialistService;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.ExpertiseRequest;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ExpertiseStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/specialist/expertise/*")
public class SpecialistExpertiseServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(SpecialistExpertiseServlet.class.getName());
//    private final ExpertiseRequestService expertiseRequestService = new ExpertiseRequestService();
    private final SpecialistService specialistService = new SpecialistService();
    @Inject
    private ExpertiseRequestService expertiseRequestService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listExpertiseRequests(req, resp);
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length == 2 && "respond".equals(pathParts[1])) {
            showResponseForm(req, resp, Long.parseLong(pathParts[0]));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length == 2 && "respond".equals(pathParts[1])) {
            submitResponse(req, resp, Long.parseLong(pathParts[0]));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listExpertiseRequests(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != Role.SPECIALIST || !(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        Doctor specialist = (Doctor) user;

        try {
            List<ExpertiseRequest> allRequests = expertiseRequestService.getAllRequestsBySpecialist(specialist.getId());
            List<ExpertiseRequest> pendingRequests = allRequests.stream()
                .filter(request -> request.getStatus() == ExpertiseStatus.PENDING)
                .collect(Collectors.toList());
                
            req.setAttribute("allRequests", allRequests);
            req.setAttribute("pendingRequests", pendingRequests);

            req.getRequestDispatcher("/WEB-INF/views/specialist/expertise.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error processing expertise request action: " + e.getMessage(), e);
            req.setAttribute("error", "Erreur lors du chargement des demandes d'expertise: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private void showResponseForm(HttpServletRequest req, HttpServletResponse resp, Long requestId)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != Role.SPECIALIST || !(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        try {
            ExpertiseRequest request = expertiseRequestService.getRequestById(requestId);
            if (request == null) {
                req.setAttribute("error", "Demande d'expertise non trouvée");
                req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
                return;
            }

            // Check if the request belongs to this specialist
            if (!request.getSpecialistAssigned().getId().equals(((Doctor) user).getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            req.setAttribute("request", request);
            req.getRequestDispatcher("/WEB-INF/views/specialist/expertise-response.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading expertise response form: " + e.getMessage(), e);
            req.setAttribute("error", "Erreur lors du chargement du formulaire: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private void submitResponse(HttpServletRequest req, HttpServletResponse resp, Long requestId)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getRole() != Role.SPECIALIST || !(user instanceof Doctor) || ((Doctor) user).getDoctorType() != DoctorType.SPECIALIST) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        try {
            String expertOpinion = req.getParameter("expertOpinion");
            String recommendations = req.getParameter("recommendations");

            if (expertOpinion == null || expertOpinion.trim().isEmpty()) {
                req.setAttribute("error", "L'avis d'expertise est obligatoire");
                showResponseForm(req, resp, requestId);
                return;
            }

            expertiseRequestService.respondToRequest(requestId, expertOpinion, recommendations);

            req.setAttribute("success", "Réponse envoyée avec succès");
            resp.sendRedirect(req.getContextPath() + "/specialist/expertise");

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error submitting expertise response: " + e.getMessage(), e);
            req.setAttribute("error", "Erreur lors de l'envoi de la réponse: " + e.getMessage());
            showResponseForm(req, resp, Long.parseLong(req.getPathInfo().substring(1).split("/")[0]));
        }
    }
}
