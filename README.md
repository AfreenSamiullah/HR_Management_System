# HRMS JavaFX Application

## Description

**HRMS** is a comprehensive Java-based Human Resource Management System (HRMS) with an interactive **JavaFX** user interface. It empowers **Admins** and **Managers** to efficiently manage employee information, track changes, and visualize organizational data through dynamic charts. The system is built on a **MySQL backend** and enforces robust role-based access control.  

This application is ideal for small to medium-sized organizations aiming to manage employee data, including personal information, department allocations, compensation, and reporting hierarchy, while maintaining an audit trail of all modifications.

---

## Features

- **Role-Based Authentication**
  - Secure login for Admins and Managers.
  - Managers must provide Employee ID for verification.

- **Employee Management**
  - Add, edit, and remove employee records with a fully editable table interface.
  - Inline editing automatically updates the database with proper audit logging.
  - Supports comprehensive employee attributes: name, contact, age, gender, department, experience, salary, and manager assignment.

- **Audit Trail**
  - Every change to employee records is logged with:
    - Who made the change
    - Which field was changed
    - Previous and new values
    - Timestamp of the change
  - Audit log viewable in a dedicated interface for Admins.

- **Advanced Queries**
  - Execute custom queries for data analysis (Admins).
  - Managers are restricted to queries on their team members for data security.

- **Graphical Analytics**
  - Gender distribution visualized with pie charts.
  - Departmental salary comparisons shown using bar charts.
  - Dynamic, interactive graphs update with real-time data.

- **Database Integration**
  - All data persisted using a relational database.
  - Designed to support complex queries, joins, and aggregation functions.
  - Ensures data integrity with transactional updates and constraint enforcement.

- **User-Friendly UI**
  - Fully responsive layout with adaptive table resizing.
  - Interactive components for quick access and modifications.
  - Maximized window and full-screen support for better visibility.

---

## Working functionality

> https://drive.google.com/file/d/193RNN-22wQeJoFvOhC0Pvq-poAX0LEGn/view?usp=drive_link


---

## Database Setup

The system relies on a **complex relational schema** including:

1. **Users Table** – Handles authentication and role management, linking managers to their employees.
2. **Employees Table** – Stores comprehensive employee information, including hierarchical relationships and payroll data.
3. **Audit Log Table** – Captures all changes for traceability, supporting advanced filtering and sorting.

The database is designed for **data integrity, normalized structure**, and **efficient query performance**, supporting both transactional updates and analytical operations.

> Note: Sample users include an Admin and a Manager. Sample employee data covers multiple departments, diverse roles, and varied compensation levels for realistic testing.

---

## Requirements

- **Java 17+**
- **JavaFX SDK**
- **MySQL 8.0+**
- IDEs like IntelliJ IDEA, Eclipse, or NetBeans recommended
- Properly configured JDBC connection for database access

---
## 🛠️ Usage Guide

### 👑 Admins
- ✅ Full access to manage **all employees**.  
- 📊 Can view and filter the **audit log**.  
- 📝 Execute **advanced queries** on the complete dataset.  
- 📈 Analyze **charts and statistics** for strategic insights.  

### 👔 Managers
- 🔒 Limited access to their **direct reports** only.  
- ✏️ Can update **team members’ information inline**.  
- 📉 View **charts and statistics** filtered by their team.  

### 🖱️ Employee Table Editing
- 🖱️ **Double-click** any editable field to modify.  
- 💾 Changes are automatically **validated, persisted, and logged**.

  ## 📝 License

This project is licensed under the **MIT License**.  

You are free to:

- ✅ Use the code for personal or commercial projects.  
- 🔄 Modify and distribute it.  
- 🛠️ Incorporate it into your own applications.  

**Conditions:**

- ⚠️ You must include the original license and copyright notice in any copies or substantial portions of the software.  
- ❌ The software is provided "as-is", without warranty of any kind.



