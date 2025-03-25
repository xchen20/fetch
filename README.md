# 🧾 Fetch Receipt Processor

A Micronaut-based backend service that processes retail receipts and awards points based on a set of rules.

---

## 🚀 Features

- REST API to submit and process receipts
- Calculates points using rules like:
    - Retailer name alphanumerics
    - Total rounding and item count
    - Purchase time/date logic
    - Description-based bonuses
- In-memory storage of processed receipts
- Lightweight and fast with Micronaut
- Docker-ready for easy deployment

---

## 📦 Tech Stack

- Java 17
- Micronaut Framework
- Gradle
- Docker

## 📦 Clone the Repository

```bash
git clone https://github.com/xchen20/fetch
cd fetch
```

---

## 🐳 Docker Instructions

- Build Image
  ```
  docker build -t fetch-receipt-app .
  ```
- Run Container
  ```
  docker run -p 8080:8080 fetch-receipt-app
  ```

## ⚙️ Run the Application

- Using Gradle
  ```
  ./gradlew run 
  ```

## 📮 API Usage

- 📤 POST /receipts/process
  ```
  curl -X POST http://localhost:8080/receipts/process \
  -H "Content-Type: application/json" \
  -d '{
    "retailer": "Target",
    "purchaseDate": "2022-01-01",
    "purchaseTime": "14:33",
    "total": "35.00",
    "items": [
      { "shortDescription": "Mountain Dew 12PK", "price": "6.49" },
      { "shortDescription": "Emils Cheese Pizza", "price": "12.25" }
    ]
  }'
  ```
  response
  ```
  {
  "id": "550e8400-e29b-41d4-a716-446655440000"
  }
  ```

- 📥 GET /receipts/{id}/points
  ```
  curl http://localhost:8080/receipts/550e8400-e29b-41d4-a716-446655440000/points
  ```
  response
  ```
  {
  "points": 109
  }
  ```
