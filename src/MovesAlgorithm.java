import java.util.ArrayList;

public class MovesAlgorithm {
    private final int MAX_DEPTH;
    private final boolean isPlayerWhite;
    private final ArrayList<Piece> aiPieces;
    private final ArrayList<Piece> playerPieces;
    private int[] chosenMove;
    private Piece chosenPiece;

    public MovesAlgorithm(boolean yesWhite, ArrayList<Piece> team, ArrayList<Piece> enemy) {
        MAX_DEPTH = 2;
        isPlayerWhite = yesWhite;
        aiPieces = team;
        playerPieces = enemy;
    }
    public int chooseAIMove(ArrayList<int[]> allFutureMoves, ArrayList<Piece> allInitialPieces, Board gameBoard, int depth, boolean aiTurn) {
        ArrayList<Piece> piecesInPlay;
        ArrayList<Piece> enemyPieces;
        if (aiTurn) {
            piecesInPlay = aiPieces;
            enemyPieces = playerPieces;
        } else {
            piecesInPlay = playerPieces;
            enemyPieces = aiPieces;
        }
        ArrayList<Integer> allPointsValue = new ArrayList<>();

        if (depth == MAX_DEPTH) {
            for (int i = 0; i < allFutureMoves.size(); i++) {
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), enemyPieces);
                allPointsValue.add(calculatePoints());
                allInitialPieces.get(i).revertTest(killed, piecesInPlay, enemyPieces);
            }
            if (aiTurn) {
                return getHighestPointVal(allPointsValue);
            } else return getLowestPointVal(allPointsValue);
        }
        for (int i = 0; i < allFutureMoves.size(); i++) {
            Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), enemyPieces);
            ArrayList<int[]> nextAllFutureMoves = new ArrayList<>();
            ArrayList<Piece> nextAllInitialPieces = new ArrayList<>();
            setUpNextTurn(enemyPieces, piecesInPlay, gameBoard, nextAllFutureMoves, nextAllInitialPieces);
            allPointsValue.add(chooseAIMove(nextAllFutureMoves, nextAllInitialPieces, gameBoard, depth +1, !aiTurn));
            allInitialPieces.get(i).revertTest(killed, piecesInPlay, enemyPieces);
            gameBoard.switchTurns();
        }
        if (depth == 1) {
            int index = getHighestPointIndex(allPointsValue);
            chosenMove = allFutureMoves.get(index);
            chosenPiece = allInitialPieces.get(index);
            return 0;
        }
        else if (aiTurn) {
            return getHighestPointVal(allPointsValue);
        } else return getLowestPointVal(allPointsValue);

    }
    public void setUpNextTurn(ArrayList<Piece> enemyPieces, ArrayList<Piece> piecesInPlay, Board gameBoard, ArrayList<int[]> nextAllFutureMoves,
                               ArrayList<Piece> nextAllInitialPieces) {
        gameBoard.switchTurns();
        gameBoard.setAllInPlayMoves(enemyPieces, piecesInPlay, gameBoard);

        gameBoard.addAllMovePossibilities(nextAllFutureMoves, nextAllInitialPieces, enemyPieces);
    }
    public int getLowestPointVal(ArrayList<Integer> allPointsValue) {
        int lowestVal = Integer.MAX_VALUE;
        for (Integer integer : allPointsValue) {
            if (integer < lowestVal) {
                lowestVal = integer;
            }
        }
        return lowestVal;
    }
    public int getHighestPointVal(ArrayList<Integer> allPointsValue) {
        int highestVal = Integer.MIN_VALUE;
        for (Integer integer: allPointsValue) {
            if (integer > highestVal) {
                highestVal = integer;
            }
        }
        return highestVal;
    }
    public int getHighestPointIndex(ArrayList<Integer> lowestPointsPostMove) {
        ArrayList<Integer> bestIndexes = new ArrayList<>();
        int highestPoints = Integer.MIN_VALUE;
        for (int i = 0; i < lowestPointsPostMove.size(); i++) {
            if (lowestPointsPostMove.get(i) > highestPoints) {
                highestPoints = lowestPointsPostMove.get(i);
                bestIndexes.clear();
                bestIndexes.add(i);
            } else if (lowestPointsPostMove.get(i) == highestPoints) {
                bestIndexes.add(i);
            }
        }
        // on the 2nd depth if multiple moves gives the same minimum loss, then they should all have equal chance.
        int random = (int) (Math.random() * bestIndexes.size() );
        return bestIndexes.get(random);
    }
    public int calculatePoints() {
        int totalPoints = 0;
        for (Piece team: aiPieces) {
            totalPoints += getPiecePointVal(team);
        }
        for (Piece enemy: playerPieces) {
            totalPoints += getPiecePointVal(enemy);
        }
        return  totalPoints;
    }
    public int getPiecePointVal(Piece piece) {
        int points;
        String name = piece.name;
        points = getStringValue(name);
        if (isPlayerWhite == piece.isPieceWhite) {
            points *= -1;
        }
        return points;
    }
    public int getStringValue(String name) {
        if (name.equals("pawn")) {
            return 10;
        } else if (name.equals("knight") || name.equals("bishop")) {
            return 30;
        } else if (name.equals("rook")) {
            return 50;
        } else if (name.equals("queen")) {
            return 90;
        } else if (name.equals("king")) {
            return 900;
        } else return 0;
    }
    public int[] getChosenMove() {
        return chosenMove;
    }
    public Piece getChosenPiece() {
        return chosenPiece;
    }
}
