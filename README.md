# University ERP System

A comprehensive Enterprise Resource Planning system for university course and enrollment management, built with Java Swing and MySQL.

## Features

- **Student Portal**: Browse courses, register/drop sections, view timetable and grades, download transcripts
- **Instructor Portal**: Manage sections, enter grades, view statistics, export data
- **Admin Portal**: User management, course/section management, maintenance mode, backup/restore

## Quick Start

1. **Setup Database**: Run SQL scripts in `database/` folder (see `HOW_TO_RUN.md`)
2. **Update Configuration**: Edit `src/main/java/edu/univ/erp/data/DatabaseConfig.java` with your MySQL credentials
3. **Start the App**: Use either the terminal (Maven) or your IDE as described below.

See `HOW_TO_RUN.md` for detailed instructions.

## Running the Application

**Project Root**: The folder containing `pom.xml`, `src/`, and `database/` folders.

**Why Maven?** This project uses Maven to automatically download and manage external libraries (MySQL driver, UI themes, PDF/CSV libraries, etc.). Your IDE can also handle this automatically—you don't need to run Maven commands manually if you use an IDE.

### Option A — Terminal (Maven)

Open a terminal/command prompt in the project root directory, then run:

```bash
# Navigate to project root (if not already there)
cd "ERP_Project"

# Build and run
mvn clean compile
mvn exec:java -Dexec.mainClass="edu.univ.erp.App"
```

**Note**: Requires Maven installed. This builds the project and launches the Swing UI. Make sure the MySQL databases are running and seeded first.

### Option B — IDE (Recommended - IntelliJ / Eclipse / NetBeans)

**Easiest method** - Your IDE handles Maven automatically:

1. Open/Import the project folder in your IDE (it will detect `pom.xml`).
2. Wait for Maven to download dependencies (first time only).
3. Ensure the project SDK/JDK is set to Java 11 (or newer).
4. Locate `src/main/java/edu/univ/erp/App.java`.
5. Right-click `App` and choose `Run 'App.main()'` (or press the Run button).

No Maven commands needed—the IDE handles everything!

After the login window appears, sign in with one of the default accounts below.

## Default Test Accounts

- **admin** / admin
- **inst1** / instructor
- **stu1** / student
- **stu2** / student

## Project Status

See `PROJECT_REPORT.md` for documentation and `TEST_PLAN.md` for the acceptance test suite.

## Documentation

- `HOW_TO_RUN.md` - Setup and running guide
- `TEST_PLAN.md` - Comprehensive test plan
- `TEST_SUMMARY.md` - Test results template
- `PROJECT_REPORT.md` - Complete project report
- `database/` - SQL schema and seed files

