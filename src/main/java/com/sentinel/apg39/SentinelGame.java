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

import java.util.ArrayList;
import java.util.List;

public class SentinelGame extends Application {

    private PieceInfo selectedPiece = null;
    private boolean isWhiteTurn = true;

    GridPane board = getGameBoard();

    int sentinelXpMoveable = -1;
    int sentinelXnMoveable = -1;
    int sentinelYpMoveable = -1;
    int sentinelYnMoveable = -1;

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
                if (
                    isWhiteTurn && pieceInfo.getId().startsWith("black") ||
                    !isWhiteTurn && pieceInfo.getId().startsWith("white")
                ) {
//                    System.out.println("Not your turn");
                    return;
                }
                selectedPiece = pieceInfo;
                highlightCell(col, row);

//                get legal moves
                String path = "\n";
                boolean rookXpBlocked = false;
                boolean rookXnBlocked = false;
                boolean rookYpBlocked = false;
                boolean rookYnBlocked = false;

                boolean bishopXpYpBlocked = false;
                boolean bishopXpYnBlocked = false;
                boolean bishopXnYpBlocked = false;
                boolean bishopXnYnBlocked = false;

                boolean queenXpYpBlocked = false;
                boolean queenXpYnBlocked = false;
                boolean queenXnYpBlocked = false;
                boolean queenXnYnBlocked = false;
                boolean queenXpBlocked = false;
                boolean queenXnBlocked = false;
                boolean queenYpBlocked = false;
                boolean queenYnBlocked = false;

                boolean pawnYBlocked = false;

                boolean sentinelXpBlocked = false;
                boolean sentinelXnBlocked = false;
                boolean sentinelYpBlocked = false;
                boolean sentinelYnBlocked = false;

                sentinelXpMoveable = -1;
                sentinelYpMoveable = -1;
                sentinelYnMoveable = row;
                sentinelXnMoveable = col;

                for (int[] move : pieceInfo.getMoves(col, row)) {
                    int moveCol = move[0];
                    int moveRow = move[1];
                    if (moveCol < 0 || moveCol > 9 || moveRow < 0 || moveRow > 7) continue;
//                    check if move is valid and block "moveable" from being placed
                    PieceInfo targetCellPiece = getPieceByCordinates(moveCol, moveRow);
                    path += "- " + targetCellPiece.getName() + " " + moveCol + " " + moveRow + " \n";

                    switch (selectedPiece.getName()) {
                        case "king":
                        case "knight":
                            if (targetCellPiece.getName().equals("empty")) {
                                markMoveableCell(moveCol, moveRow);
                            } else if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                markMoveableCell(moveCol, moveRow);
                            }
                            break;
                        case "rook":
                            if (moveCol == col) {
                                if (moveRow > row) {
                                    if (rookYpBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        rookYpBlocked = true;
                                    }
                                } else {
                                    if (rookYnBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        rookYnBlocked = true;
                                    }
                                }
                            } else if (moveRow == row) {
                                if (moveCol > col) {
                                    if (rookXpBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        rookXpBlocked = true;
                                    }
                                } else {
                                    if (rookXnBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        rookXnBlocked = true;
                                    }
                                }
                            }
                            break;
                        case "bishop":
                            if (moveCol > col && moveRow > row) {
                                if (bishopXpYpBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    bishopXpYpBlocked = true;
                                }
                            } else if (moveCol > col && moveRow < row) {
                                if (bishopXpYnBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    bishopXpYnBlocked = true;
                                }
                            } else if (moveCol < col && moveRow > row) {
                                if (bishopXnYpBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    bishopXnYpBlocked = true;
                                }
                            } else if (moveCol < col && moveRow < row) {
                                if (bishopXnYnBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    bishopXnYnBlocked = true;
                                }
                            }
                            break;
                        case "queen":
                            if (moveCol == col) {
                                if (moveRow > row) {
                                    if (queenYpBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        queenYpBlocked = true;
                                    }
                                } else {
                                    if (queenYnBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        queenYnBlocked = true;
                                    }
                                }
                            } else if (moveRow == row) {
                                if (moveCol > col) {
                                    if (queenXpBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        queenXpBlocked = true;
                                    }
                                } else {
                                    if (queenXnBlocked) {
                                        break;
                                    }
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                        queenXnBlocked = true;
                                    }
                                }
                            } else if (moveCol > col && moveRow > row) {
                                if (queenXpYpBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    queenXpYpBlocked = true;
                                }
                            } else if (moveCol > col && moveRow < row ) {
                                if (queenXpYnBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    queenXpYnBlocked = true;
                                }
                            } else if (moveCol < col && moveRow > row) {
                                if (queenXnYpBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    queenXnYpBlocked = true;
                                }
                            } else if (moveCol < col && moveRow < row) {
                                if (queenXnYnBlocked) {
                                    break;
                                }
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                } else {
                                    if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                    queenXnYnBlocked = true;
                                }
                            }
                            break;
                        case "pawn":
//                            pawn is blocked by friendly
                            if (
                                selectedPiece.getCol() == moveCol &&
                                (
                                    !targetCellPiece.getName().equals("empty") &&
                                    targetCellPiece.getIsWhite() == isWhiteTurn
                                )
                            ) {
                                pawnYBlocked = true;
                                break;
                            }
//                            pawn is blocked by enemy
                            if (
                                pawnYBlocked &&
                                selectedPiece.getCol() == moveCol &&
                                (
                                    selectedPiece.getRow() + 2 == moveRow ||
                                    selectedPiece.getRow() - 2 == moveRow
                                )
                            ) {
                                break;
                            }

                            if (selectedPiece.getUniqueId().endsWith("0")) {
                                if (
                                    moveCol == selectedPiece.getCol() ||
                                    (
                                        !targetCellPiece.getName().equals("empty") &&
                                        targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()
                                    )
                                ) {
                                    markMoveableCell(moveCol, moveRow);
                                }
                                if (
                                    moveCol == selectedPiece.getCol() &&
                                    (
                                        !targetCellPiece.getName().equals("empty") &&
                                        targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()
                                    )
                                ) {
                                    pawnYBlocked = true;
                                }
                            } else {
                                if (
                                    moveRow - selectedPiece.getRow() > 1 ||
                                    moveRow - selectedPiece.getRow() < -1
                                ) continue;
                                if (moveCol == selectedPiece.getCol()) {
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (targetCellPiece.getName().equals("empty")) {
                                        break;
                                    } else {
                                        if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                                            markMoveableCell(moveCol, moveRow);
                                        }
                                    }
                                }
                            }
                            break;
                        case "sentinel":
//                            System.out.println("Sentinel move path: " + moveCol + " " + moveRow + " " + targetCellPiece.getName() + " " + targetCellPiece.getIsWhite() + " " + pieceInfo.getIsWhite());
//                            normal diagonal moves
                            if (
                                moveCol == col + 1 && moveRow == row + 1 ||
                                moveCol == col + 1 && moveRow == row - 1 ||
                                moveCol == col - 1 && moveRow == row + 1 ||
                                moveCol == col - 1 && moveRow == row - 1
                            ) {
//                                only mark if target cell is empty regardless
                                if (targetCellPiece.getName().equals("empty")) {
                                    markMoveableCell(moveCol, moveRow);
                                }
                            }
//                            special x and y moves

//                            y axis check
                            if (moveCol == col) {
                                if (moveRow > row) {
//                                    checking v direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelYpBlocked = true;
                                    } else {
                                        if (
                                            moveRow > sentinelYpMoveable &&
                                            !sentinelYpBlocked
                                        ) {
                                            sentinelYpMoveable = moveRow;
                                        }
                                    }
                                } else {
//                                    checking ^ direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelYnBlocked = true;
                                    } else {
                                        if (
                                            moveRow < sentinelYnMoveable &&
                                            !sentinelYnBlocked
                                        ) {
                                            sentinelYnMoveable = moveRow;
                                        }
                                    }
                                }
                            } else if (moveRow == row) {
                                if (moveCol > col) {
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelXpBlocked = true;
                                    } else {
                                        if (
                                            moveCol > sentinelXpMoveable &&
                                            !sentinelXpBlocked
                                        ) {
                                            sentinelXpMoveable = moveCol;
                                        }
                                    }
                                } else {
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelXnBlocked = true;
                                    } else {
                                        if (
                                            moveCol < sentinelXnMoveable &&
                                            !sentinelXnBlocked
                                        ) {
                                            sentinelXnMoveable = moveCol;
                                        }
                                    }
                                }
                            }
                            break;

                        default:
                            break;
//                            markMoveableCell(moveCol, moveRow);
                    }
                }

//                marking sentinel's x and y furtherest moveable\
                if (selectedPiece.getName().equals("sentinel")) {
                    if (sentinelYpMoveable > -1) {
                        int moveRow = sentinelYpMoveable + 1;
                        if (!(moveRow < 0 || moveRow > 7)) {
                            markMoveableCell(col, moveRow);
                        }
                    }
                    if (sentinelXpMoveable > -1) {
                        int moveCol = sentinelXpMoveable + 1;
                        if (!(moveCol < 0 || moveCol > 9)) {
                            markMoveableCell(moveCol, row);
                        }
                    }
                    if (sentinelYnMoveable < row) {
                        int moveRow = sentinelYnMoveable - 1;
                        if (!(moveRow < 0 || moveRow > 7)) {
                            markMoveableCell(col, moveRow);
                        }
                    }
                    if (sentinelXnMoveable < col) {
                        int moveCol = sentinelXnMoveable - 1;
                        if (!(moveCol < 0 || moveCol > 9)) {
                            markMoveableCell(moveCol, row);
                        }
                    }
                }

                System.out.println("move path targetCellPiece: " + path);
            }
            if (selectedPiece != null && pieceInfo.getName().equals("moveable")) {
//                check for legal moves

//              capture pieces along sentinel's path
                if (selectedPiece.getName().equals("sentinel")) {
                    int[][] sentinelYnPath = new int[][]{
                        {selectedPiece.getCol(), selectedPiece.getRow() - 1}, {selectedPiece.getCol(), selectedPiece.getRow() - 2}, {selectedPiece.getCol(), selectedPiece.getRow() - 3}, {selectedPiece.getCol(), selectedPiece.getRow() - 4}, {selectedPiece.getCol(), selectedPiece.getRow() - 5}, {selectedPiece.getCol(), selectedPiece.getRow() - 6}, {selectedPiece.getCol(), selectedPiece.getRow() - 7}
                    };
                    int[][] sentinelYpPath = new int[][]{
                        {selectedPiece.getCol(), selectedPiece.getRow() + 1}, {selectedPiece.getCol(), selectedPiece.getRow() + 2}, {selectedPiece.getCol(), selectedPiece.getRow() + 3}, {selectedPiece.getCol(), selectedPiece.getRow() + 4}, {selectedPiece.getCol(), selectedPiece.getRow() + 5}, {selectedPiece.getCol(), selectedPiece.getRow() + 6}, {selectedPiece.getCol(), selectedPiece.getRow() + 7}
                    };
                    int[][] sentinelXnPath = new int[][]{
                        {selectedPiece.getCol() - 1, selectedPiece.getRow()}, {selectedPiece.getCol() - 2, selectedPiece.getRow()}, {selectedPiece.getCol() - 3, selectedPiece.getRow()}, {selectedPiece.getCol() - 4, selectedPiece.getRow()}, {selectedPiece.getCol() - 5, selectedPiece.getRow()}, {selectedPiece.getCol() - 6, selectedPiece.getRow()}, {selectedPiece.getCol() - 7, selectedPiece.getRow()}, {selectedPiece.getCol() - 8, selectedPiece.getRow()}, {selectedPiece.getCol() - 9, selectedPiece.getRow()}
                    };
                    int[][] sentinelXpPath = new int[][]{
                        {selectedPiece.getCol() + 1, selectedPiece.getRow()}, {selectedPiece.getCol() + 2, selectedPiece.getRow()}, {selectedPiece.getCol() + 3, selectedPiece.getRow()}, {selectedPiece.getCol() + 4, selectedPiece.getRow()}, {selectedPiece.getCol() + 5, selectedPiece.getRow()}, {selectedPiece.getCol() + 6, selectedPiece.getRow()}, {selectedPiece.getCol() + 7, selectedPiece.getRow()}, {selectedPiece.getCol() + 8, selectedPiece.getRow()}, {selectedPiece.getCol() + 9, selectedPiece.getRow()}
                    };

//                    get which direction move is made
                    if (col == selectedPiece.getCol()) {
//                        move is made on y axis
                        if (row > selectedPiece.getRow()) {
//                            move is made in v direction
                            for (int[] move : sentinelYpPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveRow < 0 || moveRow > 7) continue;
                                PieceInfo targetCellPiece = getPieceByCordinates(moveCol, moveRow);
                                if (moveRow > sentinelYpMoveable) continue;
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        } else {
//                            move is made in ^ direction
                            for (int[] move : sentinelYnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveRow < 0 || moveRow > 7) continue;
                                PieceInfo targetCellPiece = getPieceByCordinates(moveCol, moveRow);
                                if (moveRow < sentinelYnMoveable) continue;
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        }
                    } else if (row == selectedPiece.getRow()) {
//                        move is made on x axis
                        if (col > selectedPiece.getCol()) {
//                            move is made in > direction
                            for (int[] move : sentinelXpPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveCol < 0 || moveCol > 9) continue;
                                PieceInfo targetCellPiece = getPieceByCordinates(moveCol, moveRow);
                                if (moveCol > sentinelXpMoveable) continue;
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        } else {
//                            move is made in < direction
                            for (int[] move : sentinelXnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveCol < 0 || moveCol > 9) continue;
                                PieceInfo targetCellPiece = getPieceByCordinates(moveCol, moveRow);
                                if (moveCol < sentinelXnMoveable) continue;
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        }
                    }
                }
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
                square.setStyle((row + col) % 2 == 0 ? "-fx-background-color: #5d4037;" : "-fx-background-color: #795548;");
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
        for (int i = 1; i <= 10; i++) {
            board.add(new Pawn(false, String.valueOf(i * 10)), i - 1, 1);
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
        for (int i = 1; i <= 10; i++) {
            board.add(new Pawn(true, String.valueOf(i * 10)), i - 1, 6);
        }
    }

    private PieceInfo getPieceByCordinates(int col, int row) {
        PieceInfo pieceInfo = new PieceInfo();
        board.getChildren().forEach(node -> {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                if (!node.getClass().getSimpleName().equals("Pane")) {
                    pieceInfo.parseId(node.getId());
                    pieceInfo.setCol(col);
                    pieceInfo.setRow(row);
                }
            }
        });
        return pieceInfo;
    }

    private void removePieceById(String id) {
        for (Node node : board.getChildren()) {
            if (node.getId() == null) continue;
            if (node.getId().equals(id)) {
                System.out.println("Removing piece with ID: " + node.getId());
                board.getChildren().remove(node);
                break;
            }
        }
    }

    private void capturePiece(PieceInfo targetCellPiece) {
        if (!targetCellPiece.getName().equals("empty")) {
            System.out.println("Capture piece: " + targetCellPiece.getId());
            removePieceById(targetCellPiece.getId());
        }
    }

    private void movePiece(int col, int row) {
        if (selectedPiece == null) {
            System.out.println("No piece selected");
            return;
        }
        System.out.println("Moving piece: " + selectedPiece.getName() + " from col: " + selectedPiece.getCol() + ", row: " + selectedPiece.getRow() + " to col: " + col + ", row: " + row);
        PieceInfo targetCellPiece = getPieceByCordinates(col, row);
        if (
            targetCellPiece.getName().equals("empty") ||
            targetCellPiece.getIsWhite() != selectedPiece.getIsWhite()
        ) {
//        if piece is captureable, remove it
            capturePiece(targetCellPiece);
        } else {
            return;
        }
        removePieceById(selectedPiece.getId());

        if (selectedPiece.getName().equals("pawn")) {
            markPawnMoved(selectedPiece);
        }

//        Create a new piece and add it to the board
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
        isWhiteTurn = !isWhiteTurn;
    }

    private void markPawnMoved(PieceInfo selectedPiece) {
        if (selectedPiece.getUniqueId().endsWith("0")) {
            selectedPiece.updateUniqueId(String.valueOf(Integer.parseInt(selectedPiece.getUniqueId()) / 10));
        }
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

    private void markMoveableCell(int col, int row) {
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

    public static int[][] append(int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
