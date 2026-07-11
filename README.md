# 🌿 MindTrail

> A Spring Boot-based habit tracking and mood analysis application that enables users to build healthy habits,
> track daily moods, maintain streaks, and gain insights into their progress.

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![MongoDB](https://img.shields.io/badge/Database-MongoDB-success)
![License](https://img.shields.io/badge/License-MIT-blue)

---

# 📖 Table of Contents

- Overview
- Features
- Tech Stack
- Architecture
- Prerequisites
- Installation
- Configuration
- Running the Application
- API Endpoints
- Project Structure
- Future Enhancements
- Author

---

# 📌 Overview

MindTrail is a backend application developed using **Java**, **Spring Boot**, **Spring Security**, and **MongoDB**.

The application helps users maintain healthy habits by allowing them to:

- Track daily habits
- Record daily moods
- Maintain consistency through streak tracking
- View personalized dashboard analytics
- Receive automated email reminders and milestone notifications

The project follows a layered architecture and RESTful API design.

---

# ✨ Features

## 🔐 Authentication

- User Registration
- Secure Login
- Password Encryption using BCrypt
- Spring Security Authentication
- Role-Based Authorization (User/Admin)

## ✅ Habit Tracking

- Create Habits
- Daily Habit Logging
- Mark Habits as Completed
- Habit Consistency Tracking

## 😊 Mood Tracking

- Record Daily Mood
- One Mood Entry Per Day
- Mood History

## 🔥 Streak Management

- Automatic Daily Streak Calculation
- Milestone Detection
- Streak Email Notifications

## 📊 Dashboard

- Habit and mood correlation Statistics

## 📧 Email Service

- Daily Reminder Emails
- Streak Achievement Notifications
- Monthly Analytics report
- Consolation and Appreciative mails


## 💾 Database

- MongoDB Integration
- Spring Data MongoDB
- Repository Pattern

---

# 🛠 Tech Stack

| Category | Technology |
|-----------|------------|
| Language | Java 23 |
| Framework | Spring Boot 3.x |
| Security | Spring Security |
| Database | MongoDB |
| Build Tool | Maven |
| API Testing | Postman |
| Version Control | Git |
| Repository | GitHub |

---

# 🏗 Architecture

```
Client
   │
REST APIs
   │
Controllers
   │
Services
   │
Repositories
   │
MongoDB
```

---

# 📋 Prerequisites

Install the following before running the project:

- Java JDK 23
- Maven
- MongoDB Community Server or MongoDB Atlas
- Git
- IntelliJ IDEA / VS Code

Verify installation:

```bash
java -version
mvn -version
```

---

# ⚙ Installation

Clone the repository

```bash
git clone https://github.com/samkum002/MindTrail.git
```

Move into the project

```bash
cd MindTrail
```

Install dependencies

```bash
mvn clean install
```

---

# ⚙ Configuration

Open

```
src/main/resources/application.properties
```

Configure MongoDB

```properties
spring.data.mongodb.uri=YOUR_MONGODB_URI
spring.data.mongodb.database=MindTrail
```

If email notifications are enabled, configure:

```properties
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
```

---

# ▶ Running the Project

Using Maven

```bash
mvn spring-boot:run
```

or simply run

```
MindTrailApplication.java
```

from IntelliJ IDEA or VS Code.

---

# 📡 API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /public/register | Register User |
| POST | /user/login | Login |
| POST | /user/habit | Add Habit |
| GET | /habit/show | Get Habits |
| POST | /user/mood | Record Mood |
| GET | /user/show/mood | Get Mood History |
| GET | /habit/dashboard | Dashboard Analytics |

> Some Extra Endpoints might be visible due to previous implementation but hold no value in the current features.

---

# 📂 Project Structure

```
src
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── security
├── scheduler
├── util
└── resources
```

---

# 🚀 Future Enhancements

- AI-Based Habit Recommendations
- Weekly & Monthly Reports
- Responsive Frontend
- Mobile Application
- Better Dashboard Visualizations
- Achievement Badges
- Dark Mode
- Export Reports as PDF

---

# 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push your branch
5. Create a Pull Request

---

# 👨‍💻 Author

**Sameer**

GitHub: https://github.com/samkum002

---

# ⭐ If you found this project useful, consider giving it a star.
