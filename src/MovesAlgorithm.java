import java.util.ArrayList;

public class MovesAlgorithm {
    private boolean whiteTurn;
    private ArrayList<Piece> piecesInPlay;
    private ArrayList<Piece> enemyPieces;
    private int[] chosenMove;
    private Piece chosenPiece;

    public MovesAlgorithm(boolean yesWhite, ArrayList<Piece> team, ArrayList<Piece> enemy) {
        whiteTurn = yesWhite;
        piecesInPlay = team;
        enemyPieces = enemy;
    }
    public void pickBestMove(ArrayList<int[]> allFutureMoves, ArrayList<Piece> allInitialPieces) {
        int totalPoints = 0;
        ArrayList<int[]> bestMoves = new ArrayList<>();
        ArrayList<Piece> bestMovesPieces = new ArrayList<>();
        // loops through every move and makes a list of the best moves
        System.out.println(allFutureMoves.size());
        for (int i = 0; i < allFutureMoves.size(); i++) {
            int[] initialPos = allInitialPieces.get(i).getPosition();
            Piece killed = allInitialPieces.get(i).testMove(allFutureMoves.get(i), enemyPieces);
            int pointsOfMove = calculatePoints();
            if (pointsOfMove > totalPoints) {
                // if the points were greater, then clear lists and add the new options
                totalPoints = pointsOfMove;
                bestMoves.clear();
                bestMovesPieces.clear();

                bestMoves.add(allFutureMoves.get(i));
                bestMovesPieces.add(allInitialPieces.get(i));
                // otherwise you just add them to the current list if its equal
            } else if (pointsOfMove == totalPoints) {
                bestMoves.add(allFutureMoves.get(i));
                bestMovesPieces.add(allInitialPieces.get(i));
                // if it was the first move then... we have no move to create our basis on
            } else if (i == 0) {
                totalPoints = pointsOfMove;
                bestMoves.add(allFutureMoves.get(i));
                bestMovesPieces.add(allInitialPieces.get(i));
            }
            // revert back the piece back to its original position
            if (killed != null) {
                enemyPieces.add(killed);
            }
            allInitialPieces.get(i).testMove(initialPos, enemyPieces);
        }

        chooseMoveAndPiece(bestMoves, bestMovesPieces);
    }
    public void chooseMoveAndPiece(ArrayList<int[]> bestMoves, ArrayList<Piece> bestMovesPieces) {
        int randomIndex = (int) (Math.random() * bestMoves.size() );

        chosenMove = bestMoves.get(randomIndex);
        chosenPiece = bestMovesPieces.get(randomIndex);
    }
    public int calculatePoints() {
        int totalPoints = 0;
        for (Piece team: piecesInPlay) {
            totalPoints += getPiecePointVal(team);
        }
        for (Piece enemy: enemyPieces) {
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
        if (whiteTurn != piece.isPieceWhite) {
            points *= -1;
        }
        return points;
    }
    public void updatePieces(ArrayList<Piece> team, ArrayList<Piece> enemy) {
        piecesInPlay = team;
        enemyPieces = enemy;
    }
    public void updateWhoseTurn(boolean white) {
        whiteTurn = white;
    }
    public int[] getChosenMove() {
        return chosenMove;
    }
    public Piece getChosenPiece() {
        return chosenPiece;
    }
}
