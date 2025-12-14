# FareComparator

A unified platform to **estimate and compare fares** across multiple road-based transport providers for **short to medium distance travel**, helping users choose the **cheapest or fastest option** through a single interface.

---

## 🚩 Problem Statement

Urban commuters often rely on multiple transport providers such as cabs, bikes, metros, and buses.  
However, fare information is **scattered across individual platforms**, making comparison time-consuming and inefficient.

There is a need for a **single comparison system** that aggregates fare estimates and assists users in making better travel decisions.

---

## 💡 Solution Overview

**FareComparator** aggregates estimated fares from multiple transport providers using a **modular backend architecture** and presents them through a clean, intuitive frontend.

The platform focuses on:
- **Fare estimation & comparison**
- **Provider recommendation**
- **Analytics & insights**

> Note: This project focuses on comparison and estimation, **not booking**.

---

## ✨ Key Features

- 🔗 Unified fare comparison across multiple providers
- 💰 Cheapest option highlighting
- ⏱️ Fastest option identification
- 📏 Distance-based fare estimation
- 🧠 Provider recommendation logic
- 📊 Analytics and fare history tracking
- ⚠️ Graceful handling of empty and failure states
- 🎯 Demo-friendly UI with preset routes

---

## 🏗️ Architecture Overview

```

Frontend (HTML/CSS/JS)
|
v
Spring Boot Backend (REST APIs)
|
v
Provider Abstractions (Cab / Bike / Metro / Bus)
|
v
MongoDB (History & Analytics)

```

### Tech Stack
- **Frontend:** HTML, CSS, JavaScript
- **Backend:** Java, Spring Boot
- **Database:** MongoDB (Atlas)
- **Architecture:** Modular provider-based design

---

## 🔌 API Endpoints

### Compare Fares
```

POST /api/v1/compare

```

**Description:**  
Compares fares between the origin and destination and returns sorted provider results along with recommendations.

---

### Fare History
```

GET /api/v1/history

```

**Description:**  
Returns historical comparison data for analytics.

---

### Analytics
```

GET /api/v1/analytics

````

**Description:**  
Provides insights such as average fares and provider trends.

---

## ⚠️ Disclaimers & Limitations

- All prices displayed are **approximate estimates** based on calculated or observed data.
- 100% real-time accuracy is **not guaranteed**.
- Exact distance calculation requires valid **API keys via environment variables**.
- For demonstration purposes, **mock distances and predefined routes** are used where required.
- Ride availability is **not guaranteed** as provider authentication is currently unavailable.
- Metro options are shown **only for valid station-based routes**.

---

## 🚀 How to Run Locally

### Backend
1. Clone the repository
2. Configure MongoDB URI (Atlas or local)
3. Run the Spring Boot application

```bash
./mvnw spring-boot:run
````

### Frontend

1. Open `compare.html` in a browser
2. Update API base URL if backend is deployed

```js
const API_BASE = '';
```

---

## 🌐 Deployment

* **Frontend:** GitHub Pages
* **Backend:** Render
* **Database:** MongoDB Atlas

> Live demo links
> Frontend : https://nikhilonadrenaline.github.io/FareComparator/
> Backend : https://farecomparator-backend-ypzz.onrender.com

---

Prefer local as the deployed model is facing some issues on the time of submission due to deployment but local works absolutely fine

---
## 🔮 Future Scope

* Official provider API integrations
* User authentication & common provider login
* Real-time distance calculation via external APIs
* Extended analytics and trend visualization
* Support for long-distance transport options

---

## 👨‍💻 Developed For

This project was developed as part of a **hackathon**, focusing on:

* clean architecture
* scalability
* realistic constraints
* strong user experience

---

## 📜 License

This project is intended for **educational and hackathon purposes only**.

```
