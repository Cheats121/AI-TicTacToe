# AI-TicTacToe
A simple Tic-Tac-Toe game framework in Java, featuring multiple AI agents (random play, heuristic policies, and reinforcement-learning) that can play against each other or a human.
-------------------------------------------

---

## Table of Contents

- [Features](#features)  
- [Getting Started](#getting-started)  
  - [Prerequisites](#prerequisites)  
  - [Building](#building)  
  - [Running](#running)  
- [Available Agents](#available-agents)  
- [Project Structure](#project-structure)  
- [Contributing](#contributing)  
- [License](#license)  

---

## Features

- **Multiple AI agents**:  
  - RandomAgent (purely random moves)  
  - AggressiveAgent / DefensiveAgent (rule-based heuristics)  
  - QLearningAgent (tabular Q-learning)  
  - PolicyIterationAgent & ValueIterationAgent (dynamic programming)  
  - PolicyIterationAgent (with customizable Policy implementations, e.g. ε-greedy)  
- **Configurable environment**: `TTTEnvironment` and `TTTMDP` define board dynamics and reward structure.  
- **Human vs AI**: `HumanAgent` allows a human player to play against any AI agent.  
- **Easy to extend**: add new `Policy` or `Agent` implementations and plug them into `Game`.  

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher  
- A terminal/shell (Linux, macOS, or Windows PowerShell / CMD)  

### Building

From the project root, compile all source files into a `bin/` directory:

```bash
mkdir -p bin
javac -d bin src/main/java/ticTacToe/*.java

