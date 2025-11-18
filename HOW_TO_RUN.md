# How to Run - University ERP System

## Prerequisites

1. **Java Development Kit (JDK)**
   - Version: JDK 8 or higher (Java 11+ recommended)
   - Verify: `java -version`

2. **MySQL Database Server**
   - Version: MySQL 8.0 or higher
   - Running on: `localhost:3306`
   - Default user: `root`

3. **Maven** (Optional, for building)
   - Version: 3.6 or higher
   - Verify: `mvn -version`

## Database Setup

### Step 1: Run Database Scripts

Navigate to the `database/` folder and run the SQL scripts in order:

```bash
# Option 1: Using MySQL command line
mysql -u root -p < database/01_schema_auth.sql
mysql -u root -p < database/02_schema_main.sql
mysql -u root -p < database/03_seed_data.sql
```

Notes:
- Ensure MySQL is running on `localhost:3306` and accessible with your credentials.

## Running the Application

```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="edu.univ.erp.App"
```


## Default Test Accounts

After running seed data, you can login with:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin |
| `inst1` | `inst1_1` | Instructor |
| `stu1` | `stu1_1` | Student |
| `stu2` | `stu2_2` | Student |


## Features Overview

### Student Features
- Browse course catalog
- Register for sections
- Drop sections
- View timetable
- View grades
- Download transcript (CSV/PDF)

### Instructor Features
- View assigned sections
- Enter and manage grades
- View class statistics
- Export grades (CSV)

### Admin Features
- Manage users (add/edit/delete)
- Manage courses and sections
- Assign instructors to sections
- Toggle maintenance mode
- Backup and restore databases