# 🧳 Smart Luggage Tracking & AI-Powered Transportation System

A **100% software-based intelligent luggage tracking system** that simulates real-time shipment movement and uses **AI/ML models** to predict **ETA, detect route anomalies, and estimate transportation costs** — all without any IoT hardware.

This project demonstrates **backend engineering, microservices, and ML integration** using only **free cloud services**.

---

## 🚀 Key Features

- Software-only **real-time GPS tracking simulation**
- **AI-powered ETA prediction**
- **Route anomaly detection** (stops, deviations, abnormal speed)
- **ML-based transportation cost prediction**
- Secure **JWT-based authentication**
- Role-based access (**Admin & Customer**)
- Live map visualization with alerts
- Automated log archiving and compression

---

## 🏗️ System Architecture

- **Spring Boot Backend (Main API)**  
  Handles authentication, shipment management, billing, and communication with ML services.

- **Python Flask ML Microservice**  
  Provides AI predictions for ETA, anomalies, and transportation cost.

- **GPS Simulation Service**  
  Generates live route coordinates and sends tracking updates to backend APIs.

---

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- REST APIs
- JWT Authentication

### AI / ML Microservice
- Python
- Flask
- Scikit-learn

### Machine Learning Models
- ETA Prediction
- Route Anomaly Detection
- Transportation Cost Prediction (trained on synthetic data)

### Database
- MySQL 

### Frontend 
- React.js
- Leaflet.js
- OpenStreetMaps

---

## 📦 System Modules

### 1️⃣ Authentication
- User registration & login
- JWT token-based security
- Role-based access (Admin / Customer)

### 2️⃣ Luggage Management
**Customer:**
- Create shipments
- Upload luggage images
- Generate tracking ID
- View AI predictions

**Admin:**
- Monitor all shipments
- View ETA, anomalies, and cost predictions
- Download tracking logs

### 3️⃣ Software-Only GPS Tracking Simulation
- Generates GPS points every 0.5 seconds
- Uses real-world route coordinates
- Completes journey in 15–20 seconds
- Stores logs in MySQL and archives to S3

### 4️⃣ AI/ML Features
- **ETA Prediction**
- **Route Anomaly Detection**
- **Transportation Cost Prediction**

Spring Boot calls the ML service before confirming shipments.

### 5️⃣ Pricing & Billing
- Base fare
- Distance & weight charges
- Category multiplier
- AI-predicted cost
- Final invoice generation

### 6️⃣ Map Visualization
- Live marker movement
- Route polyline
- ETA & alerts
- Final bill display

### 7️⃣ Admin Dashboard
- Shipment analytics
- Active & completed luggage
- Anomaly alerts
- Live tracking logs

---
