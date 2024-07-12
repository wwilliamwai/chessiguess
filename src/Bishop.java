import processing.core.PApplet;

import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(int x, int y, boolean w, PApplet window) {
        super(x,y,w);
        name = "bishop";
        setAndLoadImage(window);
    }
    public void setAndLoadImage(PApplet window) {
        if (white) {
            imageLink = "images/Chess_blt45.svg.png";
        } else imageLink = "images/Chess_bdt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        // so the piece doesn't keep adding moves to its arrayList when the board updates
        if (isMovesAlreadySet) {
            return;
        }
        // adds all possible top left diagonal moves
        createAndCheckDiagonal(-100, -100, piecesInPlay, enemyPieces);
        // adds all possible top right diagonal moves
        createAndCheckDiagonal(100, -100, piecesInPlay, enemyPieces);
        // adds all possible bottom left diagonal moves
        createAndCheckDiagonal(-100, 100, piecesInPlay, enemyPieces);
        // adds all possible bottom right diagonal moves
        createAndCheckDiagonal(100, 100, piecesInPlay, enemyPieces);

        isMovesAlreadySet = true;
    }
    public void createAndCheckDiagonal(int horizontalUnits, int verticalUnits, ArrayList<Piece> piecesInPlay,
                                       ArrayList<Piece> enemyPieces) {
        int horizontalUnitsMultiplier = 1;
        int verticalUnitsMultiplier = 1;
        boolean isMoveConflicting = false;

        while(!isMoveConflicting) {
            int[] nextMove = new int[2];
            nextMove[0] = xPos + horizontalUnits * horizontalUnitsMultiplier;
            nextMove[1] = yPos + verticalUnits * verticalUnitsMultiplier;

            // hasMoveConflict() adds the move to its list of moves as well
            isMoveConflicting = hasMoveConflict(nextMove, piecesInPlay, enemyPieces);
            // increases multiplier for next move
            horizontalUnitsMultiplier++;
            verticalUnitsMultiplier++;
        }
    }
}
