import java.util.ArrayList;

public class MovesAlgorithm {
    private ArrayList<Integer> allPointsValue;
    private final int MAX_DEPTH;
    private final boolean isWhite;
    private final ArrayList<Piece> aiPieces;
    private final ArrayList<Piece> playerPieces;
    private int[] chosenMove;
    private Piece chosenPiece;

    public MovesAlgorithm(boolean white, ArrayList<Piece> team, ArrayList<Piece> enemy) {
        allPointsValue = new ArrayList<>();
        MAX_DEPTH = 3;
        isWhite = white;
        aiPieces = team;
        playerPieces = enemy;
    }
    public int minimax(Board gameBoard, int depth, int alpha, int beta, boolean aiTurn) {
        // if we hit the bottom of our search then it returns a static value of the max points(if ai) or min points(if player)
        if (depth == MAX_DEPTH || gameBoard.isGameOver()) {
            return gameBoard.calculatePoints(isWhite);
        }
        ArrayList<int[]> allFutureMoves = new ArrayList<>();
        ArrayList<Piece> allInitialPieces = new ArrayList<>();
        if (aiTurn) {
            gameBoard.setAllInPlayMoves(aiPieces, gameBoard);
            gameBoard.addAllMovePossibilities(allFutureMoves, allInitialPieces, aiPieces);
        } else {
            gameBoard.setAllInPlayMoves(playerPieces, gameBoard);
            gameBoard.addAllMovePossibilities(allFutureMoves, allInitialPieces, playerPieces);
        }
        if (aiTurn) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < allFutureMoves.size(); i++) {
                // testing out the move option, and then saving the point evaluation to allPointsValue
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i));
                gameBoard.switchTurns();
                if (depth == 0) {
                    System.out.println("depth 0 we got something with an eval of " + gameBoard.calculatePoints(isWhite));
                }
                int eval = minimax(gameBoard, depth +1, alpha, beta, false);
                System.out.println("but we're saving the eval of " + eval);
                if (depth == 0) {
                    allPointsValue.add(eval);
                }
                // undo the testMove that just happened
                allInitialPieces.get(i).revertTest(killed);
                gameBoard.switchTurns();

                // allPointsValue.get(i) is the point evaluation of the move AT THIS loop
                maxEval = Math.max(maxEval, eval);
                // alpha beta pruning
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            if (depth == 0) {
                int index = getHighestPointsIndex();
                chosenMove = allFutureMoves.get(index);
                chosenPiece = allInitialPieces.get(index);
                return 0;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < allFutureMoves.size(); i++) {
                // testing out the move option, and then saving the point evaluation to allPointsValue
                gameBoard.switchTurns();
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i));
                System.out.println("depth 1 the predicted player moves had an eval of " + gameBoard.calculatePoints(isWhite));
                int eval = minimax(gameBoard, depth + 1, alpha, beta, true);
                if (depth == 0) {
                    allPointsValue.add(eval);
                }
                // undo the move that just happened
                allInitialPieces.get(i).revertTest(killed);
                gameBoard.switchTurns();

                // allPointsValue.get(i) is the point evaluation of the move AT THIS loop
                minEval = Math.min(minEval, eval);
                // alpha beta pruning
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }

    }
    public int getHighestPointsIndex() {
        ArrayList<Integer> bestIndexes = new ArrayList<>();
        int highestPoints = Integer.MIN_VALUE;
        for (int i = 0; i < allPointsValue.size(); i++) {
            if (allPointsValue.get(i) > highestPoints) {
                System.out.println("this was the highest point we've seen " + allPointsValue.get(i));
                highestPoints = allPointsValue.get(i);
                bestIndexes.clear();
                bestIndexes.add(i);
            } else if (allPointsValue.get(i) == highestPoints) {
                bestIndexes.add(i);
            }
        }
        // on the 2nd depth if multiple moves gives the same minimum loss, then they should all have equal chance.
        int random = (int) (Math.random() * bestIndexes.size() );

        allPointsValue.clear();
        return bestIndexes.get(random);
    }

    public int[] getChosenMove() {
        return chosenMove;
    }
    public Piece getChosenPiece() {
        return chosenPiece;
    }
}
