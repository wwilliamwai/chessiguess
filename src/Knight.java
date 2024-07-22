import processing.core.PApplet;

import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies, PApplet window, Board gameBoard) {
        super(x,y,isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        name = "knight";
        setAndLoadImage();
    }
    public void setPositionValues() {
        positionValues = new int[][] {
                {-5, -4, -3, -3, -3, -3, -4, -5},
                {-4, -2, 0, 0, 0, 0, -2, -4},
                {-3, 0, 1, 1, 1, 1, 0, -3},
                {-3, 0, 1, 2, 2, 1, 0, -3},
                {-3, 0, 1, 2, 2, 1, 0, -3},
                {-3, 0, 1, 1, 1, 1, 0, -3},
                {-4, -2, 0, 0, 0, 0, -2, -4},
                {-5, -4, -3, -3, -3, -3, -4, -5}
        };
    }
    public void setAndLoadImage() {
        if (isPieceWhite) {
            imageLink = "images/Chess_nlt45.svg.png";
        } else imageLink = "images/Chess_ndt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void setFutureMoves() {
        if (isMovesAlreadySet) {
            return;
        }
        // check the upper vertical pair
        createAndCheckLPair(100, -200, true);
        // check the bottom vertical pair
        createAndCheckLPair(100, 200, true);
        // check the left horizontal pair
        createAndCheckLPair(-200, 100, false);
        // check the right horizontal pair
        createAndCheckLPair(200,100, false);

        isMovesAlreadySet = true;
    }
    public void createAndCheckLPair(int horizontalUnits, int verticalUnits, boolean isVertical) {
        int[] move1 = new int[2];
        int[] move2 = new int[2];

        if (isVertical) {
            move1[0] = xPos + horizontalUnits;
            move1[1] = yPos + verticalUnits;
            move2[0] = xPos - horizontalUnits;
            move2[1] = yPos + verticalUnits;

            hasMoveConflict(move1);
            hasMoveConflict(move2);
        } else {
            move1[0] = xPos + horizontalUnits;
            move1[1] = yPos + verticalUnits;
            move2[0] = xPos + horizontalUnits;
            move2[1] = yPos - verticalUnits;

            hasMoveConflict(move1);
            hasMoveConflict(move2);
        }
    }
}
