package org.medxpertise.medicaltelexpertise.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.medxpertise.medicaltelexpertise.domain.model.Nurse;
import org.medxpertise.medicaltelexpertise.domain.repository.NurseRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.config.JpaUtil;

import java.util.List;
import java.util.Optional;

public class NurseRepositoryJpa implements NurseRepository {

    private EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    @Override
    public Optional<Nurse> findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(Nurse.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Nurse> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM Nurse n ORDER BY n.lastName, n.firstName",
                            Nurse.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Nurse save(Nurse nurse) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Nurse result;
            if (nurse.getId() == null) {
                em.persist(nurse);
                result = nurse;
            } else {
                result = em.merge(nurse);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Nurse managed = em.find(Nurse.class, id);
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Nurse> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM Nurse n WHERE LOWER(n.email) = :email",
                            Nurse.class)
                    .setParameter("email", email.toLowerCase())
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
