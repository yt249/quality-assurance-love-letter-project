# Love Letter - Quality Assurance Project
![Love Letter](./resources/love-letter.png)

## Overview
This repository contains the Quality Assurance implementation for the popular tabletop card game **Love Letter**, undertaken as a semester-long project at Carnegie Mellon University. The focus of this project was to enhance code quality, improve test coverage, and extend the game's functionality by incorporating new features.

This project was forked from an instructor-provided repository and includes extensive use of static analysis tools, unit testing, and refactoring techniques to ensure code reliability and maintainability.

## Project Highlights
- **Game**: Love Letter, a strategy-based card game where players compete to deliver a love letter to the princess by outlasting other players.
- **Technologies**: 
  - **Java** for game implementation
  - **JUnit** and **Mockito** for testing
  - Static analysis tools
  - **JaCoCo** for code coverage analysis
 
## Quality Assurance Improvements
- **Static Analysis**:
  - `Checkstyle` to enforce coding standards such as whitespaces and naming convensions
  - `SpotBugs` to detect issues related to exposing mutable objects, which promotes safer coding practices
- **Refactoring**: Improved code testability by restructuring core logic and removing hard-to-test patterns.
- **Test Suite**: Developed comprehensive JUnit tests to achieve **85% code coverage**, ensuring robust functionality.
  	<img width="864" alt="jac" src="https://github.com/user-attachments/assets/1f92e0f0-72fd-470a-b9c9-403c6c64d119" />

## Implementation
This project uses the following languages, tools, and frameworks:
* [Java 17.0.9](https://docs.oracle.com/en/java/javase/17)
* [Maven 3.9.9](https://maven.apache.org)
* [JUnit 5](https://junit.org/junit5)
* [Mockito](https://site.mockito.org) (v5.6.0)
* [EasyMock](https://easymock.org) (v5.2.0)

## Acknowledgments
* **Instructors**: Chris Timperley, Jeff Gennari
* **Contributors**: Christy Tseng, Nancy Lin, Watson Chao, Kuan Wu
* **Reference**: Forked from the original repository provided by the instructor for academic purposes
