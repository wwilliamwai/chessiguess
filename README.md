# chessiguess
Attempted to recreate an entire chess game from scratch using PApplet processing on java. 
All classes made from scratch, except for the Game class using the basic PApplet template

Also attempted to create an AI Chess bot that uses the minimax algorithm to process its optimal next move versus the player, along with alpha-beta pruning to prune wasteful/unnecessary trees to explore.
By examining the current state of a board during a test, adding up the total points of the currentTeam's pieces versus it's enemy, it's able to calculate an evaluation for the quality of a move.
-> the position of a piece is also related to the amount of points it is worth, as pieces are more powerful in the center of the board, making those moves more preferred.

**However it looks now, I unfortunately could not see the project through completely. **
It was taking too much time. There was just too many complications with making an entire chess system from scratch. Regardless of how well it worked with a two player system, once I introduced an AI bot it overcomplicated everything. I had to work towards saving gamestate variables, learning a new algorithm, and etc.
It was difficult to build upon my current system when it wasn't built for use in a minimax tree, which resulted in too many bugs and use of time to fix them.



Things to touch up/improve :
King castling does not work as intended, as it's difficult to track whether the king has been in check in one section of its "future-moves tree" and revert that variable back to its prior state.
Promotion for the AI does not have a choice, as it's forced into a queen
Separate methods for move() and testMove() whereas I can attempt to combine and simplify these into one method. Same with attack() and testAttack(), though it requires me to edit the entire system.
AI does not know how to play endgame, and to look for checkmate

Not sure, but at one point the AI was strong, and was doing great moves, whereas right now even by editing the depth to 3 or greater it seems like it's not performing optimally?
