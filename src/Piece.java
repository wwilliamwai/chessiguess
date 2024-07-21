import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;

public class Piece {
    protected String name;
    protected int[][] positionValues;
    protected int xPos;
    protected int yPos;
    protected ArrayList<int[]> preTestPositions;
    protected int[] previousPosition;
    protected boolean isPieceWhite;
    protected boolean isPlayerWhite;
    protected ArrayList<int[]> futureMoves;
    protected boolean isMovesAlreadySet;
    ArrayList<Piece> teammates;
    ArrayList<Piece> enemies;
    protected String imageLink;
    protected PImage actualImage;

    public Piece(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies) {
        name = "piece";
        setPositionValues();
        xPos = x;
        yPos = y;
        preTestPositions = new ArrayList<>();
        previousPosition = getPosition();
        isPieceWhite = isWhite;
        isPlayerWhite = whitePlayer;
        futureMoves = new ArrayList<>();
        isMovesAlreadySet = false;
        this.teammates = teammates;
        this.enemies = enemies;
        imageLink = "";
        actualImage = new PImage();
    }
    public void setPositionValues() {
    }
    public void setAndLoadImage(PApplet window) {}
    public void draw(PApplet window) {
        window.image(actualImage, xPos, yPos);
    }

    public void move(int[] nextPos) {
        previousPosition = getPosition();
        attack(nextPos);
        xPos = nextPos[0];
        yPos = nextPos[1];
        printPastAndFuturePosition();
        clearFutureMoves();
        isMovesAlreadySet = false;
    }

    public void attack(int[] pos) {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (Arrays.equals(enemies.get(i).getPosition(), pos)) {
                System.out.println("Piece " + whatColor() + " " + name + " killed a " + enemies.get(i).whatColor() + " " + enemies.get(i).name);
                enemies.remove(i);
                return;
            }
        }
    }
    public Piece testMove(int[] nextPos) {
        preTestPositions.add(getPosition());
        Piece killed = testAttack(nextPos);
        xPos = nextPos[0];
        yPos = nextPos[1];
        return killed;
    }
    public Piece testAttack(int[] pos) {
        Piece killed = null;
        for (int i = enemies.size()-1; i >= 0; i--) {
            if (Arrays.equals(enemies.get(i).getPosition(), pos)) {
                killed = enemies.get(i);
                enemies.remove(i);
                return killed;
            }
        }
        return killed;
    }
    public void revertTest(Piece killed) {
        int[] preTest = preTestPositions.remove(preTestPositions.size()-1);
        xPos = preTest[0];
        yPos = preTest[1];
        if (killed != null) {
            enemies.add(killed);
        }
    }
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
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


    public void setFutureMoves() {
    }

    public void clearFutureMoves() {
        futureMoves.clear();
    }

    public void unsetMovesAlreadySet() {
        isMovesAlreadySet = false;
    }

    public boolean isMoveAnAttack(int[] nextPos) {
        for (Piece enemy : enemies) {
            if (Arrays.equals(nextPos, enemy.getPosition())) {
                return true;
            }
        }
        return false;
    }

    // only used for pieces OTHER THAN PAWN'S. (they aren't allowed to grab a piece even if its in their movement. only diagonal)
    public boolean hasMoveConflict(int[] nextMove) {
        // checks if its next move is conflicting on a teammate piece
        if (!isMoveOpen(nextMove, teammates)) {
            return true;
        }
        // checks if its next move is conflicting on an enemy piece
        if (!isMoveOpen(nextMove, enemies)) {
            // if its not in check add that move cuz it's allowed
            futureMoves.add(nextMove);
            // stop the move serach loop
            return true;
        }
        // checks if its next move is conflicting by not being in bounds
        if (nextMove[0] >= 0 && nextMove[0] < 800 && nextMove[1] >= 0 && nextMove[1] < 800) {
            futureMoves.add(nextMove);
            return false;

        }
        return true;
    }
    public boolean isMoveOpen(int[] move, ArrayList<Piece> otherPieces) {
        for (Piece other: otherPieces) {
            if (Arrays.equals(move, other.getPosition())) {
                return false;
            }
        }
        return true;
    }
    // use of aliveTeamPieces looks useless but its used in the king version of the move.
    public void filterMovesInCheck(Board gameBoard) {
        for (int i = futureMoves.size()-1; i >= 0; i--) {
            Piece killed = testMove(futureMoves.get(i));
            if (gameBoard.isBoardInCheck()) {
                futureMoves.remove(i);
            }
            revertTest(killed);
        }
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
        return previousPosition;
    }
    public void printPastAndFuturePosition() {
        System.out.println("Piece " + whatColor() + " " + name + " initially on position " + "[" + previousPosition[0] + ", "
                + previousPosition[1] + "] moved to position [" + getXPos() + ", " + getYPos() + "]");
    }
    public int getPositionValue() {
        try {
            return positionValues[xPos / 100][yPos / 100];
        } catch (Exception ex) {
            System.out.println("tried finding value of its position but instead ran " + ex + " at position " + xPos/100 + " " + yPos/100);
            return 0;
        }
    }
    public String whatColor() {
        if (isPieceWhite) {
            return "white";
        } else return "black";
    }
    public String toString() {
        return whatColor() + " " + name + " at position [" + xPos + ", " + yPos + "]";
    }
}
