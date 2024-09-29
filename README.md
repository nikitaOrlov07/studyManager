# 📚 Multi-Service Application

**A powerful, feature-rich multi-service application** designed to handle various user and course-related operations. It consists of **7 microservices** working seamlessly together to deliver robust functionality, including real-time chat, authentication, and course management.

## ⚙️ Microservice Architecture

### 1. **Main Service (View Service)**
   - **Role**: Central hub for gathering and displaying information to clients.
   - **Features**:
     - Stores **JWT tokens**, along with user device and IP information.
     - Implements real-time **WebSocket-based chat** for user interaction.
     - **Database**: Stores JWT tokens and user details.

### 2. **Authentication Service**
   - **Role**: User authentication and JWT generation.
   - **Features**:
     - Authenticates users via **HTTP requests** to the User Service.
     - Generates **JWT tokens** and forwards them to Main Service for storage.
     - Removes tokens from the database when users log out.

### 3. **Chat Service**
   - **Role**: Manage chats for users and courses.
   - **Features**:
     - Search, create, and delete user chats.
     - Manage **course-based chats** and store/delete messages.
     - Secured by JWT tokens passed from the Main Service.

### 4. **Course Service**
   - **Role**: Manage course information and assignments.
   - **Features**:
     - Stores and retrieves data related to courses and homework assignments.

### 5. **User Service**
   - **Role**: Manage user data.
   - **Features**:
     - Handles user registration, updates, and profile management.

### 6. **Notification Service**
   - **Role**: Send notifications related to user activity.
   - **Features**:
     - **Asynchronous communication** with User Service using **Apache Kafka**.
     - Sends notifications when users register or login into  their profiles.

### 7. **Discovery Service**
   - **Role**: Dynamic discovery and interaction between microservices.
   - **Features**:
     - Enables easy scaling and registration of new services without manual configuration.

---

## 🔑 Application Features

### 📖 **For Unauthenticated Users**
- Browse and search for courses by **ID** or **name**.
- View detailed course pages, including **files** and additional information.

### 🔓 **For Authenticated Users**
- **Join Courses**: Public courses or private courses with a password.
- **Real-Time Chat**: Use chat to communicate with other users and course participants.
- **Create Courses & Assignments**: Set up courses, create homework assignments with file attachments, and set deadlines.
- **Submit Homework**: Submit homework for review.
- **Manage Homework**:
  - Teachers: View, filter, and manage homework by status (`CHECKED`, `UNCHECKED`), course name, or assignment title.
  - Students: Track homework submissions and view status (`COMPLETED`, `GRADED`, `REJECTED`, `LATE`).
- **Course Chat**: Automatically generated when a new course is created. Users can clear, send, and delete messages.

---

## 🛠️ Technologies Used
- **WebSocket**: Real-time messaging and chat.
- **WebClient**: HTTP communication between services.
- **Grafana**: Logs, metrics, and tracing for monitoring.
- **Apache Kafka**: Asynchronous messaging.
- **JWT Tokens** and **Sessions**: Security measures for authentication and user session management.
- **Discovery Service**: For microservice registration and dynamic discovery.
- **Java, CSS, JavaScript, HTML**: Core technologies for application development.

---

## 🚀 How to Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repo/multiservice-app.git


2. **Build and run each service**:
   ```bash
   mvn clean install

Access the application:
 Main service at: http://localhost:8080
 Authentication service: http://localhost:8081
 Chat service: http://localhost:8082
 Course service: http://localhost:8083
 User service: http://localhost:8084
 Notification service: http://localhost:8085
 Monitoring

Access Grafana at http://localhost:3000 for logs, metrics, and tracing.

## 🐳 Docker Containers Used:

- **Grafana Tempo** (tracing)
- **Grafana Loki** (logging)
- **Prometheus** (metrics)
- **Grafana** (dashboard for logs, metrics, and tracing)
- **Zookeeper** (Kafka cluster management)
- **Kafka Broker** (message broker)
- **Redis** (centralized session storage)

## Screenshots

### Main Page 
![image](https://github.com/user-attachments/assets/1fb2c15e-d552-4b3d-9e77-16c03993d4dd)

### Detail page for course author
![image](https://github.com/user-attachments/assets/82a5207a-f241-4da1-849d-a9bf53a1d374)

### Chat Page
![image](https://github.com/user-attachments/assets/cbb55b1c-507e-445e-b148-01fa82dd2f81)

### Login and Registry pages
![image](https://github.com/user-attachments/assets/ef6a400b-cd01-484e-b733-bb97fc7fda18)

### Homeworks Page (for students)
![image](https://github.com/user-attachments/assets/2e0ca25a-2ce6-4ba1-a54e-3fde1f27b196)

### Homeworks Page (for teachers)
![image](https://github.com/user-attachments/assets/df4d31b1-315b-4cd3-86a5-5d61376ebf74)

### Homework detail page (for student)
![image](https://github.com/user-attachments/assets/514a296d-53d6-4862-9580-effe247bb266)

### Homework detail page (for teacher)
![image](https://github.com/user-attachments/assets/7f448cbf-3ce7-41ad-937a-133d77883be4)

### Create page for course 
![image](https://github.com/user-attachments/assets/820e9a3c-0b9f-444a-a402-ab5b36ac9883)

### Create page for homeworks
![image](https://github.com/user-attachments/assets/cf5288da-6309-4783-9d15-3e125bd7a291)



