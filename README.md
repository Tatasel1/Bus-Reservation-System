# Bus Reservation System

![Kotlin](https://img.shields.io/badge/Kotlin-JVM-purple)
![CLI](https://img.shields.io/badge/Interface-CLI-green)

A command-line bus reservation application built in Kotlin, demonstrating OOP design, role-based access control, custom exception handling, and CSV-based file persistence.

## Features

- **Operator** — add / remove trips
- **Client** — search trips, make reservations
- **Auth** — hashed passwords, email validation, password strength check
- **Storage** — CSV persistence across sessions (users, trips, reservations)

## Project Structure
├── Main.kt          # entry point & menu loop
├── Aplicatie.kt     # core app logic
├── User.kt          # User / Client / Operator hierarchy
├── Trip.kt          # Trip data model
├── Reservation.kt   # Reservation data model
├── Utils.kt         # SecurityUtils singleton
├── Exceptions.kt    # custom exception classes
└── data/
├── users.csv
├── operator.csv
├── calatorii.csv
└── rezervari.csv

## Getting Started

### Prerequisites

- JDK 8 or higher
- Kotlin compiler (`kotlinc`)

### Run

```bash
# Compile
kotlinc src/*.kt -include-runtime -d bus.jar

# Run
java -jar bus.jar
```
