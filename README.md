# 📚 The Howard's Council — Full Stack Web Application

IELTS & English Coaching Center | Meerut, UP

---

## 🏗️ Project Structure

```
howards-council/
├── 📁 backend/                         ← Spring Boot (Java 17)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/howardscouncil/
│       ├── HowardsCouncilApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java     ← JWT + CORS + Spring Security
│       │   ├── JwtUtil.java            ← Token generation & validation
│       │   └── JwtAuthFilter.java      ← Per-request JWT check
│       ├── controller/
│       │   ├── AuthController.java     ← POST /api/auth/login
│       │   ├── GalleryController.java  ← Gallery CRUD + S3 upload
│       │   ├── EnquiryController.java  ← Demo form + Contact form
│       │   └── TestimonialController.java
│       ├── service/
│       │   ├── S3Service.java          ← AWS S3 upload/delete
│       │   ├── GalleryService.java     ← Gallery business logic
│       │   └── EnquiryService.java     ← Enquiry + stats logic
│       ├── model/
│       │   ├── GalleryImage.java       ← MySQL entity
│       │   ├── DemoEnquiry.java
│       │   ├── ContactMessage.java
│       │   └── Testimonial.java
│       ├── repository/                 ← JPA repositories
│       ├── dto/                        ← Request/response DTOs
│       └── exception/
│           └── GlobalExceptionHandler.java
│
├── 📁 frontend/                        ← HTML + CSS + JS (served via Nginx)
│   ├── Dockerfile
│   ├── nginx.conf                      ← Proxy /api/* → backend:8080
│   ├── index.html                      ← Main website (all sections)
│   └── admin.html                      ← Admin panel (gallery, enquiries)
│
├── 📁 mysql/
│   └── init.sql                        ← Auto-creates tables on first run
│
├── docker-compose.yml                  ← Orchestrates all 3 services
├── .env.example                        ← Environment variable template
└── .gitignore
```

---

## ⚡ Quick Start (Docker)

### 1. Prerequisites
- Docker Desktop installed
- AWS account with S3 bucket created

### 2. Setup Environment
```bash
cp .env.example .env
# Edit .env with your real values:
nano .env
```

### 3. AWS S3 Setup
```bash
# Create S3 bucket in ap-south-1 (Mumbai)
# Bucket name: howards-council-media

# Bucket policy — allow public read:
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": "*",
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::howards-council-media/*"
  }]
}

# IAM User policy (for backend):
# AmazonS3FullAccess  (or scoped to your bucket only)
```

### 4. Run Everything
```bash
docker-compose up -d --build
```

### 5. Access
| Service       | URL                          |
|---------------|------------------------------|
| Website       | http://localhost             |
| Admin Panel   | http://localhost/admin.html  |
| API           | http://localhost/api         |
| Spring Boot   | http://localhost:8080        |
| MySQL         | localhost:3306               |

---

## 🔑 Admin Login
```
Username: admin
Password: Howard@Admin2024
```
*(Change in .env before deploying to production)*

---

## 📡 API Endpoints

### Public
| Method | Endpoint               | Description              |
|--------|------------------------|--------------------------|
| POST   | /api/auth/login        | Admin login → JWT token  |
| GET    | /api/gallery           | Get active gallery images|
| GET    | /api/gallery/category/:cat | Filter by category   |
| GET    | /api/testimonials      | Get active testimonials  |
| POST   | /api/enquiry/demo      | Submit demo booking      |
| POST   | /api/enquiry/contact   | Submit contact form      |

### Admin (JWT required)
| Method | Endpoint                          | Description              |
|--------|-----------------------------------|--------------------------|
| POST   | /api/gallery/admin/upload         | Upload image → S3 + MySQL|
| GET    | /api/gallery/admin/all            | All images (incl. hidden)|
| PATCH  | /api/gallery/admin/:id/toggle     | Show/hide image          |
| DELETE | /api/gallery/admin/:id            | Delete from S3 + MySQL   |
| GET    | /api/enquiry/admin/all            | All enquiries            |
| PATCH  | /api/enquiry/admin/:id/status     | Update enquiry status    |
| GET    | /api/enquiry/admin/messages       | All contact messages     |
| PATCH  | /api/enquiry/admin/messages/:id/read | Mark message as read  |
| GET    | /api/enquiry/admin/stats          | Dashboard statistics     |
| POST   | /api/testimonials/admin           | Add testimonial          |
| DELETE | /api/testimonials/admin/:id       | Delete testimonial       |

---

## 🐳 Docker Services

| Container          | Image           | Port  |
|--------------------|-----------------|-------|
| howards_mysql      | mysql:8.0       | 3306  |
| howards_backend    | Custom (JRE 17) | 8080  |
| howards_frontend   | nginx:1.25-alpine | 80  |

---

## 🔧 Local Development (without Docker)

### Backend
```bash
cd backend
# Edit src/main/resources/application.properties with local values
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
# Open frontend/index.html in browser
# OR use Live Server in VS Code
# Change API = '/api' to API = 'http://localhost:8080/api' in index.html
```

---

## 📦 Tech Stack

| Layer     | Technology                |
|-----------|---------------------------|
| Backend   | Java 17 + Spring Boot 3.2 |
| Database  | MySQL 8.0                 |
| ORM       | Spring Data JPA (Hibernate)|
| Auth      | Spring Security + JWT     |
| Storage   | AWS S3 (SDK v2)           |
| Frontend  | HTML5 + CSS3 + Vanilla JS |
| Server    | Nginx 1.25                |
| Container | Docker + Docker Compose   |

---

## 🌐 Production Deployment (AWS EC2)

```bash
# On EC2 (Ubuntu 22.04):
sudo apt update && sudo apt install docker.io docker-compose -y
sudo usermod -aG docker ubuntu

# Clone project
git clone https://github.com/your-repo/howards-council.git
cd howards-council

# Setup env
cp .env.example .env && nano .env

# Run
docker-compose up -d --build

# For HTTPS — add SSL with Let's Encrypt:
sudo apt install certbot
sudo certbot certonly --standalone -d yourdomain.com
# Then update nginx.conf with SSL certificates
```

---

## 📞 Contact
- **Phone:** +91 99977 56675
- **Address:** 618, Shiv Mandir Lane, Begum Bagh, Meerut, UP 250001
- **Hours:** Open Daily · Closes 9 PM
