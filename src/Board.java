import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private Piece whiteKing;
    private Piece blackKing;
    private boolean whiteTurn;
    private boolean tileClicked;
    private int[] clickedTileLoc;
    private Piece selected;
    private Piece lastMoved;
    public Board(PApplet window) {
        whiteTurn = true;
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        clickedTileLoc = new int[2];
        createStartPieces(window);
        selected = null;
        lastMoved = new Piece(10000, 10000, true);
    }
    public void drawPieces(PApplet window) {
        for (Piece piece: blackPieces) {
            piece.draw(window);
        }
        for (Piece piece: whitePieces) {
            piece.draw(window);
        }
    }
    public void drawClickedTile(PApplet window) {
        window.fill(0, 0, 0, 60);
        window.rect(clickedTileLoc[0], clickedTileLoc[1], 100, 100);
    }
    public void drawMoveOptions(PApplet window, ArrayList<int[]> selectedFutureMoves) {
        window.fill(255, 0, 0);
        for (int[] futureMove : selectedFutureMoves) {
            window.ellipse(futureMove[0] + 50, futureMove[1] + 50, 30, 30);
        }
    }
    private void createStartPieces(PApplet window) {
        createPawnRows(window);
        createUniquePiecesRows(window);
    }
    private void createPawnRows(PApplet window) {
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn(i * 100,600, true, window));
            blackPieces.add(new Pawn(i * 100, 100, false, window));
        }
    }
    private void createUniquePiecesRows(PApplet window) {
        whitePieces.add(new Rook(0,700, true, window));
        whitePieces.add(new Rook(700,700, true, window));
        whitePieces.add(new Knight(100, 700, true, window));
        whitePieces.add(new Knight(600, 700, true, window));
        whitePieces.add(new Bishop(200, 700, true, window));
        whitePieces.add(new Bishop(500, 700, true, window));
        whitePieces.add(new Queen(300, 700, true, window));
        whiteKing = new King(400, 700, true, window);
        whitePieces.add(whiteKing);


        blackPieces.add(new Rook(0,0, false, window));
        blackPieces.add(new Rook(700,0, false, window));
        blackPieces.add(new Knight(100, 0, false, window));
        blackPieces.add(new Knight(600, 0, false, window));
        blackPieces.add(new Bishop(200, 0, false, window));
        blackPieces.add(new Bishop(500, 0, false, window));
        blackPieces.add(new Queen(300, 0, false, window));
        blackKing = new King(400, 0, false, window);
        blackPieces.add(blackKing);
    }
    public void updateBoard(Game window) {
        ArrayList<Piece> piecesInPlay;
        Piece kingInPlay;
        ArrayList<Piece> enemyPieces;
        if (whiteTurn) {
            piecesInPlay = whitePieces;
            kingInPlay = whiteKing;
            enemyPieces = blackPieces;
        } else {
            piecesInPlay = blackPieces;
            kingInPlay = blackKing;
            enemyPieces = whitePieces;
        }
        // checks if the current board is won in checkmate
        if (isBoardInCheck()) {

            kingInPlay.setHasBeenInCheck(true);

            if (isPiecesMoveEmpty(piecesInPlay, enemyPieces)) {
                if (whiteTurn) {
                    window.fill(0,0,255);
                    window.textSize(50);
                    window.text("Black has checkmated White", 60, 400);
                    return;
                } else {
                    window.fill(0,0,255);
                    window.textSize(50);
                    window.text("White has checkmated Black", 60, 400);
                    return;
                }
            }
        } else if (isPiecesMoveEmpty(piecesInPlay, enemyPieces)) {
            System.out.println("what the heck stalemate!");
        }

        isTileClicked(window, piecesInPlay);
        if (tileClicked) {
            drawClickedTile(window);
            selected.setFutureMoves(piecesInPlay, enemyPieces);
            selected.filterMovesInCheck(piecesInPlay, enemyPieces, this, lastMoved);
            ArrayList<int[]> selectedFutureMoves = selected.getFutureMoves();
            drawMoveOptions(window, selectedFutureMoves);

            if (isFutureMoveTilePressed(window, selectedFutureMoves, selected, piecesInPlay, enemyPieces)) {
                whiteTurn = !whiteTurn;
                tileClicked = false;
                selected = null;
                clearAllFutureMoves();
            }
        }
    }
    public void clearAllFutureMoves() {
        clearTeamsFutureMove(whitePieces);
        clearTeamsFutureMove(blackPieces);
    }
    public void clearTeamsFutureMove(ArrayList<Piece> team) {
        for (Piece p: team) {
            p.clearFutureMoves();
            p.unsetMovesAlreadySet();
        }
    }
    public boolean isFutureMoveTilePressed(Game window, ArrayList<int[]> selectedFutureMoves, Piece selected,
                                           ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {
        int xClickLoc = window.getClickX();
        int yClickLoc = window.getClickY();
        for (int[] futureMove: selectedFutureMoves) {
            if (xClickLoc > futureMove[0] && xClickLoc < futureMove[0] + 100 && yClickLoc > futureMove[1]  &&
                    yClickLoc < futureMove[1] + 100) {
                selected.move(futureMove, piecesInPlay, enemyPieces, window);
                lastMoved = selected;
                System.out.println(lastMoved.name);
                return true;
            }
        }
        return false;
    }

    public void isTileClicked(Game window, ArrayList<Piece> piecesInPlay) {
        int xClickLoc = window.getClickX();
        int yClickLoc = window.getClickY();

        for (Piece p : piecesInPlay) {
            // if the place u clicked is in the tile of a piece in play
            if (xClickLoc > p.xPos && xClickLoc < p.xPos + 100 && yClickLoc > p.yPos && yClickLoc < p.yPos + 100) {
                clickedTileLoc[0] = p.getxPos();
                clickedTileLoc[1] = p.getyPos();
                selected = p;
                tileClicked = true;
                return;
            }
        }
    }
    public boolean isBoardInCheck() {
        ArrayList<Piece> piecesInPlay;
        Piece kingInPlay;
        ArrayList<Piece> enemyPieces;
        if (whiteTurn) {
            piecesInPlay = whitePieces;
            kingInPlay = whiteKing;
            enemyPieces = blackPieces;
        } else {
            piecesInPlay = blackPieces;
            kingInPlay = blackKing;
            enemyPieces = whitePieces;
        }
        for (Piece enemy: enemyPieces) {
            enemy.setFutureMoves(enemyPieces, piecesInPlay);
            ArrayList<int[]> enemyFutureMoves = enemy.getFutureMoves();
            for (int[] futureMove: enemyFutureMoves) {
                if (Arrays.equals(kingInPlay.getPosition(), futureMove)) {
                    clearTeamsFutureMove(enemyPieces);
                    return true;
                }
            }
        }
        clearTeamsFutureMove(enemyPieces);
        return false;
    }
    public boolean isPiecesMoveEmpty(ArrayList<Piece> piecesInPlay, ArrayList<Piece> enemyPieces) {

        for (Piece teamPiece: piecesInPlay) {
            teamPiece.setFutureMoves(piecesInPlay, enemyPieces);
            teamPiece.filterMovesInCheck(piecesInPlay,enemyPieces, this, lastMoved);
            ArrayList<int[]> piecesFutureMoves = teamPiece.getFutureMoves();
            if (piecesFutureMoves.size() > 0) {
                return false;
            }
        }
        return true;
    }
}






