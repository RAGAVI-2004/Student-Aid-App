**Student Aid – Android Student Helping System**

**Project Overview**

Student Aid is a comprehensive Android-based mobile application developed to simplify and digitize essential campus services for students and teachers. The application integrates multiple academic and utility modules into a unified platform, enhancing campus communication, collaboration, and accessibility. The system includes study material sharing, roommate matching, lost and found reporting with GPS tracking, and a real-time student–teacher guidance chat with voice-enabled functionality. The application is designed using Java and SQLite, ensuring efficient local data management and smooth user interaction.

**Objectives:**
- To create a centralized digital platform for campus utilities
- To enhance communication between students and teachers
- To provide structured study material sharing
- To implement secure local data management
- To integrate device features such as GPS, camera, and voice services
  
**Key Features:**

1. Role-Based Authentication:
   - Separate login and dashboard for Students and Teachers
   - Secure credential validation using SQLite
   - Session handling using SharedPreferences

2. Study Material Management:
   - Upload and download academic resources
   - Organized digital repository

3. Roommate Matching System:
   - Preference-based roommate search
   - Gender, room type, number of members filtering

4. Campus Lost & Found Portal:
   - Capture item images via camera
   - GPS location tagging using Google Maps API
   - View and filter reports

5. Student–Teacher Guidance Chat:
   - Real-time text messaging
   - Speech-to-Text integration
   - Text-to-Speech playback

---

## Screenshots

### 🔹 Role Selection
[<p align="center">
  <img src="appScreenshots/RoleSelection.png" width="350"/>
</p>](https://github.com/RAGAVI-2004/Student-Aid-App/blob/main/appScreenshots/RoleSelection.jpeg)

### 🔹 Login Page
<p align="center">
  <img src="appScreenshots/Loginpage.png" width="350"/>
</p>

### 🔹 Signup Page
<p align="center">
  <img src="appScreenshots/Signuppage.png" width="350"/>
</p>

### 🔹 Student Dashboard
<p align="center">
  <img src="appScreenshots/StudentDashboard.png" width="350"/>
</p>

### 🔹 Teacher Dashboard
<p align="center">
  <img src="appScreenshots/TeachersDashboard.png" width="350"/>
</p>

### 🔹 Study Material Upload
<p align="center">
  <img src="appScreenshots/StudyMaterialUpload.png" width="350"/>
</p>

### 🔹 Roommate Finder
<p align="center">
  <img src="appScreenshots/RoomateFinder.png" width="350"/>
</p>

---

## 🔍 Lost & Found Module

### 🔹 Lost & Found Dashboard
<p align="center">
  <img src="appScreenshots/LostAndFound/lost_found_dashboard.jpeg" width="350"/>
</p>

### 🔹 Reporting Lost Item
<p align="center">
  <img src="appScreenshots/LostAndFound/reporting_lost_item.jpeg" width="350"/>
</p>

### 🔹 View Lost & Found Items
<p align="center">
  <img src="appScreenshots/LostAndFound/view_lost_found_items.jpeg" width="350"/>
</p>

### 🔹 Guidance Chat
<p align="center">
  <img src=["appScreenshots/GuidanceChat.png](https://github.com/RAGAVI-2004/Student-Aid-App/blob/main/appScreenshots/Guidance%20Chat.jpeg)" width="350"/>
</p>

**Technology Stack**

- Programming Language: Java

- Development Environment: Android Studio

- Database: SQLite

- User Interface: XML

- APIs Used: Google Maps API, FusedLocationProviderClient, SpeechRecognizer API, TextToSpeech API

**System Requirements:**

Software:
- Android 8.0 or above

Hardware:
- Android smartphone
- Minimum 2GB RAM
- GPS & Camera support
- Internet connectivity
- 
**Project Architecture**
  
The application follows a modular Activity-based architecture. The DatabaseHelper class manages CRUD operations. Runtime permissions are implemented for camera and location access. The project follows a Gradle-based modular structure.

**Security Considerations**

- Local data storage using SQLite
- Runtime permission validation
- Role-based authentication
- Future enhancement: password hashing implementation
  
**Developer Details**
  
Name: Ragavi A
Degree: M.Tech Integrated Software Engineering
University: VIT University, Vellore
-->Developed as a part of academic course.
