import java.util.ArrayList;

public class MovesAlgorithm {
    private ArrayList<Integer> allPointsValue;
    private final int MAX_DEPTH;
    private final boolean isPlayerWhite;
    private final ArrayList<Piece> aiPieces;
    private final ArrayList<Piece> playerPieces;
    private int[] chosenMove;
    private Piece chosenPiece;

    public MovesAlgorithm(boolean yesWhite, ArrayList<Piece> team, ArrayList<Piece> enemy) {
        allPointsValue = new ArrayList<>();
        MAX_DEPTH = 3;
        isPlayerWhite = yesWhite;
        aiPieces = team;
        playerPieces = enemy;
    }
    public int minimax(Board gameBoard, int depth, int alpha, int beta, boolean aiTurn) {
        // if we hit the bottom of our search then it returns a static value of the max points(if ai) or min points(if player)
        if (depth == MAX_DEPTH) {
            return calculatePoints();
        }
        ArrayList<int[]> allFutureMoves = new ArrayList<>();
        ArrayList<Piece> allInitialPieces = new ArrayList<>();
        if (aiTurn) {
            gameBoard.setAllInPlayMoves(aiPieces, playerPieces, gameBoard);
            gameBoard.addAllMovePossibilities(allFutureMoves, allInitialPieces, aiPieces);
        } else {
            gameBoard.setAllInPlayMoves(playerPieces, aiPieces, gameBoard);
            gameBoard.addAllMovePossibilities(allFutureMoves, allInitialPieces, playerPieces);
        }
        if (aiTurn) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < allFutureMoves.size(); i++) {
                // testing out the move option, and then saving the point evaluation to allPointsValue
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), playerPieces);
                gameBoard.switchTurns();
                int eval = minimax(gameBoard, depth +1, alpha, beta, !aiTurn);
                if (depth == 0) {
                    allPointsValue.add(eval);
                }
                // undo the testMove that just happened
                allInitialPieces.get(i).revertTest(killed, aiPieces, playerPieces);
                gameBoard.switchTurns();

                // allPointsValue.get(i) is the point evaluation of the move AT THIS loop
                maxEval = Math.max(maxEval, eval);
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
            } else return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < allFutureMoves.size(); i++) {
                // testing out the move option, and then saving the point evaluation to allPointsValue
                Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), aiPieces);
                gameBoard.switchTurns();
                int eval = minimax(gameBoard, depth + 1, alpha, beta, !aiTurn);
                if (depth == 0) {
                    allPointsValue.add(eval);
                }
                // undo the move that just happened
                allInitialPieces.get(i).revertTest(killed, aiPieces, aiPieces);
                gameBoard.switchTurns();

                // allPointsValue.get(i) is the point evaluation of the move AT THIS loop
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
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
            return minEval;
        }

    }
    public int getHighestPointsIndex() {
        ArrayList<Integer> bestIndexes = new ArrayList<>();
        int highestPoints = Integer.MIN_VALUE;
        for (int i = 0; i < allPointsValue.size(); i++) {
            System.out.println("this index " + i + " had " + allPointsValue.get(i) + " points");
            if (allPointsValue.get(i) > highestPoints) {
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
        System.out.println("we seriously chose index " + bestIndexes.get(random));
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
