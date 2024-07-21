import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    private final ArrayList<Piece> whitePieces;
    private final ArrayList<Piece> blackPieces;
    private Piece whiteKing;
    private Piece blackKing;
    private boolean isPlayerWhite;
    private boolean whiteTurn;
    private boolean hasCheckedGameOver;
    private boolean movesNotSet;
    private boolean tileClicked;
    private final int[] clickedTileLoc;
    private Piece selected;
    private final ArrayList<Piece> lastMoved;
    private final MovesAlgorithm movesAlgorithm;
    public Board(PApplet window) {
        whiteTurn = true;
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        movesNotSet = true;
        clickedTileLoc = new int[2];
        pickSide();
        createStartPieces(window);
        selected = null;
        lastMoved = new ArrayList<>();
        movesAlgorithm = new MovesAlgorithm(!isPlayerWhite, getAITeam(), getAIEnemy());
    }
    public void pickSide() {
        String[] options = { "Play White?", "Play Black?" };
        int selection = JOptionPane.showOptionDialog(null, "Pick your side", "Select one:", 0, 3, null, options, options[0]);
        if (selection == 0) {
            isPlayerWhite = true;
        }
        if (selection == 1) {
            isPlayerWhite = false;
        }
    }
    public void drawPieces(PApplet window) {
        for (Piece piece: blackPieces) {
            piece.draw(window);
        }
        for (Piece piece: whitePieces) {
            piece.draw(window);
        }
    }
    public void drawLastMovedTile(PApplet window) {
        if (lastMoved.size() == 0) {
            return;
        }
        int[] currentPos = getLatestMovedPiece().getPosition();
        int[] previousPos = getLatestMovedPiece().getPreviousPosition();
        window.fill(255,0,0,80);
        window.rect(currentPos[0], currentPos[1], 100, 100);
        window.rect(previousPos[0], previousPos[1], 100, 100);
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
        int whiteYPos = 600;
        int blackYPos = 100;
        if (!isPlayerWhite) {
            whiteYPos = 100;
            blackYPos = 600;
        }
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn(i * 100, whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
            blackPieces.add(new Pawn(i * 100, blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        }
    }
    private void createUniquePiecesRows(PApplet window) {
        int whiteYPos = 700;
        int blackYPos = 0;
        if (!isPlayerWhite) {
            whiteYPos = 0;
            blackYPos = 700;
        }
        whitePieces.add(new Rook(0,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Rook(700,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Knight(600,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Knight(100,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Bishop(200,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Bishop(500,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whitePieces.add(new Queen(300,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this));
        whiteKing = new King(400,whiteYPos, true, isPlayerWhite, whitePieces, blackPieces, window, this);
        whitePieces.add(whiteKing);


        blackPieces.add(new Rook(0,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Rook(700,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Knight(600,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Knight(100,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Bishop(500,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Bishop(200,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackPieces.add(new Queen(300,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this));
        blackKing = new King(400,blackYPos, false, isPlayerWhite, blackPieces, whitePieces, window, this);
        blackPieces.add(blackKing);
    }
    public void updateBoard(Game window) {
        ArrayList<Piece> piecesInPlay;
        if (whiteTurn) {
            piecesInPlay = whitePieces;
        } else {
            piecesInPlay = blackPieces;
        }
        // checks if the current board is won in checkmate
        if (!hasCheckedGameOver) {
            if (isBoardCheckmate()) {
                if (whiteTurn) {
                    window.fill(0, 0, 255);
                    window.textSize(50);
                    window.text("Black has checkmated White", 60, 400);
                    return;
                } else {
                    window.fill(0, 0, 255);
                    window.textSize(50);
                    window.text("White has checkmated Black", 60, 400);
                    return;
                }
            } else if (isInPlayMovesEmpty()) {
                window.fill(0, 0, 255);
                window.textSize(50);
                window.text("Stalemate", 300, 400);
                return;
            }
            hasCheckedGameOver = true;
        }
        if (whiteTurn == isPlayerWhite) {
            playerMoves(piecesInPlay, window);
        } else aiMoves(movesAlgorithm);
    }
    public boolean isBoardCheckmate() {
        if (isBoardInCheck()) {
            return isInPlayMovesEmpty();
        }
        return false;
    }
    public boolean isGameOver() {
        return isInPlayMovesEmpty();
    }
    public void aiMoves(MovesAlgorithm movesAlgorithm) {
        movesAlgorithm.minimax(this, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        selected = movesAlgorithm.getChosenPiece();
        selected.move(movesAlgorithm.getChosenMove());
        hasCheckedGameOver = false;
        switchTurns();
        selected = null;
        clearAllFutureMoves();
    }
    public void addAllMovePossibilities(ArrayList<int[]> allMovePossibilities, ArrayList<Piece> allInitialPieces, ArrayList<Piece> piecesInPlay) {
        for (Piece piece: piecesInPlay) {
            for (int[] futureMove: piece.getFutureMoves()) {
                allMovePossibilities.add(futureMove);
                allInitialPieces.add(piece);
            }
        }
    }
    public void playerMoves(ArrayList<Piece> piecesInPlay, Game window) {
        if (movesNotSet) {
            setAllInPlayMoves(piecesInPlay);
            movesNotSet = false;
        }
        isTileClicked(window, piecesInPlay);
        if (tileClicked) {
            drawClickedTile(window);
            ArrayList<int[]> selectedFutureMoves = selected.getFutureMoves();
            drawMoveOptions(window, selectedFutureMoves);

            if (isFutureMoveTilePressed(window, selectedFutureMoves, selected)) {
                switchTurns();
                tileClicked = false;
                hasCheckedGameOver = false;
                selected = null;
                movesNotSet = true;
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
    public boolean isFutureMoveTilePressed(Game window, ArrayList<int[]> selectedFutureMoves, Piece selected) {
        int xClickLoc = window.getClickX();
        int yClickLoc = window.getClickY();
        for (int[] futureMove: selectedFutureMoves) {
            if (xClickLoc > futureMove[0] && xClickLoc < futureMove[0] + 100 && yClickLoc > futureMove[1]  &&
                    yClickLoc < futureMove[1] + 100) {
                selected.move(futureMove);
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
                clickedTileLoc[0] = p.getXPos();
                clickedTileLoc[1] = p.getYPos();
                selected = p;
                tileClicked = true;
                return;
            }
        }
    }
    public boolean isBoardInCheck() {
        Piece kingInPlay;
        ArrayList<Piece> enemyPieces;
        if (whiteTurn) {
            kingInPlay = whiteKing;
            enemyPieces = blackPieces;
        } else {
            kingInPlay = blackKing;
            enemyPieces = whitePieces;
        }
        for (Piece enemy: enemyPieces) {
            enemy.setFutureMoves();
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
    public boolean isInPlayMovesEmpty() {
        ArrayList<Piece> piecesInPlay;
        if (whiteTurn) {
            piecesInPlay = whitePieces;
        } else {
            piecesInPlay = blackPieces;
        }
        for (Piece teamPiece: piecesInPlay) {
            teamPiece.setFutureMoves();
            teamPiece.filterMovesInCheck();
            ArrayList<int[]> piecesFutureMoves = teamPiece.getFutureMoves();
            if (piecesFutureMoves.size() > 0) {
                clearTeamsFutureMove(piecesInPlay);
                return false;
            }
        }
        return true;
    }
    public void setAllInPlayMoves(ArrayList<Piece> piecesInPlay) {
        for (Piece piece: piecesInPlay) {
            piece.setFutureMoves();
            piece.filterMovesInCheck();
        }
    }
    public ArrayList<Piece> getAITeam() {
        if (isPlayerWhite) {
            return blackPieces;
        } else return whitePieces;
    }
    public ArrayList<Piece> getAIEnemy() {
        if (isPlayerWhite) {
            return whitePieces;
        } else return blackPieces;
    }
    public void switchTurns() {
        whiteTurn = !whiteTurn;
    }
    public int calculatePoints(boolean isWhite) {
        int totalPoints = 0;
        for (Piece white: whitePieces) {
            int addPoints = getPiecePointVal(white, isWhite);
            totalPoints += addPoints;
        }
        for (Piece black: blackPieces) {
            int addPoints = getPiecePointVal(black, isWhite);
            totalPoints += addPoints;
        }
        return totalPoints;
    }
    public int getPiecePointVal(Piece piece, boolean isWhite) {
        int points;
        points = getStringValue(piece.name);
        points += piece.getPositionValue();
        if (isWhite != piece.isPieceWhite) {
            points *= -1;
        }
        return points;
    }
    public int getStringValue(String name) {
        switch (name) {
            case "pawn":
                return 10;
            case "knight":
            case "bishop":
                return 30;
            case "rook":
                return 50;
            case "queen":
                return 90;
            case "king":
                return 900;
            default:
                return 0;
        }
    }
    public void addLastMoved(Piece movedPiece) {
        lastMoved.add(movedPiece);
    }
    public Piece removeLastMoved() {
        return lastMoved.remove(lastMoved.size()-1);
    }
    public Piece getLatestMovedPiece() {
        if (lastMoved.size() == 0) {
            return null;
        }
        return lastMoved.get(lastMoved.size()-1);
    }
}
