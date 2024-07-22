import processing.core.PApplet;

import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies, PApplet window, Board gameBoard) {
        super(x,y,isWhite,whitePlayer, teammates, enemies, window, gameBoard);
        name = "bishop";
        setAndLoadImage();
    }
    public void setPositionValues() {
        positionValues = new int[][] {
                {-2, -1, -1, -1, -1, -1, -1, -2},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-1, 0, 1, 1, 1, 1, 0, -1},
                {-1, 0, 1, 1, 1, 1, 0, -1},
                {-1, 0, 1, 1, 1, 1, 0, -1},
                {-1, 0, 1, 1, 1, 1, 0, -1},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-2, -1, -1, -1, -1, -1, -1, -2}
        };
    }
    public void setAndLoadImage() {
        if (isPieceWhite) {
            imageLink = "images/Chess_blt45.svg.png";
        } else imageLink = "images/Chess_bdt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void setFutureMoves() {
        // so the piece doesn't keep adding moves to its arrayList when the board updates
        if (isMovesAlreadySet) {
            return;
        }
        // adds all possible top left diagonal moves
        createAndCheckDiagonal(-100, -100);
        // adds all possible top right diagonal moves
        createAndCheckDiagonal(100, -100);
        // adds all possible bottom left diagonal moves
        createAndCheckDiagonal(-100, 100);
        // adds all possible bottom right diagonal moves
        createAndCheckDiagonal(100, 100);

        isMovesAlreadySet = true;
    }
    public void createAndCheckDiagonal(int horizontalUnits, int verticalUnits) {
        int horizontalUnitsMultiplier = 1;
        int verticalUnitsMultiplier = 1;
        boolean isMoveConflicting = false;

        while(!isMoveConflicting) {
            int[] nextMove = new int[2];
            nextMove[0] = xPos + horizontalUnits * horizontalUnitsMultiplier;
            nextMove[1] = yPos + verticalUnits * verticalUnitsMultiplier;

            // hasMoveConflict() adds the move to its list of moves as well
            isMoveConflicting = hasMoveConflict(nextMove);
            // increases multiplier for next move
            horizontalUnitsMultiplier++;
            verticalUnitsMultiplier++;
        }
    }
}
