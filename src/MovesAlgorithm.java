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
        // on the first turn (when the ai is gonna move) this code DOES NOT RUN AT ALL
        if (depth == MAX_DEPTH) {
            for (int i = 0; i < allFutureMoves.size(); i++) {
                // killed in this case is a black piece, and enemy pieces is black
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), enemyPieces);
                allPointsValue.add(calculatePoints());
                // killed in this case is a black piece, and enemy pieces is black
                allInitialPieces.get(i).revertTest(killed, piecesInPlay, enemyPieces);
            }
            return  getLowestPointVal(allPointsValue);
        }
        for (int i = 0; i < allFutureMoves.size(); i++) {
            // should make sense honestly
            // killed in this case would be a white piece        and enemyPieces would be white
            Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), enemyPieces);
            // now that we moved the ai (test move) we're gonna setup all the player's options
            // to mimic the board if it was now the player playing
            ArrayList<int[]> nextAllFutureMoves = new ArrayList<>();
            ArrayList<Piece> nextAllInitialPieces = new ArrayList<>();
            setUpNextTurn(enemyPieces, piecesInPlay, gameBoard, nextAllFutureMoves, nextAllInitialPieces);

            allPointsValue.add(chooseAIMove(nextAllFutureMoves, nextAllInitialPieces, gameBoard, depth + 1, !aiTurn));
            // killed in this case is still a white piece, and enemyPieces is still white
            allInitialPieces.get(i).revertTest(killed, piecesInPlay, enemyPieces);
            gameBoard.switchTurns();
        }

        int index = getHighestPointIndex(allPointsValue);
        chosenMove = allFutureMoves.get(index);
        chosenPiece = allInitialPieces.get(index);
        return getLowestPointVal(allPointsValue);
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
        int points = 0;
        if (piece.name.equals("pawn")) {
            points = 10;
        } else if (piece.name.equals("knight") || piece.name.equals("bishop")) {
            points = 30;
        } else if (piece.name.equals("rook")) {
            points = 50;
        } else if (piece.name.equals("queen")) {
            points = 90;
            // following a chart of piece strength though i dont see how this has a use since a king would always be on the board?
        } else if (piece.name.equals("king")) {
            points = 900;
        }
        if (isPlayerWhite == piece.isPieceWhite) {
            points *= -1;
        }
        return points;
    }
    public int[] getChosenMove() {
        return chosenMove;
    }
    public Piece getChosenPiece() {
        return chosenPiece;
    }
}
