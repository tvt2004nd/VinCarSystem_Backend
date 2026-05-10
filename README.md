<h1 align="center"> E_Commerce_Back </h1>

<p align="center"> The high-performance digital engine powering the next generation of automotive retail and customer engagement. </p>

<p align="center">
  <img alt="Build" src="https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge">
  <img alt="Issues" src="https://img.shields.io/badge/Issues-0%20Open-blue?style=for-the-badge">
  <img alt="Contributions" src="https://img.shields.io/badge/Contributions-Welcome-orange?style=for-the-badge">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge">
</p>
<!-- 
  **Note:** These are static placeholder badges. Replace them with your project's actual badges.
  You can generate your own at https://shields.io
-->

## 📌 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Tech Stack & Architecture](#-tech-stack--architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**E_Commerce_Back** (VinSystem) is an enterprise-grade backend ecosystem designed to revolutionize the automotive purchasing journey. By digitizing the end-to-end process—from initial vehicle exploration and AI-powered consultations to secure financial deposits and service appointments—this platform bridges the gap between traditional showrooms and the modern digital consumer.

> ### ⚡ The Problem
> The traditional automotive buying process is often fragmented, opaque, and slow. Customers face hurdles when trying to compare complex technical specifications, securing test drives, or making deposits through non-secure channels. For dealerships, managing inventory, staff routing for customer support, and maintaining a consistent history of customer interactions across different touchpoints remains a significant operational challenge.

> ### 💡 The Solution
> E_Commerce_Back provides a unified, secure, and highly scalable microservices-based architecture that orchestrates the entire customer lifecycle. It empowers users with real-time data on vehicle powertrains, dimensions, and EV-specific metrics while offering a seamless communication layer through hybrid AI-Staff chat systems. By integrating secure payment gateways and automated document generation, it transforms car buying into a transparent, click-to-buy experience.

### 🏗️ Architecture Overview
The system is built on a **Microservices-inspired modular architecture** using **Java 21** and **Spring Boot 3.2.5**. It leverages a robust service-oriented design where specific domains (Car Management, Payments, Chat, Appointments) are decoupled to ensure high availability, easier maintenance, and independent scalability.

---

## ✨ Key Features

### 🚗 Comprehensive Vehicle Intelligence
Users can explore every facet of their future vehicle with unparalleled detail.
- **Dynamic Spec Comparisons:** Access verified data on powertrains, dimensions, safety features, and comfort options.
- **EV-Specific Metrics:** Specialized tracking for electric vehicle specifications, ensuring customers understand range, battery capacity, and charging capabilities.
- **Visual Configurations:** Management of high-resolution car imagery and color options to help users visualize their exact configuration.

### 💬 Intelligent Communication Hub
A sophisticated multi-channel chat system ensures that no customer query goes unanswered.
- **AI-Powered Assistance:** An integrated AIChatService provides instant responses to common inquiries, available 24/7.
- **Smart Staff Routing:** When complexity increases, the system intelligently routes conversations to online staff members based on availability and expertise.
- **Context-Aware Conversations:** The `CarContextService` ensures that support staff have immediate visibility into the vehicle the customer is currently viewing, leading to faster resolutions.

### 💳 Secure Financial Orchestration
Enterprise-level transaction management designed for high-value automotive purchases.
- **Integrated Payments:** Seamless integration with **VNPay** for secure, real-time online deposits.
- **Deposit Tracking:** Dual-mode handling for both online and offline deposits, providing a clear audit trail for financial teams.
- **Automated Documentation:** Utilizing **OpenPDF**, the system generates professional contract templates and deposit receipts instantly, reducing administrative overhead.

### 📅 Seamless Operations Management
Optimizing the workflow between the digital storefront and physical branches.
- **Automated Appointment Scheduling:** Users can book test drives or consultations, which are then assigned to the appropriate staff members at specific branches.
- **Dashboard Analytics:** A comprehensive reporting suite for administrators to monitor revenue trends, recent transactions, and system status at a glance.
- **Branch-Specific Logic:** Manage inventory and staff across multiple geographical locations through a centralized interface.

### 🔐 Advanced Security & Auth
- **Stateful & Stateless Protection:** Utilizing **JWT (JSON Web Tokens)** for secure, stateless API communication alongside robust Spring Security configurations.
- **Role-Based Access Control (RBAC):** Granular permissions ensuring that Customers, Staff, and Admins only access the data relevant to their roles.

---

## 🛠️ Tech Stack & Architecture

The project utilizes a modern, high-performance technology stack selected for its stability, developer productivity, and enterprise support.

| Technology | Purpose | Why it was Chosen |
| :--- | :--- | :--- |
| **Java 21** | Primary Language | Provides the latest LTS features, including virtual threads and enhanced performance. |
| **Spring Boot 3.2.5** | Backend Framework | The industry standard for building robust, production-ready microservices with minimal configuration. |
| **Spring Data JPA** | Persistence Layer | Simplifies database interactions and reduces boilerplate code for CRUD operations. |
| **MySQL** | Database | A reliable, relational database for maintaining complex car specifications and transaction records. |
| **Spring Security** | Security | Provides a comprehensive security framework for authentication and authorization. |
| **JWT (jjwt)** | Authentication | Enables secure, stateless communication between the client and the backend. |
| **WebSocket** | Real-time Comm | Essential for the live chat functionality and real-time notifications. |
| **VNPay** | Payment Gateway | A leading payment provider ensuring secure and localized transaction processing. |
| **OpenPDF** | PDF Generation | Allows for the dynamic creation of contracts and receipts for customers. |
| **Cloudinary** | Media Management | Provides high-performance image hosting and optimization for vehicle galleries. |
| **Docker** | Deployment | Ensures consistent environments across development, testing, and production via containerization. |

---

## 📁 Project Structure

```
E-commerce_Back/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/vin/VinSystem/
│   │   │   ├── 📁 Auth/                # User authentication & role management
│   │   │   │   ├── 📁 Controller/      # Auth and User API endpoints
│   │   │   │   ├── 📁 DTO/             # Data Transfer Objects for auth requests
│   │   │   │   ├── 📁 Entity/          # User, Staff, and Role entities
│   │   │   │   ├── 📁 Repository/      # Database access for auth entities
│   │   │   │   └── 📁 Service/         # Business logic for auth and registration
│   │   │   ├── 📁 Chat/                # Real-time support & AI chat logic
│   │   │   │   ├── 📁 Controller/      # Chat session and message endpoints
│   │   │   │   ├── 📁 DTO/             # Chat message and session mappers
│   │   │   │   ├── 📁 Entity/          # Chat history and session storage
│   │   │   │   └── 📁 Service/         # AI logic and staff routing algorithms
│   │   │   ├── 📁 Car/                 # Vehicle catalog & technical specs
│   │   │   │   ├── 📁 Controller/      # CRUD for cars, colors, and dimensions
│   │   │   │   ├── 📁 DTO/             # Specialized specs for EV and Powertrain
│   │   │   │   ├── 📁 Entity/          # Complex car relationship models
│   │   │   │   └── 📁 Service/         # Management of vehicle specifications
│   │   │   ├── 📁 Deposit/             # Transaction and reservation logic
│   │   │   │   ├── 📁 Controller/      # Deposit processing endpoints
│   │   │   │   ├── 📁 Entity/          # Financial record entities
│   │   │   │   └── 📁 Service/         # Online/Offline deposit handling
│   │   │   ├── 📁 Appointment/         # Test drive & consultation scheduling
│   │   │   │   ├── 📁 Controller/      # Booking and staff assignment
│   │   │   │   └── 📁 Service/         # Appointment lifecycle management
│   │   │   ├── 📁 Payment/             # Payment gateway integrations
│   │   │   │   ├── 📁 Controller/      # VNPay callback and initiation
│   │   │   │   └── 📁 Service/         # Payment verification logic
│   │   │   ├── 📁 Dashboard/           # Admin reporting and analytics
│   │   │   ├── 📁 Notification/        # Email and in-app alert system
│   │   │   ├── 📁 Branch/              # Dealership location management
│   │   │   ├── 📁 Config/              # Global security and WS configurations
│   │   │   └── 📁 Security/            # JWT filters and UserDetails implementation
│   │   └── 📁 resources/
│   │       ├── 📁 fonts/               # Custom fonts for PDF generation
│   │       └── 📄 application-prod-template.properties
│   └── 📁 test/                        # Comprehensive unit and integration tests
├── 📄 pom.xml                          # Maven dependencies and build config
├── 📄 Dockerfile                       # Container definition for the app
├── 📄 docker-compose.yml               # Orchestration for app and database
├── 📄 mvnw                             # Maven wrapper for Unix
├── 📄 mvnw.cmd                         # Maven wrapper for Windows
└── 📁 uploads/                         # Temporary storage for media assets
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed on your local development machine:
- **Java 21 JDK** or higher
- **Maven 3.8+**
- **Docker & Docker Compose** (for containerized setup)
- **MySQL 8.0** (if running locally without Docker)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/E-commerce_Back.git
   cd E-commerce_Back
   ```

2. **Configure Environment**
   Duplicate the `src/main/resources/application-prod-template.properties` and rename it to `application.properties`. Fill in your specific credentials for:
   - Database connection strings
   - Mail server configurations
   - Cloudinary API keys
   - VNPay merchant details

3. **Build the Application**
   Use the Maven wrapper to build the JAR file:
   ```bash
   ./mvnw clean install
   ```

4. **Run with Docker (Recommended)**
   Launch the entire stack (Application + Database) using Docker Compose:
   ```bash
   docker-compose up --build
   ```

5. **Run Locally**
   Alternatively, start the Spring Boot application directly:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 🔧 Usage

### API Documentation
Once the application is running, you can access the interactive Swagger UI to explore and test the available endpoints:
- **URL:** `http://localhost:8080/swagger-ui/index.html`

### Key Workflows

#### 1. Vehicle Exploration
The backend serves structured data for frontends to render complex car specifications.
- `GET /api/cars`: Retrieve all available models.
- `GET /api/car-series/{id}`: Drill down into specific model variations.

#### 2. Chat Support
- **For Anonymous Users:** Start a session via `AnonBotController` to get instant AI help.
- **For Registered Users:** Connect to a WebSocket endpoint to chat with live staff.

#### 3. Making a Deposit
1. Call the `/api/deposits/online` endpoint with the vehicle ID.
2. The system generates a VNPay payment URL.
3. Upon successful payment, the `VNPayController` processes the callback and generates a PDF receipt.

#### 4. Managing Appointments
- Administrators use the `AdminAppointmentController` to assign incoming test drive requests to specific staff members based on their workload and branch location.

---

## 🤝 Contributing

We welcome contributions to improve E_Commerce_Back! Your input helps make this project better for everyone.

### How to Contribute

1. **Fork the repository** - Click the 'Fork' button at the top right of this page.
2. **Create a feature branch** 
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes** - Improve code, documentation, or features.
4. **Test thoroughly** - Ensure all functionality works as expected.
   ```bash
   ./mvnw test
   ```
5. **Commit your changes** - Write clear, descriptive commit messages.
   ```bash
   git commit -m 'Add: Implement AI-driven car recommendation logic'
   ```
6. **Push to your branch**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request** - Submit your changes for review.

### Development Guidelines
- ✅ Follow standard Java/Spring Boot coding conventions.
- 📝 Ensure new DTOs and Controllers are properly documented with Swagger annotations.
- 🧪 Maintain high test coverage for all new Service-level logic.
- 🔄 Ensure database migrations (if any) are compatible with the existing schema.

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for complete details.

### What this means:
- ✅ **Commercial use:** You can use this project commercially.
- ✅ **Modification:** You can modify the code.
- ✅ **Distribution:** You can distribute this software.
- ✅ **Private use:** You can use this project privately.
- ⚠️ **Liability:** The software is provided "as is", without warranty.
- ⚠️ **Trademark:** This license does not grant trademark rights.

---

<p align="center">Made with ❤️ by the VinSystem Team</p>
<p align="center">
  <a href="#">⬆️ Back to Top</a>
</p>