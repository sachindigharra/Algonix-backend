## AlgoNix Backend

A scalable backend application built with **Java, Spring Boot, Spring Security, PostgreSQL, and Swagger** to help developers track their coding journey, problem-solving progress, contest participation, and coding platform statistics in one place.

## Overview

DSA Tracker is designed for students and software engineers preparing for coding interviews and competitive programming. The platform centralizes coding activity across multiple platforms such as LeetCode, Codeforces, CodeChef, GeeksforGeeks, HackerRank, and GitHub.

The project follows a clean layered architecture using Controller, Service, Repository, DTO, Mapper, and Exception Handling layers, making it easy to maintain and extend.

## Features

### User Management

* User registration and authentication
* Secure password storage using BCrypt
* Profile management
* Role-based architecture ready for future enhancements

### Coding Platform Profiles

* Link multiple coding platforms
* Store usernames and profile URLs
* Track ratings, maximum ratings, contributions, streaks, and solved problems

### Problem Tracking

* Track solved, attempted, todo, and revisit problems
* Store notes, approaches, and complexity analysis
* Categorize by difficulty, tags, and companies
* Associate problems with popular sheets such as Blind 75, Striver SDE Sheet, Grind 75, and NeetCode 150

### Contest Management

* Maintain contest participation history
* Track rankings, rating changes, and problems solved
* Support multiple competitive programming platforms

### Revision Tracking

* Schedule and monitor problem revisions
* Build long-term retention of DSA concepts

### API Documentation

* Interactive Swagger/OpenAPI documentation
* Easy API testing and exploration

## Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL

### Documentation

* Swagger / OpenAPI

### Utilities

* Lombok
* SLF4J Logging
* Bean Validation

## Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Additional layers:

```text
DTO
Mapper
Exception Handling
Logging
Security
```

## Security

Current Phase:

* Spring Security Basic Authentication
* User credentials stored in PostgreSQL
* Password encryption using BCrypt

Planned Phase:

* JWT Authentication
* Access and Refresh Tokens
* Stateless Authentication

## Database Modules

* Users
* User Profiles
* Platform Profiles
* Problems
* Problem Revisions
* Contests

## Project Goals

* Provide a centralized DSA preparation dashboard
* Track coding growth over time
* Monitor contest performance
* Build revision habits
* Demonstrate production-grade Spring Boot backend development practices

## Future Enhancements

* JWT Authentication
* Email Verification
* Password Reset
* Dashboard Analytics
* Leaderboards
* AI-powered Revision Recommendations
* Contest Reminders
* AWS Deployment
* Docker and Kubernetes Support

This project is being developed as a real-world backend application to strengthen expertise in Spring Boot, Security, PostgreSQL, API Design, System Design fundamentals, and cloud-native development practices.
