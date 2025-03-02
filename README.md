# Skyjo Game

Skyjo Game is a Java implementation of the popular card game Skyjo. This project offers a complete gameplay experience with animations, artificial intelligence, sound management, and configurable options to provide an immersive experience.

## Features

- **Complete Game Logic**  
  Manages card distribution, exchanges, reveals, discards, and column checks according to Skyjo rules.

- **Artificial Intelligence**  
  Implements AI players with different difficulty levels (EASY, MEDIUM, HARD) that make decisions based on the game state without cheating.

- **Animated User Interface**  
  Smooth animations for card flipping, distribution, and view transitions to enhance the user experience.

- **Sound and Music Management**  
  Integration of sound effects (e.g., flip sound) and background music with volume control and fade effects.

- **Configurable Options**  
  Choose themes (light/dark), game modes, and volume settings via a dedicated options interface.

- **Rankings and End Game**  
  Calculates and displays the final ranking based on players' cumulative scores.

## Project Structure



## Technologies Used

- **Java 21** – Main programming language
- **JavaFX 21** – Framework for the user interface
- **Gradle Kotlin DSL** – Build tool
- **JUnit 5** – Unit testing framework

## Installation and Execution

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Matheo-Piget/Skyjo
   cd Skyjo

2. **Compile and run the project**

- For Windows:
    ```bash
    gradle build
    gradle run
- For Linux:
    ```bash
    ./gradlew build
    ./gradlew run