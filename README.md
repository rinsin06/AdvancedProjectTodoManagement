
# Advanced Project Todo Management

## Table of Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [GitHub Token Configuration](#github-token-configuration)
- [Backend Configuration](#backend-configuration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction
This is an Advanced Project Todo Management application built with React and Material-UI (MUI). It allows users to create and manage todo projects efficiently.

## Prerequisites
Before you begin, ensure you have met the following requirements:
- **Node.js** (version 14 or higher) installed on your machine.
- **npm** (Node Package Manager), which comes with Node.js.
- **Java** (version 11 or higher) installed for the backend.
- **Maven** for managing backend dependencies.
- **PostgreSQL** database installed and running.

## Installation
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/rinsin06/AdvancedProjectTodoManagement.git
   ```
2. Navigate to the project directory:
   ```bash
   cd AdvancedProjectTodoManagement
   ```
3. Install the required dependencies for the frontend:
   ```bash
   npm install
   ```

## Running the Application
### Frontend
To start the frontend application in development mode, run:
```bash
npm start
```
This will start the development server, and you can view the application in your browser at `http://localhost:3000`.

### Backend
To run the backend application, follow these steps:
1. Navigate to the backend directory (ensure you have created a suitable backend structure):
   ```bash
   cd backend
   ```
2. Update the `application.properties` file with your database connection settings and GitHub token:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
   spring.datasource.username=your_database_username
   spring.datasource.password=your_database_password
   github.token=your_github_token
   ```
3. Run the backend application using Maven:
   ```bash
   mvn spring-boot:run
   ```
The backend should run on port 8080.

## GitHub Token Configuration
If your application interacts with GitHub APIs, you will need to set up a GitHub personal access token:

1. Go to your GitHub account settings.
2. Navigate to **Developer settings** > **Personal access tokens**.
3. Click on **Generate new token**, select the scopes you need, and copy the token.
4. In your project, create a `.env` file in the root directory of the frontend and add the following line (replace `your_github_token` with your actual token):
   ```plaintext
   REACT_APP_GITHUB_TOKEN=your_github_token
   ```
5. Access the token in your frontend application using:
   ```javascript
   const token = process.env.REACT_APP_GITHUB_TOKEN;
   ```

## Usage
- Use the UI to create, manage, and track your todo projects.
- Make sure to configure your GitHub token if you are using features that require authentication.

## Contributing
Feel free to submit issues or pull requests if you would like to contribute to the project.

## License
This project is licensed under the MIT License.
