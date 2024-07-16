import processing.core.PApplet;

import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(int x, int y, boolean w, boolean whitePlayer, PApplet window) {
        super(x,y,w, whitePlayer);
        name = "knight";
        setAndLoadImage(window);
    }
    public void setAndLoadImage(PApplet window) {
        if (isPieceWhite) {
            imageLink = "chesspieces/whiteKnight.png";
        } else imageLink = "chesspieces/blackKnight.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        if (isMovesAlreadySet) {
            return;
        }
        // check the upper vertical pair
        createAndCheckLPair(100, -200, true, piecesInPlay, enemyPieces);
        // check the bottom vertical pair
        createAndCheckLPair(100, 200, true, piecesInPlay, enemyPieces);
        // check the left horizontal pair
        createAndCheckLPair(-200, 100, false, piecesInPlay, enemyPieces);
        // check the right horizontal pair
        createAndCheckLPair(200,100, false, piecesInPlay, enemyPieces);

        isMovesAlreadySet = true;
    }
    public void createAndCheckLPair(int horizontalUnits, int verticalUnits, boolean isVertical, ArrayList<Piece> piecesInPlay,
                                ArrayList<Piece> enemyPieces) {
        if (isVertical) {
            int[] move1 = new int[2];
            int[] move2 = new int[2];

            move1[0] = xPos + horizontalUnits;
            move1[1] = yPos + verticalUnits;
            move2[0] = xPos - horizontalUnits;
            move2[1] = yPos + verticalUnits;

            hasMoveConflict(move1, piecesInPlay, enemyPieces);
            hasMoveConflict(move2, piecesInPlay, enemyPieces);
        } else {
            int[] move1 = new int[2];
            int[] move2 = new int[2];

            move1[0] = xPos + horizontalUnits;
            move1[1] = yPos + verticalUnits;
            move2[0] = xPos + horizontalUnits;
            move2[1] = yPos - verticalUnits;

            hasMoveConflict(move1, piecesInPlay, enemyPieces);
            hasMoveConflict(move2, piecesInPlay, enemyPieces);
        }
    }
}
