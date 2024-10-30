# chessiguess
Attempted to recreate an entire chess game from scratch using PApplet processing on java. 
All classes made from scratch, except for the Game class using the basic PApplet template

attempted to create AI Chess bot w/ minimax algorithm for optimal moves, alpha-beta pruning to prune wasteful/unnecessary trees
Examining current board state, adding up points of currentTeam's pieces versus it's enemy,  able to calculate evaluation for quality of a move.
-> the position of piece related to amount of pointsworth, more powerful in the center

However it looks now, I unfortunately could not see the project through completely. 
Took too much time. Many complications with making an entire chess system from scratch. Once I introduced an AI bot it overcomplicated the system. Had to work towards saving gamestate variables, learning a new algorithm, etc.
Difficult to build upon current system when wasn't built for use in a minimax tree



Things to touch up/improve :
King castling does not work as intended
Promotion for the AI does not have a choice, as it's forced into a queen
Separate methods for move() and testMove() whereas I can attempt to combine and simplify these into one method. Same with attack() and testAttack(), though it requires me to edit the entire system.
AI does not know how to play endgame, and to look for checkmate
AI does not perform optimal moves despite minimax algorithm looking correct?
