
# Skyjo Game

Skyjo Game is a Java-based implementation of the popular card game Skyjo. This project aims to deliver a comprehensive gameplay experience, featuring smooth animations, intelligent AI opponents, sound management, and customizable options to provide an engaging and immersive user experience.

## Features

- **Complete Game Logic**
  - Manages card distribution, exchanges, reveals, discards, and column checks in accordance with Skyjo rules.

- **Artificial Intelligence**
  - Implements AI players with varying difficulty levels (EASY, MEDIUM, HARD) that make strategic decisions based on the game state without cheating.

- **Animated User Interface**
  - Offers smooth animations for card flipping, distribution, and view transitions to enhance the user experience.

- **Sound and Music Management**
  - Integrates sound effects (e.g., flip sound) and background music with volume control and fade effects.

- **Configurable Options**
  - Allows users to choose themes (light/dark), game modes, and volume settings via a dedicated options interface.

- **Rankings and End Game**
  - Calculates and displays the final ranking based on players' cumulative scores.

## Project Structure

The project is organized into several modules, each responsible for different aspects of the game:

- **Game Logic**: Contains the core game mechanics and rules.
- **AI**: Implements the artificial intelligence for computer-controlled players.
- **UI**: Manages the user interface and animations.
- **Sound**: Handles sound effects and background music.
- **Options**: Provides customizable settings for themes, game modes, and volume.
- **Rankings**: Calculates and displays the final game rankings.

## Technologies Used

- **Java 21**: Main programming language.
- **JavaFX 21**: Framework for building the user interface.
- **Gradle Kotlin DSL**: Build tool for managing dependencies and tasks.
- **JUnit 5**: Unit testing framework for ensuring code reliability.

## Installation and Execution

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/Matheo-Piget/Skyjo
   cd Skyjo
   ```

2. **Compile and Run the Project**:

   - For Windows:
     ```bash
     gradle build
     gradle run
     ```

   - For Linux:
     ```bash
     ./gradlew build
     ./gradlew run
     ```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes. Make sure to follow the existing code style and include relevant tests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
