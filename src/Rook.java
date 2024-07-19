import processing.core.PApplet;

import java.util.ArrayList;

public class Rook extends Piece{
    private boolean hasMoved;
    public Rook(int x, int y, boolean isWhite, boolean whitePlayer, PApplet window) {
        super(x, y, isWhite, whitePlayer);
        name = "rook";
        hasMoved = false;
        setAndLoadImage(window);
    }
    public void setAndLoadImage(PApplet window) {
        if (isPieceWhite) {
            imageLink = "chesspieces/whiteRook.png";
        } else imageLink = "chesspieces/blackRook.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, PApplet window) {
        super.move(nextPos, piecesInPlay, enemyPieces, window);
        hasMoved = true;
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        // so the piece doesn't keep adding moves to its arrayList when the board updates
        if (isMovesAlreadySet) {
            return;
        }
        // checks all moves going up
        createAndCheckStraight(-100, true, piecesInPlay, enemyPieces);
        // checks all moves going down
        createAndCheckStraight(100, true, piecesInPlay, enemyPieces);
        // checks all moves left
        createAndCheckStraight(-100, false, piecesInPlay, enemyPieces);
        // checks all moves right
        createAndCheckStraight(100, false, piecesInPlay, enemyPieces);

        isMovesAlreadySet = true;

    }
    public void createAndCheckStraight(int directionalUnits, boolean isVertical, ArrayList<Piece> piecesInPlay,
                                           ArrayList<Piece> enemyPieces) {
        int directionalUnitMultiplier = 1;
        boolean isMoveConflicting = false;

        while (!isMoveConflicting) {
            int[] nextMove = new int[2];
            if (isVertical) {
                nextMove[0] = xPos;
                nextMove[1] = yPos + directionalUnits * directionalUnitMultiplier;

                isMoveConflicting = hasMoveConflict(nextMove, piecesInPlay, enemyPieces);

                directionalUnitMultiplier++;
            } else {
                nextMove[0] = xPos + directionalUnits * directionalUnitMultiplier;
                nextMove[1] = yPos;

                isMoveConflicting = hasMoveConflict(nextMove, piecesInPlay, enemyPieces);

                directionalUnitMultiplier++;
            }
        }
    }
    public boolean getHasMoved() {
        return hasMoved;
    }
}
