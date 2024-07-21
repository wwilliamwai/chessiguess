import processing.core.PApplet;

import java.util.ArrayList;

public class King extends Piece {
    private boolean hasMoved;
    private final ArrayList<Boolean> preTestHasMoved;
    private boolean hasBeenInCheck;
    private Piece shortCastleRook;
    private Piece longCastleRook;
    public King(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies, PApplet window, Board gameBoard) {
        super(x, y, isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        name = "king";
        hasMoved = false;
        preTestHasMoved = new ArrayList<>();
        hasBeenInCheck = false;
        setAndLoadImage();
    }
    public void setPositionValues() {
        positionValues = new int[][] {
                {-3, -4, -4, -5, -5, -4, -4, -3},
                {-3, -4, -4, -5, -5, -4, -4, -3},
                {-3, -4, -4, -5, -5, -4, -4, -3},
                {-3, -4, -4, -5, -5, -4, -4, -3},
                {-2, -3, -3, -4, -4, -3, -3, -2},
                {-1, -2, -2, -2, -2, -2, -2, -1},
                {2, 3, 0, 0, 0, 0, 3, 2},
                {2, 3, 1, 0, 0, 1, 3, 2}
        };
    }
    public void setAndLoadImage() {
        if (isPieceWhite) {
            imageLink = "chesspieces/whiteKing.png";
        } else imageLink = "chesspieces/blackKing.png";
        actualImage = window.loadImage(imageLink,"png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos) {
        previousPositions.add(getPosition());
        if (nextPos[0] == xPos + 200) {
            xPos = nextPos[0];
            yPos = nextPos[1];
            nextPos[0] = nextPos[0] - 100;
            shortCastleRook.move(nextPos);
        } else if (nextPos[0] == xPos - 200) {
            xPos = nextPos[0] - 100;
            yPos = nextPos[1];
            longCastleRook.move(nextPos);
        } else {
            xPos = nextPos[0];
            yPos = nextPos[1];
        }
        attack(getPosition());
        printPastAndFuturePosition();
        gameBoard.addLastMoved(this);

        shortCastleRook = null;
        longCastleRook = null;
        clearFutureMoves();
        isMovesAlreadySet = false;
        hasMoved = true;
    }
    public Piece testMove(int[] nextPos) {
        previousPositions.add(getPosition());
        Piece killed = null;
        if (isMoveAnAttack(nextPos)) {
            killed = testAttack(nextPos);
        }
        if (nextPos[0] == xPos + 200) {
            xPos = nextPos[0];
            yPos = nextPos[1];
            nextPos[0] = nextPos[0] - 100;
            if (shortCastleRook != null) {
                shortCastleRook.testMove(nextPos);
            }
        }
        if (nextPos[0] == xPos - 200) {
            xPos = nextPos[0] - 100;
            yPos = nextPos[1];
            if (longCastleRook != null) {
                longCastleRook.testMove(nextPos);
            }
        } else {
            xPos = nextPos[0];
            yPos = nextPos[1];
        }
        gameBoard.addLastMoved(this);
        preTestHasMoved.add(hasMoved);

        shortCastleRook = null;
        longCastleRook = null;

        hasMoved = true;

        return killed;
    }
    public void revertTest(Piece killed) {
        hasMoved = preTestHasMoved.remove(preTestHasMoved.size()-1);
        super.revertTest(killed);
        if (shortCastleRook != null) {
            shortCastleRook.revertTest(killed);
        }
        if (longCastleRook != null) {
            longCastleRook.revertTest(killed);
        }
        shortCastleRook = null;
        longCastleRook = null;
    }
    public void setFutureMoves() {
        if (isMovesAlreadySet) {
            return;
        }
        createAndCheckMove(0, -100);
        createAndCheckMove(0, 100);
        createAndCheckMove(-100, 0);
        createAndCheckMove(100, 0);
        createAndCheckMove(-100, -100);
        createAndCheckMove(100, -100);
        createAndCheckMove(-100, 100);
        createAndCheckMove(100, 100);
    }
    public void createAndCheckMove(int horizontalUnits, int verticalUnits) {
        int[] move = new int[2];
        move[0] = xPos + horizontalUnits;
        move[1] = yPos + verticalUnits;
        hasMoveConflict(move);
    }
    public void filterMovesInCheck() {
        super.filterMovesInCheck();
        if (!hasMoved && !hasBeenInCheck) {
            if (doesContainRightMove()) {
                createShortCastleMove();
            }
            if (doesContainLeftMove()) {
                createLongCastleMove();
            }
        }
    }
    // checks if the king passing through the square to right puts us in check or not because we filtered it
    // although we're not checking if its only because we will take an enemyPiece on that square, we check if there are any pieces between the king and there later anyways
    public boolean doesContainRightMove() {
        for (int[] futureMove: futureMoves) {
            if (futureMove[0] == getXPos() + 100 && futureMove[1] == getYPos()) {
                return true;
            }
        }
        return false;
    }
    public boolean doesContainLeftMove() {
        for (int[] futureMove: futureMoves) {
            if (futureMove[0] == getXPos() - 100 && futureMove[1] == getYPos()) {
                return true;
            }
        }
        return false;
    }
    public void createShortCastleMove() {
        // first we have to make sure circumstances are PERFECT for castling
        // we already have (has not been moved) (has not been in check) (doesn't pass through a check on the left)
        // make sure the king position after the castle is not in check. Make sure the rook hasn't moved yet either. make sure there aren't any pieces in between
        Piece rook = findShortCastleRook();
        if (isRookGoodConditions(rook) && !isAnyPieceTwoRight() && !isShortCastleInCheck(rook)) {
            shortCastleRook = rook;
            int[] shortCastleMove = new int[2];
            shortCastleMove[0] = getXPos() + 200;
            shortCastleMove[1] = getYPos();
            futureMoves.add(shortCastleMove);
        }

    }
    public void createLongCastleMove() {
        Piece rook = findLongCastleRook();
        if (isRookGoodConditions(rook) && !isAnyPieceThreeLeft() && !isLongCastleInCheck(rook)) {
            longCastleRook = rook;
            int[] longCastleMove = new int[2];
            longCastleMove[0] = getXPos() - 200;
            longCastleMove[1] = getYPos();
            futureMoves.add(longCastleMove);
        }
    }
    public Piece findShortCastleRook() {
        for (Piece teamPiece: teammates) {
            if (teamPiece.getXPos() == xPos + 300 && teamPiece.getYPos() == yPos && teamPiece.name.equals("rook")) {
                return teamPiece;
            }
        }
        return null;
    }
    public Piece findLongCastleRook() {
        for (Piece teamPiece: teammates) {
            if (teamPiece.getXPos() == xPos - 400 && teamPiece.getYPos() == yPos && teamPiece.name.equals("rook")) {
                return teamPiece;
            }
        }
        return null;
    }
    public boolean isRookGoodConditions(Piece rook) {
        return rook != null && !rook.getHasMoved();
    }
    public boolean isAnyPieceTwoRight() {
        for (Piece teamPiece: teammates) {
            if ( (teamPiece.getXPos() == xPos + 100 && teamPiece.getYPos() == yPos) || (teamPiece.getXPos() == xPos + 200 && teamPiece.getYPos() == yPos) ) {
                return true;
            }
        }
        for (Piece enemy: enemies) {
            if ( (enemy.getXPos() == xPos + 100 && enemy.getYPos() == yPos) || (enemy.getXPos() == xPos + 200) && enemy.getYPos() == yPos) {
                return true;
            }
        }
        return false;
    }
    public boolean isAnyPieceThreeLeft() {
        for (Piece teamPiece: teammates) {
            if ( (teamPiece.getXPos() == xPos - 100 && teamPiece.getYPos() == yPos) || (teamPiece.getXPos() == xPos - 200 && teamPiece.getYPos() == yPos)
                    || (teamPiece.getXPos() == xPos - 300 && teamPiece.getYPos() == yPos)) {
                return true;
            }
        }
        for (Piece enemy: enemies) {
            if ( (enemy.getXPos() == xPos - 100 && enemy.getYPos() == yPos) || (enemy.getXPos() == xPos - 200 && enemy.getYPos() == yPos)
                    || (enemy.getXPos() == xPos - 300 && enemy.getYPos() == yPos)) {
                return true;
            }
        }
        return false;
    }
    public boolean isShortCastleInCheck(Piece rook) {
        boolean wasInCheck;
        int[] futureKingPos = new int[2];
        int[] futureRookPos = new int[2];
        futureKingPos[0] = xPos + 200;
        futureKingPos[1] = yPos;
        futureRookPos[0] = xPos + 100;
        futureRookPos[1] = yPos;

        testMove(futureKingPos);
        rook.testMove(futureRookPos);

        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null);
        rook.revertTest(null);

        return wasInCheck;
    }
    public boolean isLongCastleInCheck(Piece rook) {
        boolean wasInCheck;
        int[] futureKingPos = new int[2];
        int[] futureRookPos = new int[2];
        futureKingPos[0] = xPos - 300;
        futureKingPos[1] = yPos;
        futureRookPos[0] = xPos + 200;
        futureRookPos[1] = yPos;

        if (isTwoLeftInCheck()) {
            return true;
        }

        testMove(futureKingPos);
        rook.testMove(futureRookPos);

        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null);
        rook.revertTest(null);

        return wasInCheck;
    }
    public boolean isTwoLeftInCheck() {
        boolean wasInCheck;
        int[] twoLeft = new int[2];
        twoLeft[0] = xPos - 200;
        twoLeft[1] = yPos;
        testMove(twoLeft);
        wasInCheck = gameBoard.isBoardInCheck();
        // revert back the test moves
        revertTest(null);

        return wasInCheck;

    }
    public void setHasBeenInCheck(boolean yesOrNo) {
        hasBeenInCheck = yesOrNo;
    }
}
