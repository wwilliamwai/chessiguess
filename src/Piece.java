import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Piece {
    protected String name;
    protected boolean isMovesAlreadySet;
    protected ArrayList<int[]> futureMoves;
    protected int xPos;
    protected int yPos;
    protected boolean white;
    protected String imageLink;
    protected PImage actualImage;

    public Piece(int x, int y, boolean w) {
        name = "piece";
        xPos = x;
        yPos = y;
        white = w;
        futureMoves = new ArrayList<>();
        isMovesAlreadySet = false;
        imageLink = new String("");
        actualImage = new PImage();
    }
    public void setAndLoadImage(PApplet window) {}
    public void draw(PApplet window) {
        window.image(actualImage, xPos, yPos);
    }

    public void move(int[] nextPos, ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, PApplet window) {
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            attack(nextPos, piecesInPlay);
        }
        xPos = nextPos[0];
        yPos = nextPos[1];
        clearFutureMoves();
        isMovesAlreadySet = false;
    }

    public void attack(int[] nextPos, ArrayList<Piece> enemyPieces) {
        Iterator<Piece> iterator = enemyPieces.iterator();
        while (iterator.hasNext()) {
            Piece enemy = iterator.next();
            if (enemy.getxPos() == nextPos[0] && enemy.getyPos() == nextPos[1]) {
                iterator.remove();
            }
        }
    }
    public Piece testMove(int[] nextPos, ArrayList<Piece> enemyPieces) {
        Piece killed = null;
        if (isMoveAnAttack(nextPos, enemyPieces)) {
            killed = testAttack(nextPos, enemyPieces);
        }
        xPos = nextPos[0];
        yPos = nextPos[1];
        return killed;
    }
    public Piece testAttack(int[] nextPos, ArrayList<Piece> enemyPieces) {
        Iterator<Piece> iterator = enemyPieces.iterator();
        while (iterator.hasNext()) {
            Piece enemy = iterator.next();
            if (enemy.getxPos() == nextPos[0] && enemy.getyPos() == nextPos[1]) {
                iterator.remove();
                return enemy;
            }
        }
        // otherwise if u don't kill anyone it just returns null;
        return null;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int[] getPosition() {
        int[] position = new int[2];
        position[0] = xPos;
        position[1] = yPos;
        return position;
    }

    public ArrayList<int[]> getFutureMoves() {
        return futureMoves;
    }

    public void printFutureMoves() {
        for (int[] futureMove : futureMoves) {
            System.out.println(Arrays.toString(futureMove));
        }
    }

    public void setFutureMoves(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
    }

    public void clearFutureMoves() {
        futureMoves.clear();
    }

    public void unsetMovesAlreadySet() {
        isMovesAlreadySet = false;
    }

    public boolean isMoveAnAttack(int[] nextPos, ArrayList<Piece> enemyPieces) {
        for (Piece enemy : enemyPieces) {
            if (Arrays.equals(nextPos, enemy.getPosition())) {
                return true;
            }
        }
        return false;
    }

    // only used for pieces OTHER THAN PAWN'S. (they aren't allowed to grab a piece even if its in their movement. only diagonal)
    public boolean hasMoveConflict(int[] nextMove, ArrayList<Piece> piecesInPlay,
                                   ArrayList<Piece> enemyPieces) {
        // checks if its next move is conflicting on a teammate piece
        if (!isMoveOpen(nextMove, piecesInPlay)) {
            return true;
        }
        // checks if its next move is conflicting on an enemy piece
        if (!isMoveOpen(nextMove, enemyPieces)) {
            // if its not in check add that move cuz it's allowed
            futureMoves.add(nextMove);
            // stop the move serach loop
            return true;
        }
        // checks if its next move is conflicting by not being in bounds
        if (nextMove[0] >= 0 && nextMove[0] < 800 && nextMove[1] >= 0 && nextMove[1] < 800) {
            futureMoves.add(nextMove);
            return false;

        } else return true;
    }
    public boolean isMoveOpen(int[] move, ArrayList<Piece> otherPieces) {
        for (Piece otherP: otherPieces) {
            int[] otherPiecePosition = otherP.getPosition();
            if (Arrays.equals(otherPiecePosition,move)) {
                return false;
            }
        }
        return true;
    }
    // use of aliveTeamPieces looks useless but its used in the king version of the move.
    public void filterMovesInCheck(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces, Board gameBoard, Piece lastMoved) {
        int[] initialPos = getPosition();
        for (int i = 0; i < futureMoves.size(); i++) {
            Piece killedPiece = testMove(futureMoves.get(i), enemyPieces);
            if (gameBoard.isBoardInCheck()) {
                futureMoves.remove(i);
                i--;
            }
            // reverts the piece back to its original position after teh test
            if (killedPiece != null) {
                enemyPieces.add(killedPiece);
            }
            testMove(initialPos, enemyPieces);
        }
    }
    public void printName() {
        System.out.println(name);
    }
    // technically only works for a few pieces but is just here for using the child version if it ever asks.
    public boolean getHasMoved() {
        return true;
    }
    // technically only works for a few pieces but is just here for using the child version if it ever asks.
    public void setHasBeenInCheck(boolean trueOrFalse) {
    }
    // technically only works for a few pieces but is just here for using the child version if it ever asks.
    public int[] getPreviousPosition() {
        return new int[2];
    }
}
