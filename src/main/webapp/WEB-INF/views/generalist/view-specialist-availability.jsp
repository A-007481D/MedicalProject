<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Specialist Availability - Dr. ${specialist.firstName} ${specialist.lastName}</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body class="bg-gray-50">
    <div class="min-h-screen">
        <!-- Header -->
        <div class="bg-white shadow">
            <div class="max-w-7xl mx-auto px-4 py-4 sm:px-6 lg:px-8 flex justify-between items-center">
                <h1 class="text-2xl font-bold text-gray-900">Medical Tele-Expertise</h1>
                <div class="flex items-center space-x-4">
                    <span class="text-gray-700">${user.firstName} ${user.lastName}</span>
                    <a href="${pageContext.request.contextPath}/logout" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </div>
            </div>
        </div>

        <!-- Main Content -->
        <div class="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
            <div class="bg-white shadow overflow-hidden sm:rounded-lg">
                <div class="px-4 py-5 sm:px-6 bg-gray-50">
                    <div class="flex items-center">
                        <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}/specialists/${specialist.specialty}" 
                           class="text-blue-600 hover:text-blue-800 mr-4">
                            <i class="fas fa-arrow-left"></i>
                        </a>
                        <div>
                            <h2 class="text-lg font-medium text-gray-900">Dr. ${specialist.firstName} ${specialist.lastName}</h2>
                            <p class="mt-1 text-sm text-gray-500">${specialist.specialty.displayName} • 
                                <c:if test="${not empty specialist.phone}">
                                    ${specialist.phone} • 
                                </c:if>
                                <c:if test="${not empty specialist.profile}">
                                    <span class="font-medium">
                                        <fmt:formatNumber value="${specialist.profile.tarif}" type="currency" currencyCode="MAD" />
                                    </span>
                                    <span class="text-gray-500">/consultation</span>
                                </c:if>
                            </p>
                        </div>
                    </div>
                </div>
                
                <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
                    <div class="mb-8">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">Patient Information</h3>
                        <div class="bg-gray-50 p-4 rounded-lg">
                            <div class="grid grid-cols-1 gap-y-4 sm:grid-cols-2 sm:gap-x-6">
                                <div>
                                    <p class="text-sm font-medium text-gray-500">Patient Name</p>
                                    <p class="mt-1 text-sm text-gray-900">${consultation.patient.firstName} ${consultation.patient.lastName}</p>
                                </div>
                                <c:if test="${not empty consultation.patient.phone}">
                                    <div>
                                        <p class="text-sm font-medium text-gray-500">Phone</p>
                                        <p class="mt-1 text-sm text-gray-900">${consultation.patient.phone}</p>
                                    </div>
                                </c:if>
                                <div class="sm:col-span-2">
                                    <p class="text-sm font-medium text-gray-500">Consultation Notes</p>
                                    <p class="mt-1 text-sm text-gray-900">${consultation.observations}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <form id="expertiseRequestForm" action="${pageContext.request.contextPath}/expertise-request/${consultation.id}/submit" method="post">
                        <input type="hidden" name="specialistId" value="${specialist.id}">
                        
                        <div class="mb-6">
                            <label for="question" class="block text-sm font-medium text-gray-700">Your Question to the Specialist</label>
                            <div class="mt-1">
                                <textarea id="question" name="question" rows="4" 
                                    class="shadow-sm focus:ring-blue-500 focus:border-blue-500 block w-full sm:text-sm border border-gray-300 rounded-md p-2"
                                    placeholder="Please provide details about the patient's condition and what you'd like the specialist to focus on..."
                                    required></textarea>
                            </div>
                            <p class="mt-2 text-sm text-gray-500">Be specific about what you're asking the specialist to review.</p>
                        </div>
                        
                        <div class="mb-6">
                            <label class="block text-sm font-medium text-gray-700">Priority Level</label>
                            <div class="mt-2 space-y-2">
                                <div class="flex items-center">
                                    <input id="priority-normal" name="priority" type="radio" value="NORMAL" 
                                           class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300" checked>
                                    <label for="priority-normal" class="ml-3">
                                        <span class="block text-sm text-gray-700">Normal Priority</span>
                                        <span class="block text-xs text-gray-500">Response within 48 hours</span>
                                    </label>
                                </div>
                                <div class="flex items-center">
                                    <input id="priority-high" name="priority" type="radio" value="HIGH" 
                                           class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300">
                                    <label for="priority-high" class="ml-3">
                                        <span class="block text-sm text-gray-700">High Priority</span>
                                        <span class="block text-xs text-gray-500">Response within 24 hours (additional fees may apply)</span>
                                    </label>
                                </div>
                                <div class="flex items-center">
                                    <input id="priority-urgent" name="priority" type="radio" value="URGENT" 
                                           class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300">
                                    <label for="priority-urgent" class="ml-3">
                                        <span class="block text-sm text-gray-700">Urgent</span>
                                        <span class="block text-xs text-gray-500">Immediate attention needed (additional fees apply)</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-6">
                            <label class="block text-sm font-medium text-gray-700 mb-2">Available Time Slots</label>
                            
                            <c:choose>
                                <c:when test="${not empty specialist.profile.timeslots}">
                                    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3">
                                        <c:forEach var="timeslot" items="${specialist.profile.timeslots}" varStatus="loop">
                                            <div class="relative flex items-start">
                                                <div class="flex items-center h-5">
                                                    <input id="timeslot-${loop.index}" name="timeslot" type="radio" 
                                                           value="${timeslot.start},${timeslot.end}" 
                                                           class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300"
                                                           ${loop.first ? 'checked' : ''}>
                                                </div>
                                                <div class="ml-3 text-sm">
                                                    <label for="timeslot-${loop.index}" class="font-medium text-gray-700 cursor-pointer">
                                                        <fmt:formatDate value="${timeslot.start}" pattern="EEEE, MMMM d" />
                                                    </label>
                                                    <p class="text-gray-500">
                                                        <fmt:formatDate value="${timeslot.start}" pattern="h:mm a" /> - 
                                                        <fmt:formatDate value="${timeslot.end}" pattern="h:mm a" />
                                                    </p>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    
                                    <input type="hidden" id="timeslotStart" name="timeslotStart" value="">
                                    <input type="hidden" id="timeslotEnd" name="timeslotEnd" value="">
                                    
                                    <script>
                                        // Set the hidden input values when the form is submitted
                                        document.getElementById('expertiseRequestForm').addEventListener('submit', function(e) {
                                            const selectedTimeslot = document.querySelector('input[name="timeslot"]:checked').value.split(',');
                                            document.getElementById('timeslotStart').value = selectedTimeslot[0];
                                            document.getElementById('timeslotEnd').value = selectedTimeslot[1];
                                        });
                                    </script>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-8 bg-gray-50 rounded-lg">
                                        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                        </svg>
                                        <h3 class="mt-2 text-sm font-medium text-gray-900">No available time slots</h3>
                                        <p class="mt-1 text-sm text-gray-500">This specialist doesn't have any available time slots at the moment.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <div class="bg-gray-50 px-4 py-3 text-right sm:px-6 rounded-b-lg">
                            <div class="flex justify-between items-center">
                                <div class="text-sm text-gray-500">
                                    <p>Total: <span class="font-medium text-gray-900">
                                        <fmt:formatNumber value="${specialist.profile.tarif}" type="currency" currencyCode="MAD" />
                                        <span class="text-xs text-gray-500"> + consultation fee</span>
                                    </span></p>
                                </div>
                                <div class="flex space-x-3">
                                    <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}/specialists/${specialist.specialty}" 
                                       class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                        Back
                                    </a>
                                    <button type="submit" 
                                            class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                        Submit Request
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
