# CSE360 Individual Homework 3 - Ishita Deshpande

## Overview

This project is part of CSE360's Individual Homework 3 assignment and focuses on the development and automation of database-driven question-and-answer features. It includes a suite of five automated JUnit tests to validate the core functionality of creating, reading, updating, and deleting questions and answers in a JavaFX + H2-backed environment.

---

## 1. Automated Tests Implemented

### 1. `CreateAnswerQuestionTestPage`
**Purpose**: Validates that a question can be inserted into the database and retrieved correctly using its ID.  
**Key Actions**: Inserts a user and a question; retrieves it by ID; verifies content.

### 2. `DeleteAnswerQuestionTestPage`
**Purpose**: Tests the lifecycle of inserting and then deleting a question and its associated answer.  
**Key Actions**: Registers a user, adds question and answer, deletes both, and ensures successful cleanup.

### 3. `UpdateAnswerQuestionTestPage`
**Purpose**: Verifies that questions and answers can be updated in the database.  
**Key Actions**: Inserts a test question and answer, updates both, and confirms updated content.

### 4. `ReadAnswerQuestionTestPage`
**Purpose**: Confirms that questions and answers are correctly retrieved by ID.  
**Key Actions**: Adds a test user, question, and answer; retrieves and verifies them.

### 5. `QuestionAnswerResolvedTestPage`
**Purpose**: Tests resolution tracking for questions and proper status filtering.  
**Key Actions**: Adds one resolved and one unresolved question, validates sorting and status.

---

## 2. Javadoc Resources

- [Google Java Style Guide - Javadoc](https://google.github.io/styleguide/javaguide.html#s7-javadoc)  
- [Java Collections Source - DocJar (Archived)](https://web.archive.org/web/20190325071840/http://www.docjar.net/html/api/java/util/Collections.java.html)

> The Javadoc HTML output is available in this GitHub repo within the Javadoc HW3 Documentation pdf file: 
> [Javadoc HW3 Documentation.pdf](https://github.com/ideshpa2/CSE360_Individual-HW/blob/hw3/Javadoc%20HW3%20Documentation.pdf)

---

## 3. Screencast Submission
-  Screencast embedded in this `README.md` and available via Zoom:
- [Watch Screencast](https://asu.zoom.us/rec/share/qUoNGlZYP7i-2l0WXTGDeHlT4yp3LUcQCScDevhbBcmBwF4OYsMWEb8eBIpvE72N.marT8GkyCNEHHLb0)
- Passcode: `4te?B!P@`

---

## Author

**Ishita Deshpande**  
