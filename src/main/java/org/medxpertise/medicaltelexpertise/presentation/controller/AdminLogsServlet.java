package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medxpertise.medicaltelexpertise.domain.model.AuditLog;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.AuditLogRepositoryJpa;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/logs")
public class AdminLogsServlet extends HttpServlet {

    private AuditLogRepositoryJpa auditRepo;

    @Override
    public void init() throws ServletException {
        this.auditRepo = new AuditLogRepositoryJpa();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long userId = Long.parseLong(req.getParameter("userId"));
        List<AuditLog> logs = auditRepo.findByActorId(userId);

        req.setAttribute("logs", logs);
        req.getRequestDispatcher("/WEB-INF/views/admin/logs.jsp").forward(req, resp);
    }
}