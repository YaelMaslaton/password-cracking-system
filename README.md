# 🔐 Distributed Password Cracking System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)

**⚡ High-Performance Distributed MD5 Password Cracking System ⚡**

*Master-Minion architecture for cracking MD5 hashes of Israeli phone numbers with lightning speed!*

</div>

---

## 🚀 What Makes This Special?

This is a **complete distributed system** that demonstrates enterprise-grade architecture:

- 🧠 **Master Service** - Orchestrates tasks, manages persistence, handles recovery
- ⚡ **Minion Services** - Perform actual MD5 cracking in parallel
- 🎯 **Intelligent Distribution** - Splits phone ranges across multiple workers
- 🛡️ **Fault Tolerance** - Auto-recovery from crashes and timeouts
- 📊 **PostgreSQL Persistence** - Tracks everything with ACID compliance
- 🎨 **Beautiful APIs** - Swagger UI + Postman collections
- 🧪 **Fully Tested** - Unit & integration tests

---

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   📁 Upload     │    │  🧠 Master      │    │  ⚡ Minion 1    │
│   MD5 Hashes    │───▶│   Service       │───▶│  (Range 1-33M)  │
└─────────────────┘    │                 │    └─────────────────┘
                       │  • Task Split   │    ┌─────────────────┐
┌─────────────────┐    │  • Orchestrate  │───▶│  ⚡ Minion 2    │
│  📊 PostgreSQL  │◀───│  • Monitor      │    │  (Range 33-66M) │
│   Database      │    │  • Recover      │    └─────────────────┘
└─────────────────┘    └─────────────────┘    ┌─────────────────┐
                                              │  ⚡ Minion 3    │
                                              │  (Range 66-99M) │
                                              └─────────────────┘
```

---

## 🗄️ Database Schema

```
┌─────────────────────────────────────┐
│             BATCHES                 │
├─────────────────────────────────────┤
│ 🔑 batch_id (UUID) [PK]            │
│ 📅 created_at (TIMESTAMP)          │
│ 📊 status (VARCHAR)                │
│ 🔢 total_tasks (INTEGER)           │
│ ✅ completed_tasks (INTEGER)        │
└─────────────────────────────────────┘
                 │
                 │ 1:N
                 ▼
┌─────────────────────────────────────┐
│              TASKS                  │
├─────────────────────────────────────┤
│ 🔑 task_id (UUID) [PK]             │
│ 🔗 batch_id (UUID) [FK]            │
│ 🔐 hash_value (VARCHAR)            │
│ 📊 status (VARCHAR)                │
│ 🔓 found_password (VARCHAR)        │
│ 🔢 total_sub_tasks (INTEGER)       │
│ ✅ completed_sub_tasks (INTEGER)    │
└─────────────────────────────────────┘
                 │
                 │ 1:N
                 ▼
┌─────────────────────────────────────┐
│            SUB_TASKS                │
├─────────────────────────────────────┤
│ 🔑 sub_task_id (UUID) [PK]         │
│ 🔗 task_id (UUID) [FK]             │
│ 📱 range_start (BIGINT)            │
│ 📱 range_end (BIGINT)              │
│ 📊 status (VARCHAR)                │
│ 🔓 result_password (VARCHAR)       │
│ ⏰ started_at (TIMESTAMP)          │
└─────────────────────────────────────┘
```

---

## 🎯 Core Features

### 🔥 Smart Task Distribution
- **Range Splitting**: Automatically divides 0500000000-0599999999 phone ranges
- **Load Balancing**: Evenly distributes work across available minions
- **Dynamic Scaling**: Add/remove minions without downtime

### 🛡️ Fault Tolerance & Recovery
- **Timeout Detection**: Identifies stuck tasks after 30 minutes
- **Auto Reassignment**: Redistributes failed tasks to healthy minions
- **Retry Logic**: 3 attempts with exponential backoff
- **Fast Recovery**: 2-minute quick retry for temporary failures

### 📈 Monitoring & Observability
- **Structured Logging**: MDC-based tracing with batchId/taskId/subTaskId
- **Status Tracking**: Real-time task and batch progress monitoring
- **Performance Metrics**: Built-in timing and success rate tracking

---

## 🛠️ Tech Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Master** | Spring Boot 3.2.0 | REST API & Orchestration |
| **Minions** | Spring Boot 3.2.0 | MD5 Cracking Workers |
| **Database** | PostgreSQL 15 | Persistent Task Storage |
| **Documentation** | Swagger/OpenAPI 3 | Interactive API Docs |
| **Testing** | JUnit 5 + Mockito | Comprehensive Test Coverage |
| **Containerization** | Docker + Compose | Easy Deployment |

---

## 🚀 Quick Start

### Prerequisites
```bash
☑️ Java 17+
☑️ Maven 3.8+
☑️ Docker & Docker Compose
```

### 1️⃣ Clone & Build
```bash
git clone <repository-url>
cd distributed-password-cracking
```

### 2️⃣ Launch Everything! 🚀
```bash
docker-compose up --build
```

**🎉 That's it! The entire system is running:**
- **Master Service**: http://localhost:8080
- **Minion 1**: http://localhost:8081
- **Minion 2**: http://localhost:8082
- **Minion 3**: http://localhost:8083
- **PostgreSQL**: localhost:5432

---

## 🎮 How to Use - Complete Workflow

### Step 1: Prepare Your Hash File
Create `hashes.txt` with MD5 hashes (one per line):
```
5d41402abc4b2a76b9719d911017c592
098f6bcd4621d373cade4e832627b4f6
aab3238922bcc25a6f606eb525ffdc56
```

### Step 2: Submit for Cracking
```bash
curl -X POST http://localhost:8080/submit \
  -F "file=@hashes.txt"

# Response:
{
  "batchId": "123e4567-e89b-12d3-a456-426614174000",
  "taskIds": ["task-1", "task-2", "task-3"],
  "tasksCount": 3,
  "status": "SUBMITTED"
}
```

### Step 3: Monitor Progress
```bash
# Check specific task
curl http://localhost:8080/task/task-1

# Response examples:
{
  "taskId": "task-1",
  "hash": "5d41402abc4b2a76b9719d911017c592",
  "status": "FOUND",
  "password": "0501234567"
}
```

### Step 4: Download Results
```bash
curl -O http://localhost:8080/batch/123e4567-e89b-12d3-a456-426614174000/results

# Downloads: results_123e4567.txt
# Content:
# 5d41402abc4b2a76b9719d911017c592:0501234567
# 098f6bcd4621d373cade4e832627b4f6:NOT_FOUND
# aab3238922bcc25a6f606eb525ffdc56:FAILED_SERVER_CRASH
```

---

## 🔄 What Happens Behind the Scenes

```
1. 📁 Upload hashes.txt → Master Service
   ↓
2. 🧠 Master creates batch + individual tasks
   ↓  
3. 📊 Each task split into sub-tasks (per minion)
   ↓
4. ⚡ Sub-tasks distributed to Minion servers:
   • Minion 1: Range 0500000000-0533333333
   • Minion 2: Range 0533333334-0566666666  
   • Minion 3: Range 0566666667-0599999999
   ↓
5. 🔍 Minions crack MD5 hashes in parallel
   ↓
6. 📤 Results sent back to Master via REST API
   ↓
7. ✅ Master updates database & aggregates results
   ↓
8. 📄 Download complete results file
```

---

## 📚 API Documentation

### 🌟 Interactive Swagger UI
- **Master Service**: http://localhost:8080/swagger-ui.html
- **Minion Services**: http://localhost:808[1-3]/swagger-ui.html

### 🚀 Postman Collection
Import `Master-Service.postman_collection.json` for ready-to-use API calls with:
- ✅ Auto-variable extraction (batchId, taskId)
- 📁 File upload configuration
- 🔄 Complete workflow from submit to results

---

## 🧪 Testing

### Run All Tests
```bash
# Master Service
cd master/
mvn test

# Minion Service  
cd minion/
mvn test
```

### Test Coverage
- ✅ **Unit Tests**: Service layer with mocked dependencies
- ✅ **Integration Tests**: Full Spring context with H2 database
- ✅ **API Tests**: REST endpoint validation
- ✅ **Fault Tolerance Tests**: Timeout and recovery scenarios

---

## 🏗️ Project Structure

```
distributed-password-cracking/
├── master/                    # Master Service
│   ├── src/main/java/com/example/master/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/          # Business logic
│   │   ├── repository/       # Data access
│   │   ├── entity/           # JPA entities
│   │   └── dto/              # Data transfer objects
│   ├── src/test/             # Unit & integration tests
│   ├── pom.xml
│   └── Dockerfile
├── minion/                   # Minion Service
│   ├── src/main/java/com/example/minion/
│   │   ├── controller/       # REST API endpoints
│   │   ├── service/          # MD5 cracking logic
│   │   └── dto/              # Data transfer objects
│   ├── src/test/             # Unit tests
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml        # Complete system deployment
├── Master-Service.postman_collection.json
└── README.md                 # This file
```

---

## 🔧 Configuration

### Master Service Configuration
```yaml
# master/src/main/resources/application.yaml
minions:
  servers:
  - id: 1
    baseUrl: http://minion-1:8080
  - id: 2
    baseUrl: http://minion-2:8080
  - id: 3
    baseUrl: http://minion-3:8080

spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/cracking
    username: cracking_user
    password: cracking_pass
```

### Minion Service Configuration
```yaml
# minion/src/main/resources/application.yaml
master:
  callback-url: http://master-service:8080/result

cracking:
  phone-prefix: "05"
  batch-size: 10000
```

---

## 🐳 Docker Deployment

### Full System
```bash
docker-compose up --build
```

### Individual Services
```bash
# Master only
docker-compose up postgres master-service

# Add minions
docker-compose up minion-1 minion-2 minion-3
```

### Scaling Minions
```bash
docker-compose up --scale minion-service=5
```

---

## 🛡️ Fault Tolerance Features

### Crash Recovery
- **Master crashes**: PostgreSQL persists all state, restart and continue
- **Minion crashes**: Timeout detection + automatic task reassignment
- **Database crashes**: Connection pooling + retry logic

### Network Issues
- **Connection timeouts**: Exponential backoff retry (3 attempts)
- **Partial failures**: Individual task tracking prevents data loss
- **Split-brain scenarios**: UUID-based idempotency

### Performance Monitoring
```bash
# Check system health
curl http://localhost:8080/actuator/health

# Monitor task progress
curl http://localhost:8080/actuator/metrics
```

---

## 🤝 Contributing

1. 🍴 Fork the repository
2. 🌿 Create feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to branch (`git push origin feature/amazing-feature`)
5. 🎉 Open Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**🔥 Built with ❤️ for distributed systems learning**

*Ready to crack some hashes? Let's go! 🚀*

[![Made with Spring Boot](https://img.shields.io/badge/Made%20with-Spring%20Boot-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![Powered by Java](https://img.shields.io/badge/Powered%20by-Java-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Distributed System](https://img.shields.io/badge/Architecture-Distributed-blue?style=for-the-badge)](https://en.wikipedia.org/wiki/Distributed_computing)

</div>