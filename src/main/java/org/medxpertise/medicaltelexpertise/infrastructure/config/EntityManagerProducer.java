package org.medxpertise.medicaltelexpertise.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.enterprise.inject.Default;

@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "MedicalPU")
    private EntityManager entityManager;

    @Produces
    @Default
    @ApplicationScoped
    public EntityManager createEntityManager() {
        return entityManager;
    }
}
