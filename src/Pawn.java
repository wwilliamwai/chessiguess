import java.util.ArrayList;
import javax.swing.*;
import processing.core.PApplet;

public class Pawn extends Piece {
    private Piece promoted;
    private boolean hasMoved;
    private int verticalUnits;
    public Pawn(int x, int y, boolean w, boolean whitePlayer, PApplet window) {
        super(x, y, w, whitePlayer);
        name = "pawn";
        promoted = null;
        hasMoved = false;
        setVerticalUnits();
        setAndLoadImage(window);
    }
    public void setVerticalUnits() {
        if (isPieceWhite == isWhitePlayer) {
            verticalUnits = -100;
        } else verticalUnits = 100;
    }
    public void setAndLoadImage(PApplet window) {
        if (isPieceWhite) {
            imageLink = "chesspieces/whitePawn.png";
        } else imageLink = "chesspieces/blackPawn.png";
        actualImage = window.loadImage(imageLink,"png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, PApplet window) {
        System.out.println("piece is moving");
        previousPosition = getPosition();
        // meaning there's literally an enemy piece on the square we're advancing to
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            System.out.println("there is a piece moving which we kill");
            attack(nextPos, enemyPieces);
        } else if (nextPos[0] != xPos) {
            // otherwise it's just an enpassant scenario if you're moving diagonally and thhere's no piece there
            int[] enPassantKilled = new int[2];
            enPassantKilled[0] = nextPos[0];
            enPassantKilled[1] = nextPos[1] - verticalUnits;
            System.out.println("there is a piece moving which we kill");
            attack(enPassantKilled, enemyPieces);
        }
        xPos = nextPos[0];
        yPos = nextPos[1];
        preTestPosition = getPosition();
        if (nextPos[1] == 0 || nextPos[1] == 700) {
            // if the piece is the ai
            if (isPieceWhite != isWhitePlayer) {
                aiPromote(piecesInPlay, window);
            } else promote(piecesInPlay, window);
        }
        clearFutureMoves();
        hasMoved = true;
        isMovesAlreadySet = false;
    }
    public Piece testMove(int[] nextPos, ArrayList<Piece> enemyPieces) {
        Piece killed = null;
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            killed = testAttack(nextPos, enemyPieces);
        } else if (nextPos[0] != xPos) {
            int[] enPassantKilled = new int[2];
            enPassantKilled[0] = nextPos[0];
            enPassantKilled[1] = nextPos[1] - verticalUnits;
            killed = testAttack(enPassantKilled, enemyPieces);
        }
        xPos = nextPos[0];
        yPos = nextPos[1];
        return killed;
    }
    public void revertTest(Piece killed, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        super.revertTest(killed, piecesInPlay, enemyPieces);
        if (promoted != null) {
            piecesInPlay.remove(promoted);
        }
    }
    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        // so the piece doesn't keep adding moves to its arrayList when the board updates
        if (isMovesAlreadySet) {
            return;
        }

        int[] oneForward = createVerticalMove(verticalUnits);
        // if the move is doable then it gets added to the list
        if (isMoveOpen(oneForward, piecesInPlay) && isMoveOpen(oneForward, enemyPieces)) {
            // check if it will put the pawn incheck
            futureMoves.add(oneForward);
            // should only double check if the 2 forward is possible if the 1 forward move is possible
            if (!hasMoved) {
                int[] twoForward = createVerticalMove(verticalUnits * 2);
                if (isMoveOpen(twoForward, piecesInPlay) && isMoveOpen(twoForward, enemyPieces)) {
                    futureMoves.add(twoForward);
                }
            }
        }
        // double checks the diagonals for attacking moves
        int[] firstDiagonal = createDiagonalMove(-100, verticalUnits);
        int[] secondDiagonal = createDiagonalMove(100, verticalUnits);
        // double checks if the first diagonal works
        if (!isMoveOpen(firstDiagonal, enemyPieces)) {
            futureMoves.add(firstDiagonal);
        }
        if (!isMoveOpen(secondDiagonal, enemyPieces)) {
            futureMoves.add(secondDiagonal);
        }
        isMovesAlreadySet = true;
    }
    public int[] createDiagonalMove(int horizontalUnits, int verticalUnits) {
        int[] diagonalMove = new int[2];
        diagonalMove[0] = xPos + horizontalUnits;
        diagonalMove[1] = yPos + verticalUnits;
        return diagonalMove;
    }
    public int[] createVerticalMove(int verticalUnits) {
        int[] forwardMove = new int[2];
        forwardMove[0] = xPos;
        forwardMove[1] = yPos + verticalUnits;
        return forwardMove;
    }
    public void filterMovesInCheck(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Board gameBoard, Piece lastMoved) {
        super.filterMovesInCheck(piecesInPlay, enemyPieces, gameBoard, lastMoved);
        // if the conditions are correct. (the last moved is  pawn who moved 2 squares up, and this pawn is now side to side with the other pawn( they
        // had to have moed 3 previously) then it returns true
        // then it checks if doing enpassant will put the board in check, then adds the diagonal move to its options.
        if (isEnPassantConditionsCorrect(lastMoved)) {
            int[] enPassantPos = new int[2];

            enPassantPos[0] = lastMoved.xPos;
            enPassantPos[1] = lastMoved.yPos + verticalUnits;

            if (!isEnPassantInCheck(enPassantPos, lastMoved, enemyPieces, gameBoard)) {
                futureMoves.add(enPassantPos);
            }
        }
    }
    // you want to check if en passant is doable
    // conditions: (enemy pawn had to have moved two spaces to be next to you by the last turn). (you had to have already been 3 spaces up from your start)
    // check if doing en passant will put you in check.
    // maybe place it in the filter method so it's easier to code? since it already has access to the board
    public boolean isEnPassantConditionsCorrect(Piece lastMoved) {
        // asks if the lastMoved piece is  pawn, if their last moved position is 200 y units away from their current one
        // if their y pos is the same as the current pawn rn, and if the x pos is 100 untis to the side of the ucrrent pawn
        return lastMoved.name.equals("pawn") && (lastMoved.getPreviousPosition()[1] == lastMoved.getyPos() - 200
                || lastMoved.getPreviousPosition()[1] == lastMoved.getyPos() + 200) && lastMoved.getyPos() == yPos &&
                (lastMoved.getxPos() == xPos + 100 || lastMoved.getxPos() == xPos - 100);
    }
    public boolean isEnPassantInCheck(int[] enPassantPos, Piece lastMoved, ArrayList<Piece> enemyPieces, Board gameBoard) {
        boolean wasInCheck;
        int[] initialPos = getPosition();

        testMove(enPassantPos, enemyPieces);
        enemyPieces.remove(lastMoved);

        wasInCheck = gameBoard.isBoardInCheck();

        testMove(initialPos, enemyPieces);
        enemyPieces.add(lastMoved);

        return wasInCheck;
    }
    public void promote(ArrayList<Piece> piecesInPlay, PApplet window) {
        piecesInPlay.remove(this);
        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        int selection = JOptionPane.showOptionDialog(null, "Select what to promote to", "Select one:", 0, 3, null, options, options[0]);
        if (selection == 0) {
            piecesInPlay.add(new Queen(xPos, yPos, isPieceWhite, isWhitePlayer,window));
        }
        if (selection == 1) {
            piecesInPlay.add(new Rook(xPos, yPos, isPieceWhite, isWhitePlayer,window));
        }
        if (selection == 2) {
            piecesInPlay.add(new Bishop(xPos, yPos, isPieceWhite, isWhitePlayer,window));
        }
        if (selection == 3) {
            piecesInPlay.add(new Knight(xPos, yPos, isPieceWhite, isWhitePlayer,window));
        }
        // spawn in a new piece kill off the original pawn, and that's allyou need to do. nothing more. nothing less. and im sure it will wori. and then just
        // make it so you run this method whenenver a pawn makes it to the last square in the column. doesn't matter if its white or black. if its a pawn and it
        // makes it to the last square it will promote.
    }
    public void aiPromote(ArrayList<Piece> piecesInPlay, PApplet window) {
        piecesInPlay.remove(this);
        // it just auto promotes to a queen but don't think the ai takes promotions into account when they make a move...
        piecesInPlay.add(new Queen(xPos, yPos, isPieceWhite, isWhitePlayer, window));
    }
}
