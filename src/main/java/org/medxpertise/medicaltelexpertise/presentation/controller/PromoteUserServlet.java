package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medxpertise.medicaltelexpertise.application.service.AdminService;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;

import java.io.IOException;

@WebServlet("/admin/promote")
public class PromoteUserServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long userId = Long.parseLong(req.getParameter("userId"));
        String newRoleStr = req.getParameter("role");

        if (newRoleStr == null || newRoleStr.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/admin?error=invalidRole");
            return;
        }
        Role newRole;
        try {
            newRole = Role.valueOf(newRoleStr);
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/admin?error=invalidRole");
            return;
        }
        adminService.changeUserRole(userId, newRole);
        resp.sendRedirect(req.getContextPath() + "/dashboard/admin?success=roleChanged");
    }
}
