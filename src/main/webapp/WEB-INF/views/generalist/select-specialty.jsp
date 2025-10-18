<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Request Expertise - Select Specialty</title>
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
                    <h2 class="text-lg font-medium text-gray-900">Request Specialist Expertise</h2>
                    <p class="mt-1 text-sm text-gray-500">Select the required specialty for consultation #${consultation.id}</p>
                </div>
                
                <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
                    <div class="mb-6">
                        <h3 class="text-lg font-medium text-gray-900">Patient Information</h3>
                        <div class="mt-2 grid grid-cols-1 gap-y-4 sm:grid-cols-2 sm:gap-x-6">
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
                        </div>
                    </div>
                    
                    <div class="mb-6">
                        <h3 class="text-lg font-medium text-gray-900">Consultation Details</h3>
                        <div class="mt-2 grid grid-cols-1 gap-y-4 sm:grid-cols-2 sm:gap-x-6">
                            <div>
                                <p class="text-sm font-medium text-gray-500">Date & Time</p>
                                <p class="mt-1 text-sm text-gray-900">
                                    <fmt:formatDate value="${consultation.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm" />
                                </p>
                            </div>
                            <div>
                                <p class="text-sm font-medium text-gray-500">Status</p>
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                    Pending Expertise
                                </span>
                            </div>
                        </div>
                        <div class="mt-4">
                            <p class="text-sm font-medium text-gray-500">Observations</p>
                            <p class="mt-1 text-sm text-gray-900">${consultation.observations}</p>
                        </div>
                    </div>
                    
                    <div class="border-t border-gray-200 pt-6">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">Select Required Specialty</h3>
                        
                        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
                            <c:forEach var="specialty" items="${specialties}">
                                <a href="${pageContext.request.contextPath}/expertise-request/${consultation.id}/specialists/${specialty}" 
                                   class="relative rounded-lg border border-gray-300 bg-white px-6 py-5 shadow-sm flex items-center space-x-3 hover:border-blue-500 focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-blue-500">
                                    <div class="flex-1 min-w-0">
                                        <div class="focus:outline-none">
                                            <span class="absolute inset-0" aria-hidden="true"></span>
                                            <p class="text-sm font-medium text-gray-900">${specialty.displayName}</p>
                                            <p class="text-sm text-gray-500 truncate">Find available specialists</p>
                                        </div>
                                    </div>
                                    <div class="text-gray-400">
                                        <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                        </svg>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                    
                    <div class="mt-8 border-t border-gray-200 pt-5">
                        <div class="flex justify-end">
                            <a href="${pageContext.request.contextPath}/generalist/consultation/${consultation.id}"
                               class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                Back to Consultation
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
