
# Wiki Knowledge System with Spring Boot and Vue3

## Project Overview
This project involves the development of a full-stack knowledge base system using **Spring Boot** for backend development and **Vue3** for the frontend, implementing the **front-end and back-end separation** architecture. The system allows users to store and manage knowledge base articles efficiently, with optimized database queries and caching to enhance performance. The project also incorporates continuous integration using **Git**.

## Features
- **Full-Stack Architecture**: Developed using **Spring Boot** (Java) for the backend and **Vue3** (JavaScript) for the frontend.
- **RESTful APIs**: Designed and implemented APIs for user authentication, content management, and data retrieval.
- **Database Integration**: Integrated **MySQL** for storing and managing knowledge base articles.
- **Caching**: Utilized **Redis** to cache frequently accessed data and reduce database load, optimizing performance.
- **Optimized Database Queries**: Focused on efficient indexing and query processing to ensure fast retrieval of article content and metadata.
- **CI/CD Integration**: Implemented continuous integration and deployment practices using **Git**.

## Technologies Used
- **Backend**: Spring Boot (Java)
- **Frontend**: Vue3 (JavaScript)
- **Database**: MySQL
- **Caching**: Redis
- **Version Control**: Git

## Project Structure
The project is divided into two main parts:

### Backend (Spring Boot)
The backend is built using **Spring Boot** and exposes a set of **RESTful APIs** for:
- User authentication and management
- Article storage and retrieval
- Content management

### Frontend (Vue3)
The frontend is developed using **Vue3** to create a dynamic and user-friendly interface for:
- Displaying knowledge base articles
- Handling user interactions (e.g., viewing, editing, and deleting articles)

## Installation and Setup

### Prerequisites
- **Java 11** or higher for running the Spring Boot backend.
- **Node.js** and **npm** for building and running the Vue3 frontend.
- **Docker** for containerization.

### Backend Setup
1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd backend
   ```
2. Build and run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the Vue3 development server:
   ```bash
   npm run serve
   ```

## Running the Application
Once the application is up and running, navigate to `http://localhost:8080` to access the frontend. The backend API will be available at `http://localhost:8081`.

## Future Improvements
- **Authentication**: Enhance security by implementing OAuth2 or JWT authentication.
- **User Roles**: Implement role-based access control for different types of users (admin, editor, viewer).
- **Advanced Caching**: Use advanced caching strategies to handle large datasets and improve system performance.

## Conclusion
This project provided hands-on experience in building a full-stack application with **Spring Boot** and **Vue3**, integrating multiple technologies like **MySQL** and **Redis**. The application efficiently manages knowledge base articles with optimized database handling and improved performance through caching and query optimization.

