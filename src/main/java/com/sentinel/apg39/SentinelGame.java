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
    private static final int BOARD_ROW_SIZE = 8;
    private static final int BOARD_COL_SIZE = 10;
    public static final int CELL_SIZE = 50;

    private PieceInfo selectedPiece = null;
    private boolean isWhiteTurn = true;

    GridPane board = getGameBoard();

    int sentinelXpMoveable = -1;
    int sentinelXnMoveable = -1;
    int sentinelYpMoveable = -1;
    int sentinelYnMoveable = -1;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(board, 700, 600);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();

//        Add pieces to the board
        startingPieceLayout();

//        check board logic
        board.setOnMouseClicked(event -> {
            int col = (int) (event.getX() - 100) / CELL_SIZE;
            int row = (int) (event.getY() - 100) / CELL_SIZE;

            PieceInfo pieceInfo = getPieceByCoordinate(col, row);
            System.out.println("Clicked on (" + col + ", " + row + "), Piece: " + pieceInfo.getId() + ", Name: " + pieceInfo.getName() + ", Side: " + pieceInfo.getIsWhiteString());

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

                PieceBlocked rookXp = new PieceBlocked();
                PieceBlocked rookXn = new PieceBlocked();
                PieceBlocked rookYp = new PieceBlocked();
                PieceBlocked rookYn = new PieceBlocked();

                PieceBlocked bishopXpYp = new PieceBlocked();
                PieceBlocked bishopXpYn = new PieceBlocked();
                PieceBlocked bishopXnYp = new PieceBlocked();
                PieceBlocked bishopXnYn = new PieceBlocked();

                PieceBlocked queenXpYp = new PieceBlocked();
                PieceBlocked queenXpYn = new PieceBlocked();
                PieceBlocked queenXnYp = new PieceBlocked();
                PieceBlocked queenXnYn = new PieceBlocked();
                PieceBlocked queenXp = new PieceBlocked();
                PieceBlocked queenXn = new PieceBlocked();
                PieceBlocked queenYp = new PieceBlocked();
                PieceBlocked queenYn = new PieceBlocked();

                PieceBlocked pawnY = new PieceBlocked();

                PieceBlocked sentinelXp = new PieceBlocked();
                PieceBlocked sentinelXn = new PieceBlocked();
                PieceBlocked sentinelYp = new PieceBlocked();
                PieceBlocked sentinelYn = new PieceBlocked();

                sentinelXpMoveable = -1;
                sentinelYpMoveable = -1;
                sentinelYnMoveable = row;
                sentinelXnMoveable = col;

                for (int[] move : pieceInfo.getMoves(col, row)) {
                    int moveCol = move[0];
                    int moveRow = move[1];
                    if (moveCol < 0 || moveCol >= BOARD_COL_SIZE || moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
//                    check if move is valid and block "moveable" from being placed
                    PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                    path += "- " + targetCellPiece.getName() + " " + moveCol + " " + moveRow + " \n";

                    boolean isRowMove = moveRow == row;
                    boolean isColMove = moveCol == col;
                    boolean isMovingUp = moveRow < row;
                    boolean isMovingDown = moveRow > row;
                    boolean isMovingRight = moveCol > col;
                    boolean isMovingLeft = moveCol < col;

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
                            markRookMoves(
                                    pieceInfo, targetCellPiece,
                                    isColMove, isRowMove, isMovingDown, isMovingRight, isMovingLeft, isMovingUp,
                                    rookXp, rookXn, rookYp, rookYn
                            );
                            break;
                        case "bishop":
                            markBishopMoves(
                                    pieceInfo, targetCellPiece,
                                    isColMove, isRowMove, isMovingDown, isMovingRight, isMovingLeft, isMovingUp,
                                    bishopXpYp, bishopXpYn, bishopXnYp, bishopXnYn
                            );
                            break;
                        case "queen":
                            markRookMoves(
                                    pieceInfo, targetCellPiece,
                                    isColMove, isRowMove, isMovingDown, isMovingRight, isMovingLeft, isMovingUp,
                                    queenXp, queenXn, queenYp, queenYn
                            );
                            markBishopMoves(
                                    pieceInfo, targetCellPiece,
                                    isColMove, isRowMove, isMovingDown, isMovingRight, isMovingLeft, isMovingUp,
                                    queenXpYp, queenXpYn, queenXnYp, queenXnYn
                            );
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
                                pawnY.setIsBlocked(true);
                                break;
                            }
//                            pawn is blocked by enemy, prevent from placing moveable at 2 front
                            if (
                                pawnY.getIsBlocked() &&
                                selectedPiece.getCol() == moveCol &&
                                (
                                    selectedPiece.getRow() + 2 == moveRow ||
                                    selectedPiece.getRow() - 2 == moveRow
                                )
                            ) {
                                break;
                            }

//                            Pawn with id that ends with 0 indicates it is it's first move
                            if (selectedPiece.getUniqueId().endsWith("0")) {
//                              Spawn moveable if forward is empty or forward left or right has enemy piece
                                if (
                                    moveCol == selectedPiece.getCol() ||
                                    (
                                        !targetCellPiece.getName().equals("empty") &&
                                        targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()
                                    )
                                ) {
//                                    Prevent spawning of moveable if forward same col has enemy piece
                                    if (
                                        moveCol == selectedPiece.getCol() &&
                                        !targetCellPiece.getName().equals("empty") &&
                                        targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()
                                    ) {
                                        pawnY.setIsBlocked(true);
                                        break;
                                    }
                                    markMoveableCell(moveCol, moveRow);
                                }
//                                Indicate pawn is blocked if forward path contains enemy piece
                                if (
                                    moveCol == selectedPiece.getCol() &&
                                    !targetCellPiece.getName().equals("empty") &&
                                    targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()
                                ) {
                                    pawnY.setIsBlocked(true);
                                }
                            } else {
//                                Ignore all moveable cell if length is longer than 1
                                if (
                                    moveRow - selectedPiece.getRow() > 1 ||
                                    moveRow - selectedPiece.getRow() < -1
                                ) continue;
//                                Spawn moveable if cell in-front forward is empty
                                if (moveCol == selectedPiece.getCol()) {
                                    if (targetCellPiece.getName().equals("empty")) {
                                        markMoveableCell(moveCol, moveRow);
                                    }
                                } else {
//                                    Spawn moveable if forward left or right has enemy piece
                                    if (!targetCellPiece.getName().equals("empty")) {
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
                            if (isColMove) {
                                if (isMovingDown) {
//                                    checking v direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelYp.setIsBlocked(true);
                                    } else {
                                        if (
                                            moveRow > sentinelYpMoveable &&
                                            !sentinelYp.getIsBlocked()
                                        ) {
                                            sentinelYpMoveable = moveRow;
                                        }
                                    }
                                } else {
//                                    checking ^ direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelYn.setIsBlocked(true);
                                    } else {
                                        if (
                                            moveRow < sentinelYnMoveable &&
                                            !sentinelYn.getIsBlocked()
                                        ) {
                                            sentinelYnMoveable = moveRow;
                                        }
                                    }
                                }
                            } else if (isRowMove) {
                                if (isMovingRight) {
//                                    checking > direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelXp.setIsBlocked(true);
                                    } else {
                                        if (
                                            moveCol > sentinelXpMoveable &&
                                            !sentinelXp.getIsBlocked()
                                        ) {
                                            sentinelXpMoveable = moveCol;
                                        }
                                    }
                                } else {
//                                    checking < direction
                                    if (targetCellPiece.getName().equals("empty")) {
                                        sentinelXn.setIsBlocked(true);
                                    } else {
                                        if (
                                            moveCol < sentinelXnMoveable &&
                                            !sentinelXn.getIsBlocked()
                                        ) {
                                            sentinelXnMoveable = moveCol;
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

//                marking sentinel's x and y furthest moveable
                if (selectedPiece.getName().equals("sentinel")) {
                    if (sentinelYpMoveable > -1) {
                        int moveRow = sentinelYpMoveable + 1;
                        if (!(moveRow < 0 || moveRow >= BOARD_ROW_SIZE)) {
                            markMoveableCell(col, moveRow);
                        }
                    }
                    if (sentinelXpMoveable > -1) {
                        int moveCol = sentinelXpMoveable + 1;
                        if (!(moveCol < 0 || moveCol >= BOARD_COL_SIZE)) {
                            markMoveableCell(moveCol, row);
                        }
                    }
                    if (sentinelYnMoveable < row) {
                        int moveRow = sentinelYnMoveable - 1;
                        if (!(moveRow < 0 || moveRow >= BOARD_ROW_SIZE)) {
                            markMoveableCell(col, moveRow);
                        }
                    }
                    if (sentinelXnMoveable < col) {
                        int moveCol = sentinelXnMoveable - 1;
                        if (!(moveCol < 0 || moveCol >= BOARD_COL_SIZE)) {
                            markMoveableCell(moveCol, row);
                        }
                    }
                }

                System.out.println("move path targetCellPiece: " + path);
            }
//            If user selected cell is marked "moveable"
            if (selectedPiece != null && pieceInfo.getName().equals("moveable")) { // || pieceInfo.getName().equals("empty")
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
                                if (moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
                                if (moveRow > sentinelYpMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        } else {
//                            move is made in ^ direction
                            for (int[] move : sentinelYnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
                                if (moveRow < sentinelYnMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
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
                                if (moveCol < 0 || moveCol >= BOARD_COL_SIZE) continue;
                                if (moveCol > sentinelXpMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        } else {
//                            move is made in < direction
                            for (int[] move : sentinelXnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveCol < 0 || moveCol >= BOARD_COL_SIZE) continue;
                                if (moveCol < sentinelXnMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == selectedPiece.getIsWhite()) continue;
                                capturePiece(targetCellPiece);
                            }
                        }
                    }
                }
                movePiece(col, row);
            }
        });
    }

    private void markRookMoves(
            PieceInfo pieceInfo, PieceInfo targetCellPiece,
            boolean isColMove, boolean isRowMove, boolean isMovingDown, boolean isMovingRight, boolean isMovingLeft, boolean isMovingUp,
            PieceBlocked rookXp, PieceBlocked rookXn, PieceBlocked rookYp, PieceBlocked rookYn
    ) {
        if(isColMove) {
            if (isMovingDown) {
                markNotBlockedMoves(pieceInfo, targetCellPiece, rookYp);
            } else {
                markNotBlockedMoves(pieceInfo, targetCellPiece, rookYn);
            }
        } else if(isRowMove) {
            if (isMovingRight) {
                markNotBlockedMoves(pieceInfo, targetCellPiece, rookXp);
            } else {
                markNotBlockedMoves(pieceInfo, targetCellPiece, rookXn);
            }
        }
    }

    private void markBishopMoves(
            PieceInfo pieceInfo, PieceInfo targetCellPiece,
            boolean isColMove, boolean isRowMove, boolean isMovingDown, boolean isMovingRight, boolean isMovingLeft, boolean isMovingUp,
            PieceBlocked bishopXpYp, PieceBlocked bishopXpYn, PieceBlocked bishopXnYp, PieceBlocked bishopXnYn
    ) {
        if (isMovingRight && isMovingDown) {
            markNotBlockedMoves(pieceInfo, targetCellPiece, bishopXpYp);
        } else if (isMovingRight && isMovingUp) {
            markNotBlockedMoves(pieceInfo, targetCellPiece, bishopXpYn);
        } else if (isMovingLeft && isMovingDown) {
            markNotBlockedMoves(pieceInfo, targetCellPiece, bishopXnYp);
        } else if (isMovingLeft && isMovingUp) {
            markNotBlockedMoves(pieceInfo, targetCellPiece, bishopXnYn);
        }
    }
    
    private void markNotBlockedMoves(PieceInfo pieceInfo, PieceInfo targetCellPiece, PieceBlocked pieceBlocked) {
        int moveCol = targetCellPiece.getCol();
        int moveRow = targetCellPiece.getRow();
        if (pieceBlocked.getIsBlocked()) return;
        if (targetCellPiece.getName().equals("empty")) {
            markMoveableCell(moveCol, moveRow);
        } else {
            if (targetCellPiece.getIsWhite() != pieceInfo.getIsWhite()) {
                markMoveableCell(moveCol, moveRow);
            }
            pieceBlocked.setIsBlocked(true);
        }
    }

    public static void main(String[] args) { launch(args); }

    private static GridPane getGameBoard() {
//        Chessboard
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setPadding(new Insets(10));

// Create squares and add them to the board
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                Pane square = new Pane();
                square.setPrefSize(CELL_SIZE, CELL_SIZE);
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
        for (int i = 1; i <= 9; i++) {
            board.add(new Pawn(false, String.valueOf(i * 10)), i - 1, 1);
        }
        board.add(new Pawn(false, String.valueOf(110)), 9, 1);

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
        for (int i = 1; i <= 9; i++) {
            board.add(new Pawn(true, String.valueOf(i * 10)), i - 1, 6);
        }
        board.add(new Pawn(true, String.valueOf(110)), 9, 6);
    }

    private PieceInfo getPieceByCoordinate(int col, int row) {
        PieceInfo pieceInfo = new PieceInfo();
        board.getChildren().forEach(node -> {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                if (!node.getClass().getSimpleName().equals("Pane")) {
                    pieceInfo.parseId(node.getId());
                }
            }
        });
        pieceInfo.setCol(col);
        pieceInfo.setRow(row);
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
        if (targetCellPiece.getName().equals("empty")) return;
        System.out.println("Captured piece: " + targetCellPiece.getId());
        removePieceById(targetCellPiece.getId());
    }

    private void movePiece(int col, int row) {
        if (selectedPiece == null) {
            System.out.println("No piece selected, move action cancelled.");
            return;
        }
        System.out.println("Moving piece: " + selectedPiece.getId() + " from (" + selectedPiece.getCol() + ", " + selectedPiece.getRow() + ") to (" + col + ", " + row + ")");
        PieceInfo targetCellPiece = getPieceByCoordinate(col, row);
        if (
            targetCellPiece.getName().equals("empty") ||
            targetCellPiece.getIsWhite() != selectedPiece.getIsWhite()
        ) {
//        if piece is capture-able, remove it
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
        highlight.setWidth(CELL_SIZE); // match the cell size
        highlight.setHeight(CELL_SIZE); // match the cell size
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
}
