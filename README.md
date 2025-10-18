# Medical Tele-Expertise System

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.0.0.Final-blueviolet.svg)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

Medical Tele-Expertise System is a comprehensive healthcare coordination platform that facilitates seamless collaboration between general practitioners and medical specialists. The system streamlines patient care management, specialist consultations, and medical expertise sharing in a secure, efficient, and user-friendly manner.

## Key Features

### Role-Based Access Control
- **Nurses**: Patient registration, vital signs collection, and queue management
- **General Practitioners (GPs)**: Patient consultations and specialist referrals
- **Specialists**: Profile management, schedule configuration, and expertise provision
- **Administrators**: System and user management

### Core Functionalities
- Patient registration and medical history management
- Appointment scheduling and queue management
- Secure messaging and file sharing
- Electronic health records (EHR) management
- Specialist consultation requests
- Real-time availability and scheduling
- Secure authentication and authorization

## Technology Stack

### Backend
- **Java 17+**
- **Hibernate 6.0.0**
- **JPA**
- **Jakarta EE 11**

### Frontend
- **JSP/JSTL** (Server-side templating)
- **Bootstrap 5**
- **AJAX** for asynchronous operations

### Database
- **PostgreSQL 14+**
- **JPA/Hibernate** for ORM

### Security
- **BCrypt** password hashing
- **CSRF Protection**
- **Session Management**

## Getting Started

### Prerequisites
- Java 17 or higher
- Apache Maven 3.8.6 or higher
- PostgreSQL 14 or higher
- Apache Tomcat 10.1.16 or compatible servlet container

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/MedicalTeleXpertise.git
   cd MedicalTeleXpertise
   ```

2. **Database Setup**
   - Create a new PostgreSQL database
   - Update `application.properties` with your database credentials

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
 
   ```
   Or deploy the WAR file to your Tomcat server

5. **Access the application**
   - Open your browser and navigate to `http://localhost:8080`
   - Login with default credentials (see Configuration section)

    
## Security Considerations

- All passwords are hashed using BCrypt
- CSRF protection is enabled
- Session management with proper timeouts
- Role-based access control
- Input validation and sanitization


## License

Distributed under the MIT License. See `LICENSE` for more information.
