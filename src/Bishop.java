import processing.core.PApplet;

import java.util.ArrayList;

public class Bishop extends Piece{
    public Bishop(int x, int y, boolean isWhite, boolean whitePlayer, PApplet window, ArrayList<Piece> teammates, ArrayList<Piece> enemies) {
        super(x,y,isWhite,whitePlayer, teammates, enemies);
        name = "bishop";
        setAndLoadImage(window);
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
    public void setAndLoadImage(PApplet window) {
        if (isPieceWhite) {
            imageLink = "chesspieces/whiteBishop.png";
        } else imageLink = "chesspieces/blackBishop.png";
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
