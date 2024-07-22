# chessiguess
Attempted to recreate an entire chess game from scratch using PApplet processing on java. 
All classes made from scratch, except for the Game class using the basic PApplet template

Also attempted to create an AI Chess bot that uses the minimax algorithm to process its optimal next move versus the player, along with alpha-beta pruning to prune wasteful/unnecessary trees to explore.
By examining the current state of a board during a test, adding up the total points of the currentTeam's pieces versus it's enemy, it's able to calculate an evaluation for the quality of a move.
-> the position of a piece is also related to the amount of points it is worth, as pieces are more powerful in the center of the board, making those moves more preferred.

Things to touch up/improve :
King castling does not work as intended, as it's difficult to track whether the king has been in check in one section of its "future-moves tree" and revert that variable back to its prior state.
Promotion for the AI does not have a choice, as it's forced into a queen
Separate methods for move() and testMove() whereas I can attempt to combine and simplify these into one method. Same with attack() and testAttack(), though it requires me to edit the entire system.
AI does not know how to play endgame, and to look for checkmate

Not sure, but at one point the AI was strong, and was doing great moves, whereas right now even by editing the depth it seems like it's not performing optimally?


Reflection:
A lot of the process that goes into creating something like this is debugging. Many times I thought I was close to completing my project, but a flaw in my code produced a bug during my play. The counters to this is simplifying your system. By simplifying the game process, you're able to reduce the number of complications that will result in a bug, This also helps with readability, as many times you'd have to review your code. Logs and print statements are also great for debugging, and as soon as you find an inconsistency, you're able to inch closer to the main issue. 
^ I understand what people mean when, it usually takes 3x longer than what you intend, just because of the trouble you'd face trying to debug.

Coming from apcs, I think I've improved a lot in problem solving, and coming up with unique solutions. Since most of the project was from scratch, I had to do things my own way and get over certain barriers through my own brainstorming. An example is how I circumvented how a piece would remember the lastMoved piece. Because in a large tree (via minimax algorithm) you're going up and down different parts of the tree, so obviously the lastMoved piece is constantly changing over that. The solution is to make a global arraylist that holds a history of the latestMoved pieces, and this would kind of act as a history log. 
everytime a piece commited towards a move, it would just add itself to the end of the history log.
thus whenever it undoes that move, it just removes the end of the list

kind of simple as its just a history log, but i enjoyed how I had to figure that out myself. to save gameStates of variables over the tree via a stack (is what i learned what it was called). push and pop.

A game like chess also uses a lot of object-oriented java, since every child is representative of the greater "parent" class which is just basic inheritance, and polymorphism. 

The way I created the queen class was nice and simple, since all I had to do was give a queen its own Bishop, and Rook attributes, and those attributes would set its moves, and send it to the queen. it was an easy way to use the fact that a queen is really just all the possible moves of a rook and bishop combined. 

may possibily work on this again? but for now, a bit of a bust
