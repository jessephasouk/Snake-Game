# Snake Game 🐍

A classic Snake game implementation in Java using Swing for the GUI. Control the snake to eat food, grow longer, and try to fill the entire board without hitting yourself or the walls!

## Features

- **Smooth Controls**: Responsive keyboard input with direction queuing for quick turns
- **Win Condition**: Fill all 400 tiles (20×20 board) to win
- **Score Tracking**: Keep track of your current score
- **Game Over Detection**: Collision detection for walls and self-collision
- **Restart Capability**: Press SPACE to restart after game over or winning

## How to Play

### Controls
- **Arrow Keys**: Control snake direction (Up, Down, Left, Right)
- **SPACE**: Restart the game after game over

### Rules
- Eat the red food to grow longer and increase your score
- Avoid hitting the walls
- Avoid running into yourself
- Fill the entire board to win!

## Running the Game

### Option 1: Using the Batch File (Easiest)
Simply double-click `RunSnake.bat` to launch the game.

### Option 2: Using Command Line
```bash
javac SnakeGame.java
java SnakeGame
```

## Technical Details

- **Language**: Java
- **GUI Framework**: Swing
- **Board Size**: 20×20 tiles (500×500 pixels)
- **Tile Size**: 25×25 pixels
- **Game Speed**: 100ms delay between moves

## Implementation Highlights

- **LinkedList** for efficient snake operations (O(1) add/remove)
- **Direction Queue** to buffer multiple quick inputs between game ticks
- **Collision Detection** for walls and self-collision
- **Constructor Safety**: No "this" reference leaks during construction
- **Null-Safe**: Proper null handling for queue operations

## Requirements

- Java Development Kit (JDK) 8 or higher

## Project Structure

```
Snake/
├── SnakeGame.java    # Main game source code
├── RunSnake.bat      # Windows batch file to run the game
├── MANIFEST.MF       # JAR manifest file
├── .gitignore        # Git ignore rules
└── README.md         # This file
```

## License

Free to use and modify for educational purposes.

## Author

Built with assistance from GitHub Copilot 🤖
