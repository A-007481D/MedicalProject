package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/waiting")
public class WaitingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        if (user.getRole() != null && user.getRole() != Role.BASE) {
            switch (user.getRole()) {
                case ADMIN -> resp.sendRedirect(req.getContextPath() + "/dashboard/admin");
                case NURSE -> resp.sendRedirect(req.getContextPath() + "/dashboard/nurse");
                case GENERALIST -> resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                case SPECIALIST -> resp.sendRedirect(req.getContextPath() + "/dashboard/specialist");
                default -> resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }
        
        req.getRequestDispatcher("/WEB-INF/views/waiting.jsp").forward(req, resp);
    }
}
