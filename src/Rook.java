import processing.core.PApplet;

import java.util.ArrayList;

public class Rook extends Piece{
    private boolean hasMoved;
    private final ArrayList<Boolean> preTestHasMoved;
    public Rook(int x, int y, boolean isWhite, boolean whitePlayer, ArrayList<Piece> teammates, ArrayList<Piece> enemies, PApplet window, Board gameBoard) {
        super(x, y, isWhite, whitePlayer, teammates, enemies, window, gameBoard);
        name = "rook";
        hasMoved = false;
        preTestHasMoved = new ArrayList<>();
        setAndLoadImage();
    }
    public void setPositionValues() {
        positionValues = new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
    }
    public void setAndLoadImage() {
        if (isPieceWhite) {
            imageLink = "images/Chess_rlt45.svg.png";
        } else imageLink = "images/Chess_rdt45.svg.png";
        actualImage = window.loadImage(imageLink, "png");
        actualImage.resize(100,100);
    }
    public void move(int[] nextPos) {
        super.move(nextPos);
        hasMoved = true;
    }
    public Piece testMove(int[] nextPos) {
        preTestHasMoved.add(hasMoved);
        hasMoved = true;
        return super.testMove(nextPos);
    }
    public void revertTest(Piece killed) {
        super.revertTest(killed);
        hasMoved = preTestHasMoved.remove(preTestHasMoved.size() - 1);
    }

    public void setFutureMoves() {
        // so the piece doesn't keep adding moves to its arrayList when the board updates
        if (isMovesAlreadySet) {
            return;
        }
        // checks all moves going up
        createAndCheckStraight(-100, true);
        // checks all moves going down
        createAndCheckStraight(100, true);
        // checks all moves left
        createAndCheckStraight(-100, false);
        // checks all moves right
        createAndCheckStraight(100, false);

        isMovesAlreadySet = true;

    }
    public void createAndCheckStraight(int directionalUnits, boolean isVertical) {
        int directionalUnitMultiplier = 1;
        boolean isMoveConflicting = false;

        while (!isMoveConflicting) {
            int[] nextMove = new int[2];
            if (isVertical) {
                nextMove[0] = xPos;
                nextMove[1] = yPos + directionalUnits * directionalUnitMultiplier;

                isMoveConflicting = hasMoveConflict(nextMove);

                directionalUnitMultiplier++;
            } else {
                nextMove[0] = xPos + directionalUnits * directionalUnitMultiplier;
                nextMove[1] = yPos;

                isMoveConflicting = hasMoveConflict(nextMove);

                directionalUnitMultiplier++;
            }
        }
    }
    public boolean getHasMoved() {
        return hasMoved;
    }
}
