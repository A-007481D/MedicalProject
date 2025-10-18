<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">
    <title>My Profile - Specialist</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .profile-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .alert {
            margin-bottom: 1.5rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="profile-container">
            <h2 class="mb-4">My Specialist Profile</h2>
            
            <!-- Success/Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    ${successMessage}
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">
                    ${errorMessage}
                </div>
            </c:if>
            
            <!-- Profile Form -->
            <form id="profileForm" action="${pageContext.request.contextPath}/specialist/profile" method="post" class="needs-validation" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <div class="mb-3">
                    <label for="tarif" class="form-label">Consultation Fee (DH)</label>
                    <div class="input-group">
                        <input type="number" step="0.01" min="0" class="form-control" id="tarif" name="tarif" 
                               value="<fmt:formatNumber value='${profile.tarif}' pattern='0.00'/>" required>
                        <span class="input-group-text">DH</span>
                        <div class="invalid-feedback">
                            Please enter a valid consultation fee.
                        </div>
                    </div>
                </div>
                
                <div class="mb-4">
                    <label for="slotDuration" class="form-label">Default Consultation Duration (minutes)</label>
                    <select class="form-select" id="slotDuration" name="slotDuration" required>
                        <option value="15" ${profile.slotDurationMinutes == 15 ? 'selected' : ''}>15 minutes</option>
                        <option value="30" ${profile.slotDurationMinutes == 30 ? 'selected' : ''}>30 minutes</option>
                        <option value="45" ${profile.slotDurationMinutes == 45 ? 'selected' : ''}>45 minutes</option>
                        <option value="60" ${profile.slotDurationMinutes == 60 ? 'selected' : ''}>1 hour</option>
                    </select>
                    <div class="invalid-feedback">
                        Please select a consultation duration.
                    </div>
                </div>
                
                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
            
            <!-- Timeslot Management Section -->
            <div class="mt-5">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4>My Availability</h4>
                    <button class="btn btn-sm btn-outline-primary" id="addTimeslotBtn">
                        <i class="bi bi-plus"></i> Add Time Slot
                    </button>
                </div>
                
                <!-- Timeslot List -->
                <div class="list-group" id="timeslotList">
                    <c:if test="${empty profile.timeslots}">
                        <div class="alert alert-info">
                            No time slots have been added yet. Click "Add Time Slot" to get started.
                        </div>
                    </c:if>
                    
                    <c:forEach items="${profile.timeslots}" var="timeslot">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="mb-1">
                                    <fmt:formatDate value="${timeslot.start}" pattern="EEEE, MMMM d, yyyy" />
                                </h6>
                                <small class="text-muted">
                                    <fmt:formatDate value="${timeslot.start}" pattern="h:mm a" /> - 
                                    <fmt:formatDate value="${timeslot.end}" pattern="h:mm a" />
                                    <c:choose>
                                        <c:when test="${timeslot.status == 'AVAILABLE'}">
                                            <span class="badge bg-success ms-2">${timeslot.status}</span>
                                        </c:when>
                                        <c:when test="${timeslot.status == 'RESERVED'}">
                                            <span class="badge bg-warning ms-2">${timeslot.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary ms-2">${timeslot.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </small>
                            </div>
                            <div>
                                <button class="btn btn-sm btn-outline-danger" 
                                        onclick="confirmDeleteTimeslot(${timeslot.id})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- Add Timeslot Modal -->
    <div class="modal fade" id="timeslotModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add Available Time Slot</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="timeslotForm" action="${pageContext.request.contextPath}/api/timeslots" method="post">
                    <!-- Add CSRF token -->
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="date" class="form-label">Date</label>
                            <input type="date" class="form-control" id="date" name="date" required>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="startTime" class="form-label">Start Time</label>
                                <input type="time" class="form-control" id="startTime" name="startTime" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="endTime" class="form-label">End Time</label>
                                <input type="time" class="form-control" id="endTime" name="endTime" required>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save Time Slot</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        (function () {
            'use strict'
            var forms = document.querySelectorAll('.needs-validation')
            Array.prototype.slice.call(forms)
                .forEach(function (form) {
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault()
                            event.stopPropagation()
                        }
                        form.classList.add('was-validated')
                    }, false)
                })
        })()
        
        // Timeslot Modal
        document.getElementById('addTimeslotBtn').addEventListener('click', function() {
            const modal = new bootstrap.Modal(document.getElementById('timeslotModal'));
            modal.show();
        });
        
        // Set default date to today
        document.addEventListener('DOMContentLoaded', function() {
            const today = new Date().toISOString().split('T')[0];
            const dateInput = document.getElementById('date');
            if (dateInput) {
                dateInput.min = today;
                dateInput.value = today;
            }
        });
        
        // Function to get CSRF token
        function getCsrfToken() {
            // First try to get from meta tags
            const metaToken = document.querySelector('meta[name="_csrf"]');
            const metaHeader = document.querySelector('meta[name="_csrf_header"]');
            
            if (metaToken && metaHeader) {
                const headerName = metaHeader.content || 'X-CSRF-TOKEN';
                return {
                    token: metaToken.content,
                    headerName: headerName
                };
            }
            
            // Fallback to form input
            const formToken = document.querySelector('input[name="_csrf"]');
            if (formToken) {
                return {
                    token: formToken.value,
                    headerName: 'X-CSRF-TOKEN' // Default header name
                };
            }
            
            console.error('CSRF token not found on page');
            return null;
        }

        // Handle timeslot form submission
        document.getElementById('timeslotForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const form = e.target;
            
            // Get CSRF token directly from meta tag or form input
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || 
                            document.querySelector('input[name="_csrf"]')?.value;
            
            if (!csrfToken) {
                console.error('CSRF token not found on page');
                alert('Security error: CSRF token missing. Please refresh the page and try again.');
                return;
            }
            
            console.log('CSRF Token:', csrfToken);
            
            try {
                // Get form values
                const date = form.querySelector('[name="date"]').value;
                const startTime = form.querySelector('[name="startTime"]').value;
                const endTime = form.querySelector('[name="endTime"]').value;
                
                if (!date || !startTime || !endTime) {
                    throw new Error('Please fill in all required fields');
                }
                
                const data = { date, startTime, endTime };
                console.log('Sending timeslot data:', data);
                
                // Get the CSRF token from the meta tag or form input
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || 
                                document.querySelector('input[name="_csrf"]')?.value;
                
                if (!csrfToken) {
                    throw new Error('CSRF token not found on page');
                }
                
                // Create headers object with proper CSRF header
                const headers = {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                };
                
                console.log('Sending request with headers:', headers);
                
                const response = await fetch(form.action, {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify(data),
                    credentials: 'same-origin'
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('Server responded with:', errorText);
                    try {
                        const error = JSON.parse(errorText);
                        throw new Error(error.message || 'Failed to save timeslot');
                    } catch (e) {
                        throw new Error('Failed to parse server response: ' + errorText);
                    }
                }

                // Close the modal and refresh the page to show the new timeslot
                const modal = bootstrap.Modal.getInstance(document.getElementById('timeslotModal'));
                if (modal) {
                    modal.hide();
                }
                window.location.reload();
                
            } catch (error) {
                console.error('Error saving timeslot:', error);
                alert('Error: ' + (error.message || 'Failed to save timeslot. Please try again.'));
            }
        });
        
        // Confirm timeslot deletion
        function confirmDeleteTimeslot(timeslotId) {
            if (confirm('Are you sure you want to delete this time slot?')) {
                // TODO: Send AJAX request to delete timeslot
                console.log('Deleting timeslot:', timeslotId);
                alert('Time slot deleted successfully!');
            }
        }
    </script>
</body>
</html>
