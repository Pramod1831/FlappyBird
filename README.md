# FlappyBird Java Game

A simple clone of the popular game Flappy Bird built using Java and Swing for the graphical interface. <br>The objective of the game is to navigate the bird through gaps between pipes by pressing the spacebar to keep the bird from falling or crashing into the pipes.</br>

## Game Screenshot

![Flappy Bird Screenshot](Game_ScreenShot.png)

## Features

Graphics: The game uses custom images for the background, bird, and pipes.<br></br>
Scoring: Tracks the player's score as they pass through pipes.<br></br>
High Score: The highest score is saved to a file and loaded when the game starts.<br></br>
Gravity: The bird is affected by gravity, and you can make it jump by pressing the spacebar.<br></br>
Collision Detection: The game ends if the bird crashes into a pipe or falls to the ground.<br></br>

## Code Overview
1. App.java<br></br>
The App class is the entry point of the game. It sets up the main window (JFrame) and adds the FlappyBird game panel to it.<br></br>

Sets the window size to 360x640.<br></br>
Initializes the FlappyBird game class.<br></br>
Sets the window to non-resizable and centers it on the screen.<br></br>
Makes the window visible for the game to start.<br></br>

2. FlappyBird.java<br></br>
The FlappyBird class handles all the game logic, rendering, and user input.<br></br>

## Game Components:

Bird: Represents the player's character.<br></br>
Pipes: Randomly placed obstacles for the bird to avoid.<br></br>
Score & High Score: Tracks the player's score, and saves the highest score in a file named highscore.txt.<br></br>
Key Methods:<br></br>
paintComponent(Graphics g): Draws the background, bird, pipes, score, and high score.<br></br>
move(): Handles the movement of the bird and pipes, including gravity and velocity changes.<br></br>
collision(): Detects if the bird collides with any pipe, ending the game.<br></br>
placePipes(): Randomly generates pipes at regular intervals.<br></br>
loadHighScore() and saveHighScore(): Manages saving and loading the high score from highscore.txt.<br></br>
Controls:<br></br>
Spacebar: Press to make the bird jump.<br></br>
