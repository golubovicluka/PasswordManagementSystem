# Password Management System

A secure, feature-rich desktop password manager built with JavaFX.

## Features

- **Secure Password Storage**: All passwords are encrypted using industry-standard BCrypt hashing algorithm
- **User Authentication**: Multi-user support with secure login/registration system
- **Intuitive UI**: Modern, user-friendly interface built with JavaFX
- **Password Organization**: Categorize your passwords for better organization
- **Search Functionality**: Quickly find passwords with powerful search filters
- **Website Favicon Integration**: Visual identification of websites using their favicons
- **Password Masking**: Passwords are hidden by default, with reveal-on-demand functionality
- **Copy to Clipboard**: Easily copy usernames and passwords without revealing them
- **Category Filtering**: Filter password entries by categories
- **CRUD Operations**: Full create, read, update, and delete functionality for password entries

## Tech Stack

- **Java 17**: Core programming language
- **JavaFX**: UI framework
- **Spring Security**: For password encryption
- **MySQL**: Database for storing encrypted user data
- **BCrypt**: Secure password hashing
- **Maven**: Dependency management and build tool
- **HikariCP**: High-performance JDBC connection pool
- **Jsoup**: HTML parser for website favicon extraction
- **JUnit & Mockito**: Testing frameworks

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository
   ```
   git clone https://github.com/yourusername/PasswordManagementSystem.git
   ```

2. Navigate to the project directory
   ```
   cd PasswordManagementSystem
   ```

3. Configure your database connection in `src/main/java/com/golubovicluka/passwordmanagementsystem/dao/DatabaseConnection.java`

4. Build the project
   ```
   mvn clean install
   ```

5. Run the application
   ```
   mvn javafx:run
   ```

## Usage

1. Register a new account or log in with existing credentials
2. Add new password entries with website, username, password, and category
3. Use the search function to find specific entries
4. Filter entries by category using the category buttons
5. Edit or delete entries as needed
6. Copy credentials to clipboard with a single click

## Security Features

- Passwords are never stored in plain text
- BCrypt hashing with salt for maximum security
- Session management to protect user data
- Automatic password masking in the UI

## Development

This project follows the MVC (Model-View-Controller) architecture:
- **Model**: Represents data objects like `PasswordEntry`, `User`, and `Category`
- **View**: FXML files defining the UI layout
- **Controller**: Java classes handling user interactions and business logic

### Project Structure

- `model/`: Data classes
- `controller/`: UI controllers
- `service/`: Business logic services
- `dao/`: Data access objects for database operations
- `view/`: FXML UI layout files
- `styles/`: CSS stylesheets
- `exception/`: Custom exception classes
