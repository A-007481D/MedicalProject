<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Répondre à la Demande d'Expertise - Medical TeleXpertise</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .consultation-card {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .form-section {
            background-color: #fff;
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 30px;
        }
        .patient-info {
            background-color: #e7f3ff;
            border-left: 4px solid #007bff;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/specialist/dashboard">
                <i class="fas fa-stethoscope me-2"></i>Medical TeleXpertise - Spécialiste
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/specialist/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Tableau de Bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/specialist/expertise">
                            <i class="fas fa-clipboard-list me-1"></i>Demandes d'Expertise
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i>Déconnexion
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <h2><i class="fas fa-reply text-warning me-2"></i>Répondre à la Demande d'Expertise</h2>
                <p class="text-muted">Fournissez votre avis médical d'expert</p>
            </div>
        </div>

        <!-- Consultation Information -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="consultation-card">
                    <h4><i class="fas fa-file-medical text-primary me-2"></i>Informations de la Consultation</h4>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="patient-info">
                                <h6><i class="fas fa-user-circle me-2"></i>Informations du Patient</h6>
                                <p><strong>Nom:</strong> ${request.consultation.patient.firstName} ${request.consultation.patient.lastName}</p>
                                <p><strong>Âge:</strong> ${request.consultation.patient.age} ans</p>
                                <p><strong>Sexe:</strong> ${request.consultation.patient.gender}</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="fas fa-stethoscope me-2"></i>Détails Médicaux</h6>
                            <p><strong>Motif:</strong> ${request.consultation.motif}</p>
                            <p><strong>Observations:</strong> ${request.consultation.observations}</p>
                            <p><strong>Examen clinique:</strong> ${request.consultation.clinicalExam}</p>
                            <p><strong>Date de consultation:</strong> <fmt:formatDate value="${request.consultation.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Generalist Information -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="alert alert-info">
                    <h6><i class="fas fa-user-md me-2"></i>Demandé par le Généraliste</h6>
                    <p><strong>Dr. ${request.consultation.generalist.firstName} ${request.consultation.generalist.lastName}</strong></p>
                    <p><i class="fas fa-envelope me-2"></i>${request.consultation.generalist.email}</p>
                    <c:if test="${not empty request.description}">
                        <div class="mt-2">
                            <strong>Description de la demande:</strong><br>
                            ${request.description}
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Response Form -->
        <div class="row">
            <div class="col-12">
                <div class="form-section">
                    <h4><i class="fas fa-edit text-success me-2"></i>Votre Avis d'Expert</h4>

                    <form method="post" action="${pageContext.request.contextPath}/specialist/expertise/${request.id}/respond">
                        <div class="mb-3">
                            <label for="expertOpinion" class="form-label">
                                <i class="fas fa-comment-medical me-1"></i>Avis d'Expertise <span class="text-danger">*</span>
                            </label>
                            <textarea class="form-control" id="expertOpinion" name="expertOpinion"
                                      rows="8" required placeholder="Décrivez votre analyse médicale et votre avis d'expert...">${request.expertOpinion}</textarea>
                            <div class="form-text">Fournissez une analyse détaillée de la situation médicale du patient</div>
                        </div>

                        <div class="mb-3">
                            <label for="recommendations" class="form-label">
                                <i class="fas fa-prescription me-1"></i>Recommandations
                            </label>
                            <textarea class="form-control" id="recommendations" name="recommendations"
                                      rows="5" placeholder="Recommandations thérapeutiques, examens complémentaires, suivi...">${request.recommendations}</textarea>
                            <div class="form-text">Indiquez les traitements, examens ou suivi recommandé</div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <button type="submit" class="btn btn-success btn-lg">
                                <i class="fas fa-paper-plane me-2"></i>Envoyer la Réponse
                            </button>
                            <a href="${pageContext.request.contextPath}/specialist/expertise" class="btn btn-secondary btn-lg">
                                <i class="fas fa-arrow-left me-2"></i>Retour à la Liste
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
