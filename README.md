# UniRide 🚍

UniRide is a comprehensive transportation management app developed to provide students and drivers with real-time access to bus schedules, routes, live tracking, and communication. It features an issue reporting system, emergency contact options, live chat, driver reviews with AI-based summaries, dual language support (English and Bengali), and a fully functional admin panel.

## Table of Contents 📑

- [Features](#features)
- [Getting Started](#getting-started)
  - [Firebase Setup](#firebase-setup)
  - [API Keys Configuration](#api-keys-configuration)
- [Firestore Security Rules](#firestore-security-rules)

## Features ⭐

- **Live Location Tracking**: Real-time GPS tracking for currently running buses, allowing students to view live bus locations.
- **Route and Schedule Management**: Easily view bus routes and schedules.
- **Chatbox**: A messaging system for communication between students and drivers.
- **Issue Reporting System**: Allows users to report issues directly from the app.
- **Emergency Helpline**: Quick access to emergency contact numbers.
- **Location Finder**: Helps users find their own location and nearby buses.
- **Driver Reviews and AI Summaries**: Students can rate drivers, with reviews summarized by Gemini AI.
- **Dual Language Support**: English and Bengali language support.
- **Admin Panel**: Full control panel for managing routes, buses, schedules, and handling driver accounts.

## Getting Started 🚀

### Firebase Setup 🔧

1. **Create a Firebase Project**: Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.

2. **Enable Authentication**: In the Firebase Console, go to **Authentication > Sign-in method** and enable:
   - **Email/Password**
   - **Phone Authentication**
   - **Google Authentication**

3. **Set up Firestore Database**:
   - Go to **Firestore Database** in the Firebase Console.
   - Set up the database in **production mode** for added security.
   - Use the Firestore rules provided below to set up access permissions for different user roles.

4. **Enable Storage**: Configure Firebase Storage to manage and store profile pictures and other documents.

5. **Enable Cloud Messaging**: Set up Firebase Cloud Messaging (FCM) for notifications to students and drivers.

### API Keys Configuration 🔑

1. Rename `native-lib.cpp.example` to `native-lib.cpp` and open it.
   - Add your **Google Maps API Key** and **Gemini API Key** in the file for location services and AI-based features.

2. Rename `local.properties.example` to `local.properties` and open it.
   - Add your keystore information for secure app distribution.

## Firestore Security Rules 🔒

To secure data access and permissions, use the following Firestore security rules:

```firestore
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /admin_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if false;
    }
    
    match /driver_list/{driverId} {
      allow read: if (request.auth != null && request.auth.uid == driverId) || isUserAdmin();
      allow write: if (request.auth != null &&
                      request.auth.uid == driverId &&
                      isDriverRegisteringOrUpdatingProfile()) ||
                      isUserAdmin();
    }
    
    match /running_bus_list/{busId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && isDriverAuthorized(request.auth.uid, resource.data);
    }
    
    match /drive_history_list/{busId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && isDriverAuthorized(request.auth.uid, resource.data);
    }
    
    match /student_list/{studentId} {
      allow read, write: if request.auth.uid == studentId;
    }
    
    match /route_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /bus_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /bus_category_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /route_category_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /schedule_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /place_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /announcement_list/{adminId} {
      allow read: if request.auth != null;
      allow write: if isUserAdmin();
    }
    
    match /issue_list/{issueId} {
      allow read: if isUserAdmin();
      allow write: if isAddingNewIssue() || isUserAdmin();
    }
    
    match /driver_review_list/{driverId} {
      allow read, write: if request.auth != null;
    }
    
    match /chat_list/{chatId} {
      allow read, write: if request.auth != null;
    }
    
    function isUserAdmin() {
      return request.auth != null &&
             get(/databases/$(database)/documents/admin_list/$(request.auth.uid)).data != null;
    }
    
    function isDriverAuthorized(driverId, busData) {
      return (!exists(/databases/$(database)/documents/running_bus_list/$(request.resource.id)) ||
             busData.driver == null ||
             busData.driver.id == driverId ||
             busData.status == null ||
             busData.status == 'STOPPED') &&
             get(/databases/$(database)/documents/driver_list/$(driverId)).data.accountStatus == 'APPROVED';
    }
		
    function isDriverRegisteringOrUpdatingProfile() {
      return ((resource == null || resource.data.accountStatus == null) &&
             request.resource.data.accountStatus == 'PENDING') ||
             (resource != null &&
             resource.data.accountStatus != null &&
             request.resource.data.accountStatus == resource.data.accountStatus);
    }
		
    function isAddingNewIssue() {
      return request.auth != null &&
             resource.data.resolved == null &&
             request.resource.data.resolved == false;
    }
  }
}
```

## Contact 📬

Wanna reach out to me? DM me at

Email: mahmudul15-13791@diu.edu.bd
