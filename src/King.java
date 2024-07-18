import processing.core.PApplet;

import java.util.ArrayList;

public class King extends Piece {
    private boolean hasMoved;
    private boolean hasBeenInCheck;
    private Piece shortCastleRook;
    private Piece longCastleRook;
    public King(int x, int y, boolean w, boolean whitePlayer, PApplet window) {
        super(x, y, w, whitePlayer);
        name = "king";
        hasMoved = false;
        hasBeenInCheck = false;
        setAndLoadImage(window);
    }
    public void setAndLoadImage(PApplet window) {
        if (isPieceWhite) {
            imageLink = "chesspieces/whiteKing.png";
        } else imageLink = "chesspieces/blackKing.png";
        actualImage = window.loadImage(imageLink,"png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, PApplet window) {
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            attack(nextPos, enemyPieces);
        }
        if (nextPos[0] == xPos + 200) {
            xPos = nextPos[0];
            yPos = nextPos[1];
            nextPos[0] = nextPos[0] - 100;
            shortCastleRook.move(nextPos, piecesInPlay, enemyPieces, window);
        } else if (nextPos[0] == xPos - 200) {
            xPos = nextPos[0] - 100;
            yPos = nextPos[1];
            longCastleRook.move(nextPos, piecesInPlay, enemyPieces, window);
        } else {
            xPos = nextPos[0];
            yPos = nextPos[1];
        }
        preTestPosition = getPosition();
        shortCastleRook = null;
        longCastleRook = null;
        clearFutureMoves();
        isMovesAlreadySet = false;
        hasMoved = true;
    }
    public Piece testMove(int[] nextPos, ArrayList<Piece> enemyPieces) {
        Piece killed = null;
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            killed = testAttack(nextPos, enemyPieces);
        }
        if (nextPos[0] == xPos + 200) {
            xPos = nextPos[0];
            yPos = nextPos[1];
            nextPos[0] = nextPos[0] - 100;
            if (shortCastleRook != null) {
                shortCastleRook.testMove(nextPos, enemyPieces);
            }
        }
        if (nextPos[0] == xPos - 200) {
            xPos = nextPos[0] - 100;
            yPos = nextPos[1];
            if (longCastleRook != null) {
                longCastleRook.testMove(nextPos, enemyPieces);
            }
        } else {
            xPos = nextPos[0];
            yPos = nextPos[1];
        }
        return killed;
    }
    public void revertTest(Piece killed, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        super.revertTest(killed, piecesInPlay, enemyPieces);
        if (shortCastleRook != null) {
            shortCastleRook.revertTest(killed, piecesInPlay, enemyPieces);
        }
        if (longCastleRook != null) {
            longCastleRook.revertTest(killed, piecesInPlay, enemyPieces);
        }
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        if (isMovesAlreadySet) {
            return;
        }
        createAndCheckMove(0, -100, piecesInPlay, enemyPieces);
        createAndCheckMove(0, 100, piecesInPlay, enemyPieces);
        createAndCheckMove(-100, 0, piecesInPlay, enemyPieces);
        createAndCheckMove(100, 0, piecesInPlay, enemyPieces);
        createAndCheckMove(-100, -100, piecesInPlay, enemyPieces);
        createAndCheckMove(100, -100, piecesInPlay, enemyPieces);
        createAndCheckMove(-100, 100, piecesInPlay, enemyPieces);
        createAndCheckMove(100, 100, piecesInPlay, enemyPieces);
    }
    public void createAndCheckMove(int horizontalUnits, int verticalUnits, ArrayList<Piece> piecesInPlay,
                                   ArrayList<Piece> enemyPieces) {
        int[] move = new int[2];
        move[0] = xPos + horizontalUnits;
        move[1] = yPos + verticalUnits;
        hasMoveConflict(move, piecesInPlay, enemyPieces);
    }
    public void filterMovesInCheck(ArrayList<Piece> enemyPieces, ArrayList<Piece> piecesInPlay, Board gameBoard, Piece lastMoved) {
        super.filterMovesInCheck(enemyPieces, piecesInPlay, gameBoard, lastMoved);
        if (!hasMoved && !hasBeenInCheck) {
            if (doesContainRightMove()) {
                createShortCastleMove(enemyPieces, piecesInPlay, gameBoard);
            }
            if (doesContainLeftMove()) {
                createLongCastleMove(enemyPieces, piecesInPlay, gameBoard);
            }
        }
    }
    // checks if the king passing through the square to right puts us in check or not because we filtered it
    // although we're not checking if its only because we will take an enemyPiece on that square, we check if there are any pieces between the king and there later anyways
    public boolean doesContainRightMove() {
        for (int[] futureMove: futureMoves) {
            if (futureMove[0] == getxPos() + 100 && futureMove[1] == getyPos()) {
                return true;
            }
        }
        return false;
    }
    public boolean doesContainLeftMove() {
        for (int[] futureMove: futureMoves) {
            if (futureMove[0] == getxPos() - 100 && futureMove[1] == getyPos()) {
                return true;
            }
        }
        return false;
    }
    public void createShortCastleMove(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Board gameBoard) {
        // first we have to make sure circumstances are PERFECT for castling
        // we already have (has not been moved) (has not been in check) (doesn't pass through a check on the left)
        // make sure the king position after the castle is not in check. Make sure the rook hasn't moved yet either. make sure there aren't any pieces in between
        Piece rook = findShortCastleRook(piecesInPlay);
        if (isRookGoodConditions(rook) && !isAnyPieceTwoRight(enemyPieces, piecesInPlay) && !isShortCastleInCheck(piecesInPlay, enemyPieces, rook, gameBoard)) {
            shortCastleRook = rook;
            int[] shortCastleMove = new int[2];
            shortCastleMove[0] = getxPos() + 200;
            shortCastleMove[1] = getyPos();
            futureMoves.add(shortCastleMove);
        }

    }
    public void createLongCastleMove(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Board gameBoard) {
        Piece rook = findLongCastleRook(piecesInPlay);
        if (isRookGoodConditions(rook) && !isAnyPieceThreeLeft(piecesInPlay, enemyPieces) && !isLongCastleInCheck(piecesInPlay, enemyPieces, rook, gameBoard)) {
            longCastleRook = rook;
            int[] longCastleMove = new int[2];
            longCastleMove[0] = getxPos() - 200;
            longCastleMove[1] = getyPos();
            futureMoves.add(longCastleMove);
        }
    }
    public Piece findShortCastleRook(ArrayList<Piece> piecesInPlay) {
        for (Piece teamPiece: piecesInPlay) {
            if (teamPiece.getxPos() == xPos + 300 && teamPiece.getyPos() == yPos && teamPiece.name.equals("rook")) {
                return teamPiece;
            }
        }
        return null;
    }
    public Piece findLongCastleRook(ArrayList<Piece> piecesInPlay) {
        for (Piece teamPiece: piecesInPlay) {
            if (teamPiece.getxPos() == xPos - 400 && teamPiece.getyPos() == yPos && teamPiece.name.equals("rook")) {
                return teamPiece;
            }
        }
        return null;
    }
    public boolean isRookGoodConditions(Piece rook) {
        return rook != null && !rook.getHasMoved();
    }
    public boolean isAnyPieceTwoRight(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        for (Piece enemy: enemyPieces) {
            if ( (enemy.getxPos() == xPos + 100 && enemy.getyPos() == yPos) || (enemy.getxPos() == xPos + 200) && enemy.getyPos() == yPos) {
                return true;
            }
        }
        for (Piece teamPiece: piecesInPlay) {
            if ( (teamPiece.getxPos() == xPos + 100 && teamPiece.getyPos() == yPos) || (teamPiece.getxPos() == xPos + 200 && teamPiece.getyPos() == yPos) ) {
                return true;
            }
        }
        return false;
    }
    public boolean isAnyPieceThreeLeft(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        for (Piece enemy: enemyPieces) {
            if ( (enemy.getxPos() == xPos - 100 && enemy.getyPos() == yPos) || (enemy.getxPos() == xPos - 200 && enemy.getyPos() == yPos)
                    || (enemy.getxPos() == xPos - 300 && enemy.getyPos() == yPos)) {
                return true;
            }
        }
        for (Piece teamPiece: piecesInPlay) {
            if ( (teamPiece.getxPos() == xPos - 100 && teamPiece.getyPos() == yPos) || (teamPiece.getxPos() == xPos - 200 && teamPiece.getyPos() == yPos)
                    || (teamPiece.getxPos() == xPos - 300 && teamPiece.getyPos() == yPos)) {
                return true;
            }
        }
        return false;
    }
    public boolean isShortCastleInCheck(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Piece rook, Board gameBoard) {
        boolean wasInCheck;
        int[] futureKingPos = new int[2];
        int[] futureRookPos = new int[2];
        futureKingPos[0] = xPos + 200;
        futureKingPos[1] = yPos;
        futureRookPos[0] = xPos + 100;
        futureRookPos[1] = yPos;

        testMove(futureKingPos,enemyPieces);
        rook.testMove(futureRookPos, enemyPieces);

        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null, piecesInPlay, enemyPieces);
        rook.revertTest(null, piecesInPlay, enemyPieces);

        return wasInCheck;
    }
    public boolean isLongCastleInCheck(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Piece rook, Board gameBoard) {
        boolean wasInCheck;
        int[] futureKingPos = new int[2];
        int[] futureRookPos = new int[2];
        futureKingPos[0] = xPos - 300;
        futureKingPos[1] = yPos;
        futureRookPos[0] = xPos + 200;
        futureRookPos[1] = yPos;

        if (isTwoLeftInCheck(piecesInPlay, enemyPieces, gameBoard)) {
            return true;
        }

        testMove(futureKingPos,enemyPieces);
        rook.testMove(futureRookPos, enemyPieces);

        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null, piecesInPlay, enemyPieces);
        rook.revertTest(null, piecesInPlay, enemyPieces);

        return wasInCheck;
    }
    public boolean isTwoLeftInCheck(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Board gameBoard) {
        boolean wasInCheck;
        int[] twoLeft = new int[2];
        twoLeft[0] = xPos - 200;
        twoLeft[1] = yPos;
        testMove(twoLeft, enemyPieces);
        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null, piecesInPlay, enemyPieces);

        return wasInCheck;

    }
    public void setHasBeenInCheck(boolean yesOrNo) {
        hasBeenInCheck = yesOrNo;
    }
}
