CREATE DATABASE HRMS_SIMPLE;
USE HRMS_SIMPLE;

-- Employees Table

CREATE TABLE employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INT NOT NULL,
    gender ENUM('MALE','FEMALE','OTHER') NOT NULL,
    years_experience INT NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    department VARCHAR(50) NOT NULL,
    manager_id INT NULL,
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

-- Users Table

CREATE TABLE users (
    sr_no INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('ADMIN','MANAGER') NOT NULL,
    emp_id INT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);

-- =====================================================
-- INSERT EMPLOYEES (15 total, 4 managers with manager_id = NULL)
-- =====================================================

INSERT INTO employees (first_name, last_name, phone_number, email, age, gender, years_experience, salary, department, manager_id)
VALUES
('Alice','Johnson','5550000001','alice.johnson@example.com',40,'FEMALE',15,90000,'Sales',NULL),
('Bob','Smith','5550000002','bob.smith@example.com',38,'MALE',12,85000,'IT',NULL),
('Carol','Davis','5550000003','carol.davis@example.com',42,'FEMALE',18,92000,'HR',NULL),
('David','Lee','5550000004','david.lee@example.com',45,'MALE',20,95000,'Finance',NULL),
('John','Doe','5550000005','john.doe@example.com',28,'MALE',3,50000,'Sales',1),
('Jane','Roe','5550000006','jane.roe@example.com',32,'FEMALE',5,60000,'Sales',1),
('Liam','Scott','5550000007','liam.scott@example.com',29,'MALE',4,48000,'Sales',1),
('Mike','Brown','5550000008','mike.brown@example.com',26,'MALE',2,45000,'IT',2),
('Sara','Wilson','5550000009','sara.wilson@example.com',29,'FEMALE',4,52000,'IT',2),
('Emma','Clark','5550000010','emma.clark@example.com',27,'FEMALE',3,47000,'IT',2),
('Tom','Harris','5550000011','tom.harris@example.com',31,'MALE',6,58000,'HR',3),
('Ava','Turner','5550000012','ava.turner@example.com',27,'FEMALE',3,47000,'HR',3),
('Olivia','Lopez','5550000013','olivia.lopez@example.com',30,'FEMALE',7,65000,'Finance',4),
('Ethan','Walker','5550000014','ethan.walker@example.com',33,'MALE',9,75000,'Finance',4),
('Noah','Baker','5550000015','noah.baker@example.com',28,'MALE',3,51000,'Finance',4);

-- =====================================================
-- INSERT USERS (Admin + Managers)
-- =====================================================
INSERT INTO users (username, password, role, emp_id)
VALUES
('admin','admin123','ADMIN',NULL),
('alice','alice123','MANAGER',1),
('bob','bob123','MANAGER',2),
('carol','carol123','MANAGER',3),
('david','david123','MANAGER',4);


-- audit_log Table

CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    changed_by VARCHAR(50),
    column_name VARCHAR(50),
    old_value VARCHAR(100),
    new_value VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
