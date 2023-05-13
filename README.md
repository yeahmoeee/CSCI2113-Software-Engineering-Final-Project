# ![CSCI2113](https://cs2113-s23.github.io/) Final Project: Ultimate Tic-Tac-Toe

![tic-tac-toe](https://user-images.githubusercontent.com/76827587/235328649-0283a9e2-a280-4651-8d05-d1a51d7967db.png)

In this project, we will be implementing a variant of Tic-Tac-Toe called the Ultimate (Nested) Tic-Tac-Toe. Since the project involved the implementation of a chat room based on the client-server model, instead of creating a new set of handshake protocol, we will be expanding on the pre-existing handshake protocol from [Project 2](https://github.com/yeahmoeee/yeahmoeee-project2/). Aside from the Ultimate Tic-Tac-Toe, we have also decided to include a game mode for waitlisted players to play an ordinary 3x3 Tic-Tac-Toe against an AI. More on how everything works will be explain underneath. 

## Rules for the Game

Suppose that Player $1$ is playing against Player $2$,

1. If Player $1$ choose position with index $i$ in sub-board $j$, then Player 2 can only choose any empty index from sub-board $i$.  

![IMG_A325F7B1165A-1](https://user-images.githubusercontent.com/76827587/235329112-bde345d7-891c-4063-81d2-2523f2838077.jpeg)

2. To win the game, one of the players must win the sub-boards aligned according to the similar rules, either in the same line or diagonal, just like in a regular Tic-Tac-Toe game. The rule to win each individual sub-board is also similar to Tic-Tac-Toe.

3. If a sub-board has been won by a player but still contains empty indices, those empty indices are still considered available for future moves.

4. If a sub-board is full, but no player has been declared as the winner in that sub-board, it is considered as wasted.
5. If a player is sent to a sub-board that is already full, the player can choose any empty index in any sub-board to make their move.

## GUI Description
### Log-in GUI
<img width="399" alt="Screenshot 2023-04-29 at 22 54 42" src="https://user-images.githubusercontent.com/76827587/235333293-b4f1bded-8b36-47d3-b6c8-2e8a1c1f8195.png">

The GUI for logging in is a simple design. It consists of a field labeled `Username` where you can enter your desired username. 

*Note: Username containing spaces or usernames already taken by others will not be accepted.*

### Ultimate Tic-Tac-Toe Main Game Board GUI 
<img width="1318" alt="Jack's perspective" src="https://user-images.githubusercontent.com/76827587/235334014-cc9482d8-8080-48e9-a699-e60bb7477f8d.png">
<img width="1321" alt="Jill's perspective" src="https://user-images.githubusercontent.com/76827587/235334015-c3e513c3-29e1-4162-a9b2-4548eb5c8c6b.png">

There are $3$ players connected to the server. The first two players are actual player who get to play against each other. The third player is on waitlist and is prompted with a different GUI. The first picture illustrate the perspective of Jack (playing as O). The second picture illustrate the perspective of Jill (playing as X). The sub-board coloured as red is won by Jack and the sub-board coloured as blue is won by Jill. 

*Note: If the sub-board is coloured as green, then it is considered draw and hence the sub-board is wasted.*

### Waiting Room GUI
<img width="314" alt="Waitlist" src="https://user-images.githubusercontent.com/76827587/235334173-3a06a358-fe26-4a4a-84b0-37c4462ff0dc.png">

This GUI is shown only to those who join the server when there are already two players on the server playing against each others or the player who lost the most recent match. There are two interesting buttons: `Practice` is to play the ordinary 3x3 tic-tac-toe against the AI. `Spectate` is to watch the live game that is ongoing between the two actual players. 

*Note: The GUI when you clicked `Spectate` is exactly the same as the Game Board GUI, with a few minor tweaks.*

### Ordinary 3x3 Tic-Tac-Toe against AI
<img width="840" alt="Screenshot 2023-04-29 at 23 48 46" src="https://user-images.githubusercontent.com/76827587/235334649-3bc4b112-af2f-4770-8630-8c727407fadc.png">

This GUI is shown when you clicked `Practice`. You are essentially playing against an AI. This is implemented using a [Minimax algorithm](https://github.com/yeahmoeee/tic-tac-toe). 


## Setup
To run the server, enter the following commands:
```
javac Board_PVP_Channel.java
java Board_PVP_Channel 8080
```
*Note: We have decided to use a fixed port (in our case, it is 8080) hosted on a local device. You can alter part of the code to change IP Address and Port Number. However, for sake of simplicity, we have removed the option for players to do it on the GUI.*

Once the server started running, in order to run the GUI, enter the following commands:
``` 
javac GameLoginFrame.java
java GameLoginFrame
```
