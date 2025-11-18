# Hotel Control System

> A robust, CLI-based Hotel Management System featuring a custom AVL-Tree Map implementation.

[![Java](https://img.shields.io/badge/Java-25-orange)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9.+-blue)](https://maven.apache.org/)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![License](https://img.shields.io/badge/license-MIT-green)]()

## Table of Contents
- [1. About the Project](#-about-the-project)
- [2. Key Features](#-key-features)
- [3. Technical Highlights](#-technical-highlights)
  - [Custom Map Implementation](#custom-map-implementation)
  - [Design Patterns](#design-patterns)
- [4. Project Structure](#-project-structure)
- [5. Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [6. Usage](#-usage)
  - [Running the Application](#running-the-application)
  - [Command Examples](#command-examples)
- [7. Quality Assurance](#-quality-assurance)

---

## 1. About the Project

This project was developed as a comprehensive exercise in Java programming, focusing on Object-Oriented Programming (OOP) principles, algorithm implementation, and software architecture.

The core objective was to create a functional **Hotel Management System** that operates via a text-based interface (REPL). A critical requirement was to implement a custom **Map data structure** from scratch (without relying on `java.util.HashMap` or `TreeMap` for internal storage) and use it to manage the hotel's data.

**Bonuses Implemented:**
- ✅ **Bonus 1:** Loading hotel configuration/state from an external CSV file on startup.
- ✅ **Bonus 2:** Persisting the current hotel state (rooms, guests, reservations) to a CSV file.

---

## 2. Key Features

The system provides a fully interactive command-line interface to manage hotel operations:

* **Check-in:** Register new guests into rooms with validation (capacity, occupancy).
* **Check-out:** Process guest departures and calculate the total bill based on stay duration.
* **View Room:** Display detailed information about a specific room (price, capacity, current residents).
* **List All:** Show a formatted table of all rooms, their status (Free/Occupied), and main guest details.
* **Prices:** Display a price list for all rooms.
* **Persistence:** Save and Load the entire hotel state to/from CSV files.

---

## 3. Technical Highlights

### Custom Map Implementation
Instead of using standard Java collections, this project features a custom, generic implementation of a Map: `MyMap<K, V>`.
* **Data Structure:** **AVL Tree** (Self-balancing Binary Search Tree).
* **Complexity:** Guaranteed **O(log n)** for `put`, `get`, and `remove` operations.
* **Features:** Automatic balancing (rotations), generic key/value support, custom iterator.
* **Location:** `my-map-implementation` module.

### Design Patterns
The architecture adheres to SOLID principles and utilizes standard design patterns:
1.  **Command Pattern:** Encapsulates user actions (`CheckinCommand`, `ListCommand`, etc.) implementing a common interface.
2.  **Factory Pattern:** The `CommandRegistry` dynamically creates command instances based on user input using Java Reflection.
3.  **Strategy Pattern:** The execution logic is decoupled from the command selection.

---

## 4. Project Structure

The project is organized as a Multi-Module Maven project:

```text
hotel-control-system/
├── hotel-main/                # Application logic & Entry point
│   ├── src/main/java/
│   │   ├── commandcontrol/    # Commands & Registry (Controller)
│   │   ├── model/             # Hotel, Room, Guest, Reservation (Model)
│   │   └── HotelApplication.java
│   └── src/test/java/         # Unit tests for logic
├── my-map-implementation/     # Utility library (hotel-utils)
│   ├── src/main/java/
│   │   └── map/               # MyMap (AVL Tree) implementation
│   └── src/test/java/         # Unit tests for Data Structure
├── javadoc/                   # Generated documentation
├── sonar-qube/                # Quality reports
└── pom.xml                    # Parent POM
````

-----

## 5. Getting Started

### Prerequisites

* **Java JDK 25** (or compatible newer version)
* **Apache Maven** 3.6+
* (Optional) **Docker** for running SonarQube analysis.

### Installation

1.  **Clone the repository:**

    ```bash
    git clone [https://github.com/your-username/hotel-control-system.git](https://github.com/your-username/hotel-control-system.git)
    cd hotel-control-system
    ```

2.  **Build the project:**
    This will compile code, run tests, and generate the "Fat JAR" (including dependencies).

    ```bash
    mvn clean package
    ```

-----

## 6. Usage

### Running the Application

You can run the application in two modes:

**1. Default Mode (Hardcoded Data):**

```bash
java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**2. File Mode (Load from CSV):**
Provide a path to your `hotel_state.csv` file as an argument.

```bash
java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar hotel_state.csv
```

### Command Examples

The User Interface is designed to be clean and informative.

**Listing all rooms (`list`):**

```text
---[ ALL ROOMS INFORMATION ]---
+-----------------------------------------------------------------------------------+
| Room Nr  | Status     | Main guest                | Checkin date  | Checkout date |
+-----------------------------------------------------------------------------------+
| 101      | Free       | ---                       | ---           | ---           |
| 202      | Occupied   | Alice Smith               | 2025-11-17    | 2025-11-22    |
+-----------------------------------------------------------------------------------+
```

**Checking a guest in (`checkin`):**

```text
> checkin

---[ CHECK-IN ]---
Enter room number: 101
Enter main guest full name: John Doe
Enter number of additional guests (0-1): 0
Enter duration of stay (nights): 3
Enter check-in date (YYYY-MM-DD) or press Enter for today: 

Check-in completed successfully for room 101.
```

**Saving state (`save`):**

```text
> save
Provide the name of the file where you want to save hotel state or press Enter for hotel_state.csv filename:
my_backup.csv

---[ SUCCESSFULLY SAVED TO THE FILE 9 ROOMS ]---
```

-----

## 7. Quality Assurance

The project maintains high code quality standards:

* **Unit Tests:** \~80% code coverage using **JUnit 5** and **Mockito**.
    * Complex logic (AVL balancing, booking rules) is thoroughly tested.
* **Static Analysis:** Verified with **SonarQube**.
    * ✅ Zero Blocker/Critical issues.
    * ✅ "A" rating for Reliability and Security.
    * ✅ Reduced Cognitive Complexity.
* **Documentation:** Comprehensive **Javadoc** available in the `/javadoc` directory.

-----

### Author

**Dmitry Nikitin**
*Student at AGH University of Science and Technology*

