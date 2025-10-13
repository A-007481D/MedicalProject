package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.application.service.AuthenticationService;
import org.medxpertise.medicaltelexpertise.domain.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");

        Optional<User> userOpt = Optional.ofNullable(authService.authenticate(identifier, password));

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            if (user.getRole() == null) {
                resp.sendRedirect(req.getContextPath() + "/waiting");
                return;
            }

            switch (user.getRole()) {
                case BASE -> resp.sendRedirect(req.getContextPath() + "/waiting");
                case ADMIN -> resp.sendRedirect(req.getContextPath() + "/dashboard/admin");
                case NURSE -> resp.sendRedirect(req.getContextPath() + "/dashboard/nurse");
                case GENERALIST -> resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");
                case SPECIALIST -> resp.sendRedirect(req.getContextPath() + "/dashboard/specialist");
                default -> resp.sendRedirect(req.getContextPath() + "/login?error=role");
            }

        } else {
            req.setAttribute("error", "Invalid credentials. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}