# ThogaKade CIMS – Layered Architecture (JavaFX · MySQL · YAML)


ThogaKade CIMS (Customer & Inventory Management System) is a desktop application built with JavaFX, MySQL, and a layered architecture design pattern.
The project demonstrates industry-standard practices such as service validation, singleton deign pattern, loose coupling with interfaces, DTO/Entity separation, exception handling and externalized database configuration via YAML.

This project was built to simulate a small-scale POS/Inventory Management system and showcase clean architecture & design patterns for enterprise applications.


## 📌 Features
- **Customer Management** -- add, update, delete, and view customer details.
- **Item Management** -- manage product inventory with CRUD operations.
- **User Login & Session** -- authentication with username/password, user session tracking.
- **UI** -- JavaFX UI with FXML based views.
- **Database Configurator** -- configure DB connection from within the app (store in YAML).
- **Database Seeding** -- populate sample data via `db-data.yml`.
- **Error Handling** -- custom service exceptions and user-friendly alerts.
- **Architecture** -- strict **layered architecture** with DTOs, entities, repositories, and services.


## 🏗️ Project Architecture

### Layered Design
- **Controller Layer** : JavaFX controllers — handle UI input, display results, catch exceptions.  
- **Service Layer** : Business logic, validation, entity ↔ DTO conversion. Wraps low-level runtime exceptions in custom service exceptions.  
- **Repository Layer** : JDBC-based data access. Only this layer touches the database.  
- **Model Layer** :
  - **DTOs** — lightweight objects between Controller ↔ Service.
  - **Entities** — DB row representations between Service ↔ Repository.
- **Utilities** :
  - `DbConnection` (singleton connection manager)  
  - `WindowManagerUtil` (scene switching)  
  - `SessionUserUtil` (logged-in user tracking)  
  - `AlertPopupUtil` (dialog alerts)  
  - `configDb` utilities (load/save config, seed data)


### Project Structure

    edu.icet.cims
    |
    ├── controller/     # JavaFX controllers
    │ ├── configDb/     # DB configuration controllers
    │ └── util/         # Misc UI controllers
    |
    ├── db/             # DB connection singleton
    |
    ├── model/
    │ ├── dto/          # DTOs (Controller ↔ Service)
    │ └── entity/       # Entities (Service ↔ Repository)
    |
    ├── repository/     # Repository (DAO) classes
    |
    ├── service/
    │ ├── exception/    # Custom service exceptions
    │ ├── impl/         # Service implementations
    │ └── interfaces    # Service interfaces
    |
    ├── util/
    │ ├── configDb/     # Config & seed utilities (YAML)
    │ └── validator/    # Alert, session, window helpers
    |
    └── resources/
      ├── view/           # JavaFX FXML views
      ├── db-config.yml   # DB connection config
      └── db-data.yml     # Seed data

--- 

## 🔑 Login & Session Flow

1. **Controller** builds `UserCredentialsDto` from input.  
2. **Service**:
   - validates format (no whitespace, length ≥ 3, alphanumeric username).  
   - calls repository to check credentials.  
   - returns `ActiveUserDto` if valid, Which is used to diplay Active-user and Name.  
   - throws `UserCredentialsException` if invalid or DB error.  
3. **Repository** executes parameterized `SELECT ... WHERE username=? AND password=?`.  
4. **SessionUserUtil** stores the active user in memory.  
5. **WindowManagerUtil** switches the scene to `Dashboard.fxml`.

  <p align="left">
    <img src="https://github.com/user-attachments/assets/4cd76818-c96c-47c6-a7d7-b8d94cef817e" width="420">
  </p>
  <p align="left">
    <img src="https://github.com/user-attachments/assets/64c2d568-5762-4b75-afd0-39fc12cb719d" width="600">
  </p>

---

## 🗄️ Database Connection (Singleton)

A key part of this project is the **`DbConnection` class**, which follows the **Singleton Design Pattern**.

### Why Singleton?
- We only ever need **one active database connection** at a time.
- Prevents accidentally opening multiple connections (which would waste resources).
- Provides a **single global access point** to the DB connection.

### How It Works
```java
// Access a single instance
DbConnection connection = DbConnection.getInstance();

// Get the underlying JDBC connection
Connection con = connection.getConnection();
```
Dynamic connection: the constructor does not include a fixed database name in the URL.

- Example: jdbc:mysql://localhost:3306/ (notice no /dbname).
- This allows running queries like CREATE DATABASE ... before selecting the DB.

Switching database: once a DB is created/selected, you can explicitly call:
```
DbConnection.useDb("thogakade");
// which executes a SQL USE <dbName>
```

---

## ⚙️ Database Config
Project includes an **interactive Database Configurator** and a **Database Creation/Seeding** feature.  
The goal is to let end users set up the database connection **without editing any code**. 

- On startup, the app first **checks if the target database exists**.  
  - ✅ If database **exists** → it proceeds to check for the required tables (like `user_credentials`) and proceed to **login**.  
  - ❌ If database or required tables are **missing** → the user is prompted to configure or create them.  

### 🛠️ User Options  
- **Configure Database**  
  - If DB already exists but under a *different name*, user can simply enter the new **host / port / username / password / dbName** in the configurator.  
  - The app will then generate a fresh `db-config.yml` file with these details — so the connection works next time without re-entering.
    
- **Create Database**  
  - If no DB is found, user can choose to create one entirely from scratch.  
  - The app runs the necessary SQL scripts to **create the schema, tables, and even seed sample data** (from `db-data.yml`).  

<div align="left">
  <video src="https://github.com/user-attachments/assets/5cbe6f2c-3006-4e75-8059-bc0c9334db39">
  </video>
</div>

### `db-config.yml`  
After configuration, the access details are saved here — meaning no need to reconfigure every run.  
This avoids **hard-coded credentials** inside the source code: 
```yaml
database:
  pswd: '1234'
  port: '3306'
  dbName: thogakade_cims
  parameter: ''
  host: localhost
  user: root
```

---
## 📦 Dependencies (pom.xml)

```xml
  <dependencies>
    JavaFX 19 — UI toolkit
    MySQL Connector/J 8/9 — JDBC driver
    Lombok — boilerplate reduction      
    SnakeYAML 2.4 — YAML parsing
  </dependencies>
```
