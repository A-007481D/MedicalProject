<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Specialist - ${specialty.displayName}</title>
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
        <div class="max-w-7xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
            <div class="bg-white shadow overflow-hidden sm:rounded-lg">
                <div class="px-4 py-5 sm:px-6 bg-gray-50">
                    <div class="flex items-center">
                        <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}" 
                           class="text-blue-600 hover:text-blue-800 mr-4">
                            <i class="fas fa-arrow-left"></i>
                        </a>
                        <div>
                            <h2 class="text-lg font-medium text-gray-900">Select ${specialty.displayName} Specialist</h2>
                            <p class="mt-1 text-sm text-gray-500">Choose a specialist for consultation #${consultation.id}</p>
                        </div>
                    </div>
                </div>
                
                <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
                    <div class="mb-6">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">Available Specialists</h3>
                        
                        <c:choose>
                            <c:when test="${not empty specialists}">
                                <div class="bg-white shadow overflow-hidden sm:rounded-md">
                                    <ul class="divide-y divide-gray-200">
                                        <c:forEach var="specialist" items="${specialists}">
                                            <li>
                                                <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}/specialist/${specialist.id}/availability" 
                                                   class="block hover:bg-gray-50">
                                                    <div class="px-4 py-4 sm:px-6">
                                                        <div class="flex items-center justify-between">
                                                            <div class="flex items-center">
                                                                <div class="min-w-0 flex-1 flex items-center">
                                                                    <div class="flex-shrink-0 h-12 w-12 rounded-full bg-blue-100 flex items-center justify-center">
                                                                        <span class="text-blue-600 text-lg font-medium">
                                                                            ${specialist.firstName.charAt(0)}${specialist.lastName.charAt(0)}
                                                                        </span>
                                                                    </div>
                                                                    <div class="min-w-0 flex-1 px-4">
                                                                        <div>
                                                                            <p class="text-sm font-medium text-blue-600 truncate">
                                                                                Dr. ${specialist.firstName} ${specialist.lastName}
                                                                            </p>
                                                                            <p class="mt-1 text-sm text-gray-500 truncate">
                                                                                ${specialist.specialty.displayName}
                                                                                <c:if test="${not empty specialist.phone}">
                                                                                    • ${specialist.phone}
                                                                                </c:if>
                                                                            </p>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                                <div class="ml-5 flex-shrink-0">
                                                                    <div class="text-sm text-gray-500">
                                                                        <c:if test="${not empty specialist.profile}">
                                                                            <span class="font-medium text-gray-900">
                                                                                <fmt:formatNumber value="${specialist.profile.tarif}" type="currency" currencyCode="MAD" />
                                                                            </span>
                                                                            <span class="text-gray-500">/consultation</span>
                                                                        </c:if>
                                                                    </div>
                                                                    <div class="mt-1 text-xs text-gray-400">
                                                                        <c:choose>
                                                                            <c:when test="${not empty specialist.profile && not empty specialist.profile.timeslots}">
                                                                                ${fn:length(specialist.profile.timeslots)} available time slots
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                No available time slots
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="ml-2 flex-shrink-0 flex">
                                                                <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                                                    <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                                                </svg>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-12 bg-gray-50 rounded-lg">
                                    <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                                    </svg>
                                    <h3 class="mt-2 text-sm font-medium text-gray-900">No specialists available</h3>
                                    <p class="mt-1 text-sm text-gray-500">There are no ${specialty.displayName} specialists available at the moment.</p>
                                    <div class="mt-6">
                                        <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                            <svg class="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                                <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
                                            </svg>
                                            Back to Specialties
                                        </a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="mt-8 border-t border-gray-200 pt-5">
                        <div class="flex justify-between">
                            <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}" 
                               class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                Back to Specialties
                            </a>
                            <a href="${pageContext.request.contextPath}/consultations/${consultation.id}" 
                               class="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                Cancel Request
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
