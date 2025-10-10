<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MediCare Hospital - Authentication</title>
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <style>
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .animate-fadeIn {
            animation: fadeIn 0.6s ease-out;
        }
        .gradient-bg {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .glass-effect {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
        }
    </style>
</head>
<body class="gradient-bg min-h-screen flex items-center justify-center p-4">
    <div class="w-full max-w-md">
        <!-- Login Form -->
        <div id="loginForm" class="glass-effect rounded-2xl shadow-2xl p-8 animate-fadeIn">
            <div class="text-center mb-8">
                <div class="inline-block bg-gradient-to-r from-blue-600 to-purple-600 p-3 rounded-full mb-4">
                    <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                    </svg>
                </div>
                <h2 class="text-3xl font-bold text-gray-800">Welcome Back</h2>
                <p class="text-gray-600 mt-2">Sign in to your MediCare account</p>
            </div>

            <form action="${pageContext.request.contextPath}/login" method="post" class="space-y-6">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                    <input type="email" name="email" placeholder="doctor@medicare.com" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-200 outline-none">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
                    <input type="password" name="password" placeholder="••••••••" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-purple-600 focus:border-transparent transition duration-200 outline-none">
                </div>

                <div class="flex items-center justify-between">
                    <label class="flex items-center">
                        <input type="checkbox" name="remember-me" class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500">
                        <span class="ml-2 text-sm text-gray-700">Remember me</span>
                    </label>
                    <button type="button" onclick="showForm('forgotForm')" class="text-sm text-purple-600 hover:text-purple-800 font-medium transition duration-200">
                        Forgot Password?
                    </button>
                </div>

                <button type="submit" class="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:-translate-y-0.5 transition duration-200">
                    Sign In
                </button>

                <div class="text-center text-sm text-gray-600">
                    Don't have an account?
                    <button type="button" onclick="showForm('registerForm')" class="text-purple-600 hover:text-purple-800 font-medium transition duration-200">
                        Sign Up
                    </button>
                </div>
            </form>
        </div>

        <!-- Register Form -->
        <div id="registerForm" class="glass-effect rounded-2xl shadow-2xl p-8 hidden">
            <div class="text-center mb-8">
                <div class="inline-block bg-gradient-to-r from-green-600 to-blue-600 p-3 rounded-full mb-4">
                    <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                    </svg>
                </div>
                <h2 class="text-3xl font-bold text-gray-800">Create Account</h2>
                <p class="text-gray-600 mt-2">Join MediCare Hospital today</p>
            </div>

            <form action="${pageContext.request.contextPath}/register" method="post" class="space-y-5">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">First Name</label>
                        <input type="text" name="firstName" placeholder="John" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">Last Name</label>
                        <input type="text" name="lastName" placeholder="Doe" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                    <input type="email" name="email" placeholder="john.doe@example.com" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">User Type</label>
                    <select name="userType" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                        <option value="PATIENT">Patient</option>
                        <option value="DOCTOR">Doctor</option>
                        <option value="NURSE">Nurse</option>
                        <option value="ADMIN">Administrator</option>
                    </select>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
                    <input type="password" name="password" placeholder="••••••••" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Confirm Password</label>
                    <input type="password" name="confirmPassword" placeholder="••••••••" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-600 focus:border-transparent transition duration-200 outline-none">
                </div>

                <label class="flex items-start">
                    <input type="checkbox" name="terms" required class="w-4 h-4 mt-1 text-blue-600 border-gray-300 rounded focus:ring-blue-500">
                    <span class="ml-2 text-sm text-gray-700">
                        I agree to the <a href="#" class="text-blue-600 hover:text-blue-800">Terms of Service</a> and <a href="#" class="text-blue-600 hover:text-blue-800">Privacy Policy</a>
                    </span>
                </label>

                <button type="submit" class="w-full bg-gradient-to-r from-green-600 to-blue-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:-translate-y-0.5 transition duration-200">
                    Create Account
                </button>

                <div class="text-center text-sm text-gray-600">
                    Already have an account?
                    <button type="button" onclick="showForm('loginForm')" class="text-blue-600 hover:text-blue-800 font-medium transition duration-200">
                        Sign In
                    </button>
                </div>
            </form>
        </div>

        <!-- Forgot Password Form -->
        <div id="forgotForm" class="glass-effect rounded-2xl shadow-2xl p-8 hidden">
            <div class="text-center mb-8">
                <div class="inline-block bg-gradient-to-r from-orange-600 to-red-600 p-3 rounded-full mb-4">
                    <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"></path>
                    </svg>
                </div>
                <h2 class="text-3xl font-bold text-gray-800">Reset Password</h2>
                <p class="text-gray-600 mt-2">We'll send you instructions via email</p>
            </div>

            <form action="${pageContext.request.contextPath}/forgot-password" method="post" class="space-y-6">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                    <input type="email" name="email" placeholder="doctor@medicare.com" required class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-orange-600 focus:border-transparent transition duration-200 outline-none">
                    <p class="mt-2 text-sm text-gray-500">Enter the email address associated with your account</p>
                </div>

                <button type="submit" class="w-full bg-gradient-to-r from-orange-600 to-red-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:-translate-y-0.5 transition duration-200">
                    Send Reset Link
                </button>

                <div class="text-center">
                    <button type="button" onclick="showForm('loginForm')" class="text-sm text-gray-600 hover:text-gray-800 font-medium transition duration-200 flex items-center justify-center mx-auto">
                        <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
                        </svg>
                        Back to Sign In
                    </button>
                </div>
            </form>
        </div>

        <!-- Reset Password Success -->
        <div id="resetSuccessForm" class="glass-effect rounded-2xl shadow-2xl p-8 hidden">
            <div class="text-center mb-8">
                <div class="inline-block bg-green-500 p-3 rounded-full mb-4">
                    <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                </div>
                <h2 class="text-3xl font-bold text-gray-800">Check Your Email</h2>
                <p class="text-gray-600 mt-2">We've sent password reset instructions to your email</p>
            </div>

            <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                <p class="text-sm text-blue-800">
                    <strong>Didn't receive the email?</strong><br>
                    Check your spam folder or request a new reset link
                </p>
            </div>

            <button type="button" onclick="showForm('loginForm')" class="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:-translate-y-0.5 transition duration-200">
                Return to Sign In
            </button>
        </div>

        <!-- Footer -->
        <div class="text-center mt-6 text-white text-sm">
            <p>&copy; 2025 MediCare Hospital. All rights reserved.</p>
        </div>
    </div>

    <script>
        function showForm(formId) {
            const forms = ['loginForm', 'registerForm', 'forgotForm', 'resetSuccessForm'];
            forms.forEach(id => {
                const element = document.getElementById(id);
                if (element) {
                    if (id === formId) {
                        element.classList.remove('hidden');
                        element.classList.add('animate-fadeIn');
                    } else {
                        element.classList.add('hidden');
                    }
                }
            });
        }

        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('form').forEach(form => {
                form.addEventListener('submit', function(e) {
                    e.preventDefault();
                    if (this.closest('#forgotForm')) {
                        showForm('resetSuccessForm');
                    } else {
                        this.submit();
                    }
                });
            });
        });
    </script>
</body>
</html>
