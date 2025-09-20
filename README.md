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
<br>

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
<br>
  <p align="left">
    <img src="https://github.com/user-attachments/assets/4cd76818-c96c-47c6-a7d7-b8d94cef817e" width="420">
  </p>
  <br>
  <p align="left">
    <img src="https://github.com/user-attachments/assets/64c2d568-5762-4b75-afd0-39fc12cb719d" width="600">
  </p>

<br>

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

<br>

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
  
<br>

🎥 **Demo:**
<div align="left">
  <video src="https://github.com/user-attachments/assets/5cbe6f2c-3006-4e75-8059-bc0c9334db39">
  </video>
</div>

<br>

### `db-config.yml`  
After configuration, the new access details are automatically saved here — meaning no need to reconfigure every run.  
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

<br>

---


## 👥 Customer Management (CRUD)

The system supports full **Customer CRUD operations** (Create, Read, Update, Delete) with layered validation and error handling.  

- **Validation Layer:**  
  Before persisting, customer data is validated using `CustomerValidatorUtil` and `CommonValidatorUtil`.  
  Examples:  
  - Customer ID format → `C001`  
  - Name format (letters only, first letter uppercase, max 30 chars)  
  - DOB cannot be in the future & age < 120  
  - Address / Postal Code character rules  

- **Service Layer (`ServiceCustomerImpl`)**  
  Handles business logic and validation.  
  Delegates database actions to the repository, and throws `CustomerServiceException` on validation or DB errors.  

- **Repository Layer (`RepositoryCustomer`)**  
  Encapsulates all SQL queries for customers.  

<br>

🎥 **Demo: Customer CRUD**  
<div align="left">
  <video src="https://github.com/user-attachments/assets/31b8e671-da35-440d-9370-456b72f3fcc0"></video>
</div>

<br>

---



## 📦 Item Management (CRUD)

Similar to customer handling, the system supports full **Item CRUD operations** with validation rules.  

- **Validation Layer:**  
  - Item code format → `P001`, `B0123`  
  - Description max length: 100 chars  
  - Pack size validation (e.g., `1kg`, `1.5L`)  
  - Price must not be `0.00`  
  - Quantity must not be `0`  

- **Service Layer (`ServiceItemImpl`)**  
  Converts between `ItemDto` and `ItemEntity`, validates, and throws `ItemServiceException` on invalid input or SQL errors.  

- **Repository Layer (`RepositoryItem`)**  
  Encapsulates SQL queries for inserting, updating, deleting, and fetching items.  

<br>

🎥 **Demo: Item CRUD**  
<div align="left">
  <video src="https://github.com/user-attachments/assets/2887deba-4ced-46d2-969c-04d0ead92ed1"></video>
</div>

<br>

---



## 📦 Dependencies (pom.xml)

```xml
  <dependencies>
      Javafx-controls 19 — UI toolkit
      Javafx-fxml 19
      MySQL Connector/J 8/9 — JDBC driver
      Lombok 1.18.38 — boilerplate reduction      
      SnakeYAML 2.4 — YAML parsing
  </dependencies>
```

<br>



## 🚀 How to Run the Project

### 🧰 Prerequisites

Make sure you have the following installed on your system:
- **Java 21** or later  
- **Maven** (for dependency management and build)  
- **MySQL Server** (local or remote instance running)  
- **IDE** (IntelliJ IDEA recommended, with JavaFX plugin support)  


1. **Clone the Repository**
   ```bash
   git clone https://github.com/rnshalinda/Thogakade-cims-layered-architecture-javafx-mysql.git
   cd Thogakade-cims-layered-architecture-javafx-mysql/ThogaKade
   ```
2. Open the project in IntelliJ IDEA.
   
3. **Build with Maven** in the InteliJ console run
   ```bash
   mvn clean install
   ```
4. Run Main.java

<br>

---


## 🛠️ Troubleshoot Common Issues (IntelliJ / Maven)

When running the project after cloning, you might face errors like:

    java: cannot find symbol
    symbol: method getDbName()
    location: class edu.icet.cims.model.dto.DbConfigDto

This usually happens because IntelliJ has not properly indexed the project or Maven dependencies.

### ✅ Solution
1. **Reimport Maven Project**  
   - In IntelliJ, right–click the `pom.xml` → *Add as Maven Project*.  
   - Or, click the *Maven* tool window → refresh/reimport.  

2. **Invalidate Caches & Restart**  
   - Go to `File → Invalidate Caches -> Tick all boxes -> Invalidate and Restart`.  
   - This forces IntelliJ to clear caches and rebuild its indexes.  

3. **Rebuild Project**  
   - From the top menu: `Build → Rebuild Project`.  

4. **Check Java SDK**  
   - Ensure the project SDK is set to Java 21 (since this project uses Java 21).  
   - `File → Project Structure → Project SDK`.

After these steps, IntelliJ should correctly recognize methods like `getDbName()` and the project will build and run without issues.

