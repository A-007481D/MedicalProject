package org.medxpertise.medicaltelexpertise.application.service;

import org.medxpertise.medicaltelexpertise.domain.model.VitalSign;
import org.medxpertise.medicaltelexpertise.domain.repository.VitalSignRepository;
import org.medxpertise.medicaltelexpertise.infrastructure.repository.VitalSignRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VitalSignsService {
    private final VitalSignRepository vitalSignRepository = new VitalSignRepositoryJpa();

    public VitalSign saveVitalSign(VitalSign vitalSign) {
        return vitalSignRepository.save(vitalSign);
    }

    public Optional<VitalSign> getVitalSignById(Long id) {
        return vitalSignRepository.findById(id);
    }

    public List<VitalSign> getVitalSignsByPatient(Long patientId) {
        return vitalSignRepository.findAll().stream()
                .filter(vs -> vs.getPatient() != null && vs.getPatient().getId().equals(patientId))
                .sorted((vs1, vs2) -> vs2.getRecordedAt().compareTo(vs1.getRecordedAt()))
                .toList();
    }

    public Optional<VitalSign> getLatestVitalSignByPatient(Long patientId) {
        return vitalSignRepository.findLatestByPatient(patientId);
    }

    public List<VitalSign> getVitalSignsByPatientBetween(Long patientId, LocalDateTime start, LocalDateTime end) {
        return vitalSignRepository.findByPatientBetween(patientId, start, end);
    }

    public void deleteVitalSign(Long id) {
        vitalSignRepository.deleteById(id);
    }
}
