# Smart Excel-JSON Parser 

## Overview
Smart Excel-JSON Parser is a full-stack web application that allows users to:
1. Convert Excel files with given to JSON.
2. Convert JSON data into structured Excel files.

The application is built using **Spring Boot (Java)** for the backend and **Thymeleaf** for the frontend.

---

## Features
- Supports multiple sheets in an Excel file, encapsulated into a single JSON structure.
- Converts JSON back to an Excel file while preserving structure.
- Provides a simple web-based UI for file uploads and conversions.
- Validates the Excel file type before processing.
- Unit tests for core functionalities.

---

## Setup & Execution

### Step 1: Download the Project
The project is provided in a Google Drive link. Follow these steps:
1. Go to the provided Google Drive link.
2. Download the **SmartExcelJsonParser.zip** file.
3. Extract the contents to a suitable location on your system.

### Step 2: Install Dependencies
Ensure you have the following installed:
- **Java 17** (Check with `java -version`)
- **Maven** (Check with `mvn -version`)

Then, navigate to the extracted project folder and run:
```sh
mvn clean install
```

### Step 3: Run the Application
To start the application, use the following command:
```sh
mvn spring-boot:run
```

## "Alternative (IDE):

Import the project as a Maven project in your IDE.

Run SmartExcelJsonParserApplication.java directly.

This will start the server on **http://localhost:8080**.

### Step 4: Access the Web UI
1. Open a web browser and go to:
   ```
   http://localhost:8080
   ```
2. Upload an Excel file(I have attached sample test files for both the use cases in root folder) and choose the conversion type (Excel → JSON or JSON → Excel).
3. View the results or download the converted file.

---

## Project Structure
```
SmartExcelJsonParser/
│── src/main/java/org/prakhar/
│   ├── controller/      # Handles HTTP requests
│   ├── service/         # Business logic for conversion
│   ├── utils/           # Helper functions for Excel & JSON processing
│── src/main/resources/templates/  # Thymeleaf HTML templates
│── pom.xml             # Project dependencies (Maven)
│── README.md           # Project documentation
```

---

## Technologies Used
- **Backend**: Spring Boot (Java), Maven
- **Frontend**: Thymeleaf (HTML)
- **Libraries**: Apache POI (for Excel handling), Jackson (for JSON processing)
- **Build Tool**: Maven

---

## Additional Notes, Assumptions, and Limitations
- If any issues occur during execution, ensure that Java and Maven versions are correctly installed and configured.
- The project is optimized for the provided sample Excel format. While it works with other format of sheets, but the best readable JSON results are achieved with the given structure.
- The application is designed for simple Excel structures. Complex formatting (e.g., colors, bold text) is not retained in JSON.

---

## Author
**Prakhar Pandey**

For any queries, please reach out via email at prakharpandey1198@gmail.com.

