package org.medxpertise.medicaltelexpertise.presentation.controller;

import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.medxpertise.medicaltelexpertise.domain.model.Consultation;
import org.medxpertise.medicaltelexpertise.domain.model.Doctor;
import org.medxpertise.medicaltelexpertise.domain.model.Patient;
import org.medxpertise.medicaltelexpertise.domain.model.QueueEntry;
import org.medxpertise.medicaltelexpertise.domain.model.User;
import org.medxpertise.medicaltelexpertise.domain.model.enums.ConsultationStatus;
import org.medxpertise.medicaltelexpertise.domain.model.enums.DoctorType;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Role;
import org.medxpertise.medicaltelexpertise.domain.model.enums.Specialty;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/generalist/consultation/create")
public class CreateConsultationServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("MedicalPU");
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        EntityManager em = null;

        try {
            User user = (User) session.getAttribute("user");
            if (user == null || user.getRole() != Role.GENERALIST) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "You must be logged in as a generalist");
                return;
            }

            Long patientId = Long.parseLong(req.getParameter("patientId"));
            String motif = req.getParameter("motif");
            String observations = req.getParameter("observations");
            String clinicalExam = req.getParameter("clinicalExam");

            if (motif == null || motif.trim().isEmpty()) {
                throw new IllegalArgumentException("Motif de consultation est requis");
            }

            em = emf.createEntityManager();
            em.getTransaction().begin();

            System.out.println("=== Starting consultation creation ===");
            System.out.println("Patient ID: " + patientId);
            System.out.println("Generalist ID: " + user.getId());

            Doctor generalist = null;
            try {
                generalist = em.find(Doctor.class, user.getId());
                if (generalist != null) {
                    System.out.println("Found existing Doctor entity: " + generalist.getFirstName() + " " + generalist.getLastName());
                }
            } catch (Exception e) {
                System.out.println("Could not load as Doctor (discriminator mismatch): " + e.getMessage());
            }

            if (generalist == null) {
                System.out.println("Need to fix user_type discriminator and create Doctor record...");

                em.createNativeQuery("UPDATE users SET user_type = 'DOCTOR' WHERE id = :userId")
                        .setParameter("userId", user.getId())
                        .executeUpdate();

                System.out.println("Updated user_type discriminator to DOCTOR");

                Long doctorCount = em.createQuery(
                                "SELECT COUNT(d) FROM Doctor d WHERE d.id = :id", Long.class)
                        .setParameter("id", user.getId())
                        .getSingleResult();

                if (doctorCount == 0) {
                    em.createNativeQuery(
                                    "INSERT INTO doctor (id, doctor_type, specialty, phone) VALUES (:id, :doctorType, :specialty, :phone)")
                            .setParameter("id", user.getId())
                            .setParameter("doctorType", "GENERALIST")
                            .setParameter("specialty", "GENERAL_PRACTICE")
                            .setParameter("phone", null)
                            .executeUpdate();

                    System.out.println("Created Doctor record in database");
                }

                em.flush();
                em.clear();

                generalist = em.find(Doctor.class, user.getId());
                if (generalist == null) {
                    throw new IllegalStateException("Failed to create/load Doctor entity");
                }
                System.out.println("Successfully loaded Doctor: " + generalist.getFirstName() + " " + generalist.getLastName());
            }

            Patient patient = em.find(Patient.class, patientId);
            if (patient == null) {
                throw new IllegalArgumentException("Patient non trouvé avec l'ID: " + patientId);
            }
            System.out.println("Found patient: " + patient.getFirstName() + " " + patient.getLastName());

            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setGeneralist(generalist);
            consultation.setMotif(motif);
            consultation.setObservations(observations);
            consultation.setClinicalExam(clinicalExam);
            consultation.setStatus(ConsultationStatus.PENDING);
            consultation.setBaseCost(new java.math.BigDecimal("150.00"));
            consultation.setCreatedAt(LocalDateTime.now());

            System.out.println("Persisting consultation...");

            em.persist(consultation);
            em.flush();

            System.out.println("Consultation persisted with ID: " + consultation.getId());

            try {
                QueueEntry queueEntry = em.createQuery(
                                "SELECT q FROM QueueEntry q WHERE q.patient.id = :patientId",
                                QueueEntry.class)
                        .setParameter("patientId", patientId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (queueEntry != null) {
                    em.remove(queueEntry);
                    System.out.println("Removed patient from queue");
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not remove from queue: " + e.getMessage());
            }

            System.out.println("Committing transaction...");
            em.getTransaction().commit();
            System.out.println("Transaction committed successfully!");

            session.setAttribute("successMessage", "Consultation créée avec succès!");
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");

        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid patient ID format");
            e.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            session.setAttribute("errorMessage", "Format d'ID patient invalide");
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");

        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Validation failed - " + e.getMessage());
            e.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            session.setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");

        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error during consultation creation");
            e.printStackTrace();
            if (em != null && em.getTransaction().isActive()) {
                System.err.println("Rolling back transaction...");
                em.getTransaction().rollback();
            }
            session.setAttribute("errorMessage",
                    "Erreur lors de la création de la consultation: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/dashboard/generalist");

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("EntityManager closed");
            }
        }
    }
}