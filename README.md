# Online Publishing Platform JDBC

A Java + MySQL management system for an online publishing platform. It manages articles, authors, categories, subscriptions, and more, with a command-line interface for administration.

## Features

- User registration and role management (Admin, Editor, Author, Subscriber)
- Article workflow: draft, review, publish, archive
- Author and category management
- Tagging, series, and comments support
- Subscription plans and purchase tracking
- Revenue and analytics queries (churn rate, top articles, etc.)
- CLI-based admin panel

## Database Schema

- MySQL schema is auto-created on first run (see `SQLQueries.java`)
- Includes tables for users, roles, articles, categories, tags, subscriptions, purchases, comments, and more

## Getting Started

### Prerequisites

- Java 22+
- MySQL 8.0+ Server running locally (default user: `root`)
- Gradle (or use the provided shadow JAR)

### Setup

1. **Clone the repository:**
   ```
   git clone https://github.com/yourusername/online-publishing-platform-jdbc.git
   cd online-publishing-platform-jdbc
   ```

2. **Build the shadow JAR:**
   ```
   gradle shadowJar
   ```
   The JAR will be created in the project root as `jdbc-app-1.0.jar` (or similar).

3. **Run the application:**
   ```
   java -jar jdbc-app-1.0.jar
   ```

4. **On first run:**
   - Enter your MySQL root password when prompted.
   - The database and tables will be created automatically, with sample data inserted.

## Usage

- Use the CLI menu to:
  - View analytics and reports
  - Register new users
  - Manage articles and workflow
  - Change subscription plan prices and status

## Project Structure

```
online-publishing-platform-jdbc/
├── bin/
├── build.gradle
├── gradle/
├── gradlew.bat
├── jdbc-app-1.0.jar
├── lib/
│   └── mysql-connector-j-9.4.0.jar
├── settings.gradle
└── src/
    ├── Main.java
    └── onlinePublishingPlatform/
        ├── CLIMenu.java
        ├── DBConnector.java
        ├── Menu.java
        ├── SQLQueries.java
        └── onlinePublishingPlatform.java
```

## Notes

- All data is stored in a MySQL database named `AdamsOPP`.
- The CLI is for demonstration and admin use.

## License

MIT License

