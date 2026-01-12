# 📔 Daily ME 

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)

> **✨ LIVE APPLICATION: [https://daily-me-cvoa.onrender.com](https://daily-me-cvoa.onrender.com)**

Daily ME is your personal digital sanctuary—a secure, mood-tracking diary application designed to help you reflect on your days, cherish memories, and track your emotional well-being.

## 🚀 Features

- **🔐 Secure Authentication**: Private user accounts with secure login and registration.
- **📝 Daily Journaling**: proper space to write your thoughts and experiences.
- **🙂 Mood Tracking**: Log your daily mood and track emotional trends over time.
- **📸 Memory Capture**: Attach images to your diary entries to keep your memories vivid.
- **📅 Interactive Calendar**: View your monthly journey at a glance with an intuitive calendar view.
- **📊 Dashboard**: A central hub to manage your latest entries and quick actions.

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.1
- **Frontend**: Thymeleaf (Server-side rendering), HTML5, CSS3
- **Database**: H2 (Dev), PostgreSQL (Prod/Supported)
- **Build Tool**: Gradle

## 🏃‍♂️ Getting Started

### Prerequisites
- Java 17 SDK or higher
- Gradle (optional, wrapper included)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/daily-me.git
   cd daily-me
   ```

2. **Configuration**
   The application uses an H2 database by default for local development. No extra configuration is needed to start.
   
   To use a custom database, set environment variables:
   ```properties
   DATABASE_URL=jdbc:postgresql://localhost:5432/dailyyou
   DATABASE_USERNAME=your_user
   DATABASE_PASSWORD=your_password
   ```

3. **Run the Application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the App**
   Open your browser and navigate to:
   `http://localhost:8080`

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---
Made with ❤️ by Prashant Shukla
