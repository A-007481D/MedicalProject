package org.medxpertise.medicaltelexpertise.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.medxpertise.medicaltelexpertise.infrastructure.config.qualifier.AppEntityManager;

@ApplicationScoped
public class JpaConfig {

    @Produces
    @ApplicationScoped
    @AppEntityManager
    public EntityManager createEntityManager() {
        return Persistence
                .createEntityManagerFactory("MedicalPU")
                .createEntityManager();
    }
}
