import processing.core.PApplet;

import java.util.ArrayList;

public class Queen extends Piece {
    private final Bishop invisBishop;
    private final Rook invisRook;
    public Queen(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies, PApplet window, Board gameBoard) {
        super(x,y,isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        name = "queen";
        invisBishop = new Bishop(x, y, isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        invisRook = new Rook(x, y, isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        setAndLoadImage();
    }
    public void setPositionValues() {
        positionValues = new int[][] {
                {-2, -1, -1, -0, -0, -1, -1, -2},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-0, 0, 0, 0, 0, 0, 0, -0},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-1, 0, 0, 0, 0, 0, 0, -1},
                {-2, -1, -1, -0, -0, -1, -1, -2}
        };
    }
    public void setAndLoadImage() {
        if (isPieceWhite) {
            imageLink = "images/Chess_qlt45.svg.png";
        } else imageLink = "images/Chess_qdt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos) {
        preTestPositions.add(getPosition());
        attack(nextPos);
        xPos = nextPos[0];
        yPos = nextPos[1];
        printPastAndFuturePosition();
        gameBoard.addLastMoved(this);
        updateInvisPieces();
        isMovesAlreadySet = false;
    }
    public Piece testMove(int[] nextPos) {
        preTestPositions.add(getPosition());
        Piece killed = testAttack(nextPos);
        xPos = nextPos[0];
        yPos = nextPos[1];
        testUpdateInvisPieces();
        gameBoard.addLastMoved(this);
        return killed;
    }
    public void revertTest(Piece killed) {
        super.revertTest(killed);
        testUpdateInvisPieces();
    }
    public void setFutureMoves() {
        if (isMovesAlreadySet) {
            return;
        }
        // adds all possible top left diagonal moves
        invisBishop.createAndCheckDiagonal(-100, -100);
        // adds all possible top right diagonal moves
        invisBishop.createAndCheckDiagonal(100, -100);
        // adds all possible bottom left diagonal moves
        invisBishop.createAndCheckDiagonal(-100, 100);
        // adds all possible bottom right diagonal moves
        invisBishop.createAndCheckDiagonal(100, 100);

        // checks all moves going up
        invisRook.createAndCheckStraight(-100, true);
        // checks all moves going down
        invisRook.createAndCheckStraight(100, true);
        // checks all moves left
        invisRook.createAndCheckStraight(-100, false);
        // checks all moves right
        invisRook.createAndCheckStraight(100, false);

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
    public void testUpdateInvisPieces() {
        invisBishop.xPos = xPos;
        invisBishop.yPos = yPos;
        invisRook.xPos = xPos;
        invisRook.yPos = yPos;
    }
    public void updateInvisPieces() {
        invisBishop.xPos = xPos;
        invisBishop.yPos = yPos;
        invisBishop.clearFutureMoves();
        invisBishop.unsetMovesAlreadySet();
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
