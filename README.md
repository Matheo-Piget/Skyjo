# 🎴 Skyjo Game 🎴

Skyjo is a popular card game where players aim to have the lowest score by managing their cards strategically. This project is a Java-based implementation of Skyjo, offering a complete gameplay experience with smooth animations, AI opponents, and customizable options.

## ✨ Features

- 🎲**Complete Game Logic**
  - Manages card distribution, exchanges, reveals, discards, and column checks in accordance with Skyjo rules.

- 🤖**Artificial Intelligence**
  - Implements AI players with varying difficulty levels (EASY, MEDIUM, HARD) that make strategic decisions based on the game state without cheating.

- 🎨**Animated User Interface**
  - Offers smooth animations for card flipping, distribution, and view transitions to enhance the user experience.

- 🎵 **Sound and Music Management**
  - Integrates sound effects (e.g., flip sound) and background music with volume control and fade effects.

- ⚙️**Configurable Options**
  - Allows users to choose themes (light/dark), game modes, and volume settings via a dedicated options interface.

- 🏆**Rankings and End Game**
  - Calculates and displays the final ranking based on players' cumulative scores.

## 🏗️Project Structure

The project is organized into several modules, each responsible for different aspects of the game:

- 🎮 **Game Logic**: Contains the core game mechanics and rules.
- 🤖 **AI**: Implements the artificial intelligence for computer-controlled players.
- 🖥️ **UI**: Manages the user interface and animations.
- 🎵**Sound**: Handles sound effects and background music.
- ⚙️ **Options**: Provides customizable settings for themes, game modes, and volume.
- 🏆**Rankings**: Calculates and displays the final game rankings.

## Technologies Used

- **Java 21**: Chosen for its robustness and cross-platform compatibility.
- **JavaFX 21**: Used for building a modern and responsive user interface.
- **Gradle Kotlin DSL**: Provides a flexible and efficient build system.
- **JUnit 5**: Ensures code reliability through comprehensive unit testing.

### 📋Prerequisites
- Java 21 or later.
- Gradle installed on your system.

### 🚀Installation and Execution

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

### 🤝 Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Submit a pull request with a detailed description of your changes.
4. Ensure your code follows the existing style and includes relevant tests.

## 📜  License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
