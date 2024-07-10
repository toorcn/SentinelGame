package com.sentinel.apg39;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameSave {
    private GridPane board;
    private PieceInfo[] whiteCapturedPieces;
    private PieceInfo[] blackCapturedPieces;
    private boolean playingSide;

    public GameSave(GridPane board, PieceInfo[] whiteCapturedPieces, PieceInfo[] blackCapturedPieces, boolean playingSide) {
        this.board = board;
        this.whiteCapturedPieces = whiteCapturedPieces;
        this.blackCapturedPieces = blackCapturedPieces;
        this.playingSide = playingSide;
    }

    public GameSave(GridPane board) {
        this.board = board;
    }

    public boolean saveGame() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game.txt"))) {
            for (Node node : board.getChildren()) {
                if (node instanceof Piece) {
                    writer.write(((Piece) node).getId() + "," + GridPane.getColumnIndex(node) + "," + GridPane.getRowIndex(node));
                    writer.newLine();
                }
            }
            writer.write("-whiteCapturedPieces");
            writer.newLine();
            for (PieceInfo piece : whiteCapturedPieces) {
                if (piece == null) {
                    break;
                }
                writer.write(piece.getId() + "," + piece.getCol() + "," + piece.getRow());
                writer.newLine();
            }
            writer.write("-blackCapturedPieces");
            writer.newLine();
            for (PieceInfo piece : blackCapturedPieces) {
                if (piece == null) {
                    break;
                }
                writer.write(piece.getId() + "," + piece.getCol() + "," + piece.getRow());
                writer.newLine();
            }

            writer.write("-playingSide");
            writer.newLine();
            writer.write((playingSide ? "white" : "black"));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void loadGame() {
        GridPane newBoard = board;
        try (BufferedReader reader = new BufferedReader(new FileReader("game.txt"))) {
            String line;
            boolean readWhiteCaptured = false;
            boolean readBlackCaptured = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("-whiteCapturedPieces")) {
                    readWhiteCaptured = true;
                    continue;
                } else if (line.equals("-blackCapturedPieces")) {
                    readBlackCaptured = true;
                    continue;
                } else if (line.equals("-playingSide")) {
                    playingSide = reader.readLine().equals("white");
                    continue;
                }

                if (!readWhiteCaptured) {
//                    load board piece
                    String[] parts = line.split(",");
                    Piece piece = createPieceById(parts[0]);
                    GridPane.setColumnIndex(piece, Integer.parseInt(parts[1]));
                    GridPane.setRowIndex(piece, Integer.parseInt(parts[2]));
                    newBoard.getChildren().add(piece);
                } else if (!readBlackCaptured) {
//                    load white captured piece
                    String[] parts = line.split(",");
                    PieceInfo piece = new PieceInfo();
                    piece.parseId(parts[0]);
                    piece.setCol(Integer.parseInt(parts[1]));
                    piece.setRow(Integer.parseInt(parts[2]));
                    whiteCapturedPieces[Integer.parseInt(parts[2])] = piece;
                } else {
//                  load black captured pieces
                    String[] parts = line.split(",");
                    PieceInfo piece = new PieceInfo();
                    piece.parseId(parts[0]);
                    piece.setCol(Integer.parseInt(parts[1]));
                    piece.setRow(Integer.parseInt(parts[2]));
                    blackCapturedPieces[Integer.parseInt(parts[2])] = piece;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Piece createPieceById(String pieceId) {
        String name = pieceId.split("_")[1];
        Boolean isWhite = pieceId.split("_")[0].equals("white");
        String id = pieceId.split("_")[2];
        return switch (name) {
            case "pawn" -> new Pawn(isWhite, id);
            case "rook" -> new Rook(isWhite, id);
            case "knight" -> new Knight(isWhite, id);
            case "bishop" -> new Bishop(isWhite, id);
            case "queen" -> new Queen(isWhite, id);
            case "king" -> new King(isWhite, id);
            case "sentinel" -> new Sentinel(isWhite, id);
            default -> null;
        };
    }

    public boolean getPlayingSide() {
        return playingSide;
    }
}
