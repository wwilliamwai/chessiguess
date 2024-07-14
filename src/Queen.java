import processing.core.PApplet;

import java.util.ArrayList;

public class Queen extends Piece {
    private Bishop invisBishop;
    private Rook invisRook;
    public Queen(int x, int y, boolean w, PApplet window) {
        super(x,y,w);
        name = "queen";
        invisBishop = new Bishop(x, y, w, window);
        invisRook = new Rook(x, y, w, window);
        setAndLoadImage(window);
    }
    public void setAndLoadImage(PApplet window) {
        if (white) {
            imageLink = "images/Chess_qlt45.svg.png";
        } else imageLink = "images/Chess_qdt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, PApplet window) {
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            attack(nextPos,enemyPieces);
        }
        xPos = nextPos[0];
        yPos = nextPos[1];
        updateInvisBishop();
        updateInvisRook();
        isMovesAlreadySet = false;
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        if (isMovesAlreadySet) {
            return;
        }
        // adds all possible top left diagonal moves
        invisBishop.createAndCheckDiagonal(-100, -100, piecesInPlay, enemyPieces);
        // adds all possible top right diagonal moves
        invisBishop.createAndCheckDiagonal(100, -100, piecesInPlay, enemyPieces);
        // adds all possible bottom left diagonal moves
        invisBishop.createAndCheckDiagonal(-100, 100, piecesInPlay, enemyPieces);
        // adds all possible bottom right diagonal moves
        invisBishop.createAndCheckDiagonal(100, 100, piecesInPlay, enemyPieces);

        // checks all moves going up
        invisRook.createAndCheckStraight(-100, true, piecesInPlay, enemyPieces);
        // checks all moves going down
        invisRook.createAndCheckStraight(100, true, piecesInPlay, enemyPieces);
        // checks all moves left
        invisRook.createAndCheckStraight(-100, false, piecesInPlay, enemyPieces);
        // checks all moves right
        invisRook.createAndCheckStraight(100, false, piecesInPlay, enemyPieces);

        sendBishopMoves();
        sendRookMoves();
        isMovesAlreadySet = true;
    }
    public void sendBishopMoves() {
        futureMoves.addAll(invisBishop.getFutureMoves());
    }
    public void sendRookMoves() {
        futureMoves.addAll(invisRook.getFutureMoves());
    }
    public void updateInvisBishop() {
        invisBishop.xPos = xPos;
        invisBishop.yPos = yPos;
        invisBishop.clearFutureMoves();
        invisBishop.unsetMovesAlreadySet();
    }
    public void updateInvisRook() {
        invisRook.xPos = xPos;
        invisRook.yPos = yPos;
        invisRook.clearFutureMoves();
        invisRook.unsetMovesAlreadySet();
    }
    public void clearFutureMoves() {
        futureMoves.clear();
        invisRook.clearFutureMoves();
        invisBishop.clearFutureMoves();
    }
    public void unsetMovesAlreadySet() {
        isMovesAlreadySet = false;
        invisRook.unsetMovesAlreadySet();
        invisBishop.unsetMovesAlreadySet();
    }
}