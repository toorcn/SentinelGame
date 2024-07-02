package com.sentinel.apg39;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class SentinelGame extends Application {

    private int selectedCol = -1;
    private int selectedRow = -1;
    private PieceInfo selectedPiece = null;

    GridPane board = getGameBoard();

    @Override public void start(Stage stage) {

        Scene scene = new Scene(board, 700, 600);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();

//        Add pieces to the board
        startingPieceLayout();

//        check board logic
        board.setOnMouseClicked(event -> {
            int col = (int) (event.getX() - 100) / 50;
            int row = (int) (event.getY() - 100) / 50;

            PieceInfo pieceInfo = getPieceByCordinates(col, row);
            System.out.println("Clicked on Col: " + col + ", Row: " + row + ", Piece: " + pieceInfo.getId() + ", Name: " + pieceInfo.getName() + ", Side: " + pieceInfo.getIsWhiteString());

            removeHighlight();
            if (
                !pieceInfo.getName().equals("empty") &&
                (
                    pieceInfo.getId().startsWith("white") ||
                    pieceInfo.getId().startsWith("black")
                )
            ) {
                selectedPiece = pieceInfo;
                selectedCol = col;
                selectedRow = row;
                highlightCell(col, row);

//                get legal moves
                for (int[] move : pieceInfo.getMoves(col, row)) {
                    int moveCol = move[0];
                    int moveRow = move[1];
//                    check if move is valid and block "moveable" from being placed
                    highlightMoveables(moveCol, moveRow);
                }
            }
            if (selectedPiece != null && pieceInfo.getName().equals("moveable")) {
//                check for legal moves
                movePiece(col, row);
            }
//            if (selectedPiece != null && (pieceInfo.getName().equals("empty") || pieceInfo.getName().equals("moveable"))) {
//                movePiece(col, row);
//            }
        });

    }

    public static void main(String[] args) { launch(args); }

    private static GridPane getGameBoard() {
//        Chessboard
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setPadding(new Insets(10));

// Create squares and add them to the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                Pane square = new Pane();
                square.setPrefSize(50, 50);
                square.setStyle((row + col) % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: gray;");
                board.add(square, col, row);
            }
        }
        return board;
    }

    private void startingPieceLayout() {
        board.add(new Rook(false, "0"), 0, 0);
        board.add(new Sentinel(false, "0"), 1, 0);
        board.add(new Knight(false, "0"), 2, 0);
        board.add(new Bishop(false, "0"), 3, 0);
        board.add(new Queen(false, "0"), 4, 0);
        board.add(new King(false, "0"), 5, 0);
        board.add(new Bishop(false, "1"), 6, 0);
        board.add(new Knight(false, "1"), 7, 0);
        board.add(new Sentinel(false, "1"), 8, 0);
        board.add(new Rook(false, "1"), 9, 0);
        for (int i = 0; i < 10; i++) {
            board.add(new Pawn(false, String.valueOf(i)), i, 1);
        }

        board.add(new Rook(true, "0"), 0, 7);
        board.add(new Sentinel(true, "0"), 1, 7);
        board.add(new Knight(true, "0"), 2, 7);
        board.add(new Bishop(true, "0"), 3, 7);
        board.add(new Queen(true, "0"), 4, 7);
        board.add(new King(true, "0"), 5, 7);
        board.add(new Bishop(true, "1"), 6, 7);
        board.add(new Knight(true, "1"), 7, 7);
        board.add(new Sentinel(true, "1"), 8, 7);
        board.add(new Rook(true, "1"), 9, 7);
        for (int i = 0; i < 10; i++) {
            board.add(new Pawn(true, String.valueOf(i)), i, 6);
        }

//        int blankId = 0;
//        for (int col = 0; col < 10; col++) {
//            for (int row = 2; row < 6; row++) {
//                board.add(new Blank(String.valueOf(blankId)), col, row);
//                blankId++;
//            }
//        }
    }

    private PieceInfo getPieceByCordinates(int col, int row) {
        PieceInfo pieceInfo = new PieceInfo();
        board.getChildren().forEach(node -> {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                if (!node.getClass().getSimpleName().equals("Pane")) {
                    pieceInfo.parseId(node.getId());
                }
            }
        });
        return pieceInfo;
    }

    public void removePiece(int col, int row) {
        // Iterate over all children of the board to find the piece at (col, row)
        for (Node node : board.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                if (node.getClass().getSimpleName().equals("Pane")) {
                    continue;
                }
                System.out.println("Removing piece: " + node.getClass().getSimpleName() + " at col: " + col + ", row: " + row);
                // Remove the piece from the board
                board.getChildren().remove(node);
                break; // Exit loop since we found and removed the piece
            }
        }
    }

    private void movePiece(int col, int row) {
        if (selectedPiece == null) {
            System.out.println("No piece selected");
            return;
        }
        System.out.println("Moving piece: " + selectedPiece.getName() + " from col: " + selectedCol + ", row: " + selectedRow + " to col: " + col + ", row: " + row);
        removePiece(selectedCol, selectedRow);
        Piece piece = switch (selectedPiece.getName()) {
            case "pawn" -> new Pawn(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "rook" -> new Rook(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "knight" -> new Knight(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "bishop" -> new Bishop(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "queen" -> new Queen(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "king" -> new King(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            case "sentinel" -> new Sentinel(selectedPiece.getIsWhite(), selectedPiece.getUniqueId());
            default -> null;
        };

        board.add(piece, col, row);

        selectedPiece = null;
        selectedCol = -1;
        selectedRow = -1;
    }

    private void highlightCell(int col, int row) {
        Rectangle highlight = new Rectangle();
        String uniqueId = String.valueOf(System.currentTimeMillis());
        highlight.setId("none_highlight_" + uniqueId); // set an ID for the Rectangle
        highlight.setWidth(50); // match the cell size
        highlight.setHeight(50); // match the cell size
        highlight.setFill(Color.YELLOW);
        highlight.setOpacity(0.2); // semi-transparent

        board.add(highlight, col, row);
    }

    private void highlightMoveables(int col, int row) {
        Circle circle = new Circle();
        String uniqueId = String.valueOf(System.currentTimeMillis());
        circle.setId("none_moveable_" + uniqueId); // set an ID for the Rectangle
        circle.setRadius(15);
        circle.setFill(Color.BLUE);
        circle.setOpacity(0.2);

//        board.add(circle, col, row);
        StackPane stackPane = new StackPane(circle);
        String uniqueId2 = String.valueOf(System.currentTimeMillis());
        stackPane.setId("none_moveable_" + uniqueId2);
        board.add(stackPane, col, row);
    }

    private void removeHighlight() {
        List<Node> nodesToRemove = new ArrayList<>();
        board.getChildren().forEach(node -> {
            if (node.getId() != null && node.getId().startsWith("none_highlight_")) {
                nodesToRemove.add(node);
            }
            if (node.getId() != null && node.getId().startsWith("none_moveable_")) {
                nodesToRemove.add(node);
            }
        });
        board.getChildren().removeAll(nodesToRemove);
    }
}
