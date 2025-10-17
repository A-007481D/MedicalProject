<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dossier Patient</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard/generalist">
                <i class="bi bi-arrow-left"></i> Retour au tableau de bord
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-white me-3">
                    <i class="bi bi-person-circle"></i> 
                    Dr. ${sessionScope.user.firstName} ${sessionScope.user.lastName}
                </span>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Patient Information -->
            <div class="col-md-4">
                <div class="card mb-3">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="bi bi-person-fill"></i> Informations Patient</h5>
                    </div>
                    <div class="card-body">
                        <h4>${patient.firstName} ${patient.lastName}</h4>
                        <hr>
                        <p><strong><i class="bi bi-calendar"></i> Date de naissance:</strong><br>
                            <c:if test="${not empty patient.birthDate}">
                                <c:out value="${patient.birthDate}"/>
                            </c:if>
                            <c:if test="${empty patient.birthDate}">
                                Non spécifiée
                            </c:if>
                        </p>
                        <p><strong><i class="bi bi-gender-ambiguous"></i> Sexe:</strong><br>
                            ${patient.gender}
                        </p>
                        <p><strong><i class="bi bi-card-text"></i> CIN:</strong><br>
                            ${patient.cin}
                        </p>
                        <p><strong><i class="bi bi-phone"></i> Téléphone:</strong><br>
                            ${patient.phone}
                        </p>
                        <p><strong><i class="bi bi-geo-alt"></i> Adresse:</strong><br>
                            ${patient.address}
                        </p>
                    </div>
                </div>

                <!-- Latest Vital Signs -->
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0"><i class="bi bi-heart-pulse"></i> Signes Vitaux Récents</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty vitalSignsHistory && vitalSignsHistory.size() > 0}">
                                <c:set var="latest" value="${vitalSignsHistory[0]}" />
                                <div class="mb-2">
                                    <small class="text-muted">
                                        ${latest.recordedAt}
                                    </small>
                                </div>
                                <p><i class="bi bi-heart-pulse text-danger"></i> 
                                   <strong>Tension:</strong> ${latest.bloodPressure}</p>
                                <p><i class="bi bi-activity text-info"></i> 
                                   <strong>Fréquence cardiaque:</strong> ${latest.pulse} bpm</p>
                                <p><i class="bi bi-thermometer text-warning"></i> 
                                   <strong>Température:</strong> ${latest.temperature}°C</p>
                                <p><i class="bi bi-wind text-primary"></i> 
                                   <strong>Fréquence respiratoire:</strong> ${latest.respiratoryRate}/min</p>
                                <p><i class="bi bi-person"></i> 
                                   <strong>Poids:</strong> ${latest.weight} kg</p>
                                <p><i class="bi bi-arrows-vertical"></i> 
                                   <strong>Taille:</strong> ${latest.height} cm</p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted">Aucun signe vital enregistré</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Consultation Form -->
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0"><i class="bi bi-journal-medical"></i> Nouvelle Consultation</h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/generalist/consultation/create" method="post">
                            <input type="hidden" name="patientId" value="${patient.id}">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            
                            <div class="mb-3">
                                <label for="motif" class="form-label">Motif de consultation *</label>
                                <input type="text" class="form-control" id="motif" name="motif" 
                                       placeholder="Ex: Douleurs abdominales, Fièvre, Contrôle..." required>
                            </div>

                            <div class="mb-3">
                                <label for="observations" class="form-label">Observations / Anamnèse</label>
                                <textarea class="form-control" id="observations" name="observations" 
                                          rows="4" placeholder="Décrivez les symptômes, historique..."></textarea>
                            </div>

                            <div class="mb-3">
                                <label for="clinicalExam" class="form-label">Examen clinique</label>
                                <textarea class="form-control" id="clinicalExam" name="clinicalExam" 
                                          rows="4" placeholder="Résultats de l'examen physique..."></textarea>
                            </div>

                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i> 
                                Après création de la consultation, vous pourrez soit finaliser le traitement directement, 
                                soit demander l'avis d'un spécialiste.
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-save"></i>Créer la consultation
                                </button>
                                <a href="${pageContext.request.contextPath}/dashboard/generalist" 
                                   class="btn btn-secondary">
                                    <i class="bi bi-x-circle"></i> Annuler
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Vital Signs History -->
                <c:if test="${not empty vitalSignsHistory && vitalSignsHistory.size() > 1}">
                    <div class="card mt-3">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-graph-up"></i> Historique des signes vitaux</h5>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-sm">
                                    <thead>
                                        <tr>
                                            <th>Date</th>
                                            <th>Tension</th>
                                            <th>FC</th>
                                            <th>Temp</th>
                                            <th>FR</th>
                                            <th>Poids</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="vs" items="${vitalSignsHistory}" begin="1">
                                            <tr>
                                                <td>${vs.recordedAt}</td>
                                                <td>${vs.bloodPressure}</td>
                                                <td>${vs.pulse}</td>
                                                <td>${vs.temperature}°C</td>
                                                <td>${vs.respiratoryRate}</td>
                                                <td>${vs.weight} kg</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
