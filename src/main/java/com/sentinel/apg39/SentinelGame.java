package com.sentinel.apg39;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SentinelGame extends Application {
    public static final boolean FLIPBOARD_ENABLED = true;

    public static final int BOARD_ROW_SIZE = 8;
    public static final int BOARD_COL_SIZE = 10;
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;
    public static final int CELL_SIZE = 55;

    public static final int WINDOW_MARGIN = 25;

    public List<String> moveHistory = new ArrayList<>();
    public ListView<String> moveHistoryListView = new ListView<String>();

    private PieceInfo selectedPiece = null;
    private boolean isWhiteTurn = true;
    public PieceInfo[] whiteCaptured = new PieceInfo[16];
    public PieceInfo[] blackCaptured = new PieceInfo[16];
    public boolean isWhiteChecked = false;
    public boolean isBlackChecked = false;

    public GridPane board = getGameBoard();
    public HBox whiteCapturedPieceDisplay = new HBox();
    public HBox blackCapturedPieceDisplay = new HBox();

    int sentinelXpMoveable = -1;
    int sentinelXnMoveable = -1;
    int sentinelYpMoveable = -1;
    int sentinelYnMoveable = -1;

    @Override
    public void start(Stage stage) {
//        Rectangle saveRec = new Rectangle(CELL_SIZE, CELL_SIZE);
//        saveRec.setFill(Color.GREEN);
//        saveRec.setOnMouseClicked(event -> {
//            System.out.println("Save button clicked");
//            GameSave gameSave = new GameSave(board, whiteCaptured, blackCaptured, isWhiteTurn);
//            gameSave.saveGame();
//        });
//
//        Rectangle loadSaveRec = new Rectangle(CELL_SIZE, CELL_SIZE);
//        loadSaveRec.setFill(Color.BLUE);
//        loadSaveRec.setOnMouseClicked(event -> {
//            System.out.println("Load button clicked");
//            GameSave gameSave = new GameSave(board, whiteCaptured, blackCaptured, isWhiteTurn);
//            board.getChildren().removeIf(node -> node instanceof Piece);
//            gameSave.loadGame();
//            isWhiteTurn = gameSave.getPlayingSide();
//
//            updateCapturedPiecesDisplay();
//        });
//
//        HBox saveBox = new HBox();
//        saveBox.getChildren().addAll(saveRec, loadSaveRec);
//        saveBox.setAlignment(Pos.TOP_LEFT);
//        saveBox.setPadding(new Insets(10));
//        saveBox.setStyle("-fx-background-color: #f0f0f0;");

//        VBox capturedPanelComponent = new VBox();
//        VBox.setMargin(capturedPanelComponent, new Insets(50));
//        capturedPanelComponent.setStyle("-fx-background-color: #f0f0f0;");
//        capturedPanelComponent.getChildren().addAll(whiteCapturedText, whiteCapturedPieceDisplay, blackCapturedText, blackCapturedPieceDisplay);

//        VBox sidePanel = new VBox();
//        sidePanel.getChildren().addAll(saveBox, capturedPanelComponent);

//        HBox hBox = new HBox();
//        hBox.getChildren().addAll(board, sidePanel);

//      Main layout

        HBox window = new HBox();
        GameWindow gw = new GameWindow(board, moveHistoryListView, whiteCapturedPieceDisplay, blackCapturedPieceDisplay);
        window.getChildren().addAll(gw.leftWindowPanel(), gw.rightWindowPanel());
        window.setStyle("-fx-background-color: grey;");

        Scene scene = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();

//        Add pieces to the board
        startingPieceLayout();

//        check board logic
        board.setOnMouseClicked(event -> {
//            System.out.println("Clicked on (" + event.getX() + ", " + event.getY() + ")");
            int col = (int) (event.getX()) / CELL_SIZE;
            int row = (int) (event.getY()) / CELL_SIZE;

            onBoardClick(col, row);
        });
    }

    private void onBoardClick(int col, int row) {
        PieceInfo pieceInfo = getPieceByCoordinate(col, row);
        System.out.println("Clicked on (" + col + ", " + row + "), Piece: " + pieceInfo.getId() + ", Name: " + pieceInfo.getName() + ", Side: " + pieceInfo.getIsWhiteString());

        removeHighlight();
        if (
            !pieceInfo.isUnoccupied() &&
            (
                pieceInfo.getId().startsWith("white") ||
                pieceInfo.getId().startsWith("black")
            )
        ) {
            if (
                isWhiteTurn && pieceInfo.getId().startsWith("black") ||
                !isWhiteTurn && pieceInfo.getId().startsWith("white")
            ) {
//                System.out.println("Not your turn");
                return;
            }
            selectedPiece = pieceInfo;
            highlightCell(col, row);

            getMoveableCells(pieceInfo);
        }

//            If user selected cell is marked "moveable"
        if (selectedPiece != null && pieceInfo.isMoveable()) { // || pieceInfo.isUnoccupied()
//              capture pieces along sentinel's path
            if (selectedPiece.isSentinel()) {
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
//                Castling swap
            if (selectedPiece.isKing()) {
                PieceInfo tmpKingPiece = selectedPiece;
                if (col == 3) {
                    selectedPiece = getPieceByCoordinate(0, selectedPiece.getRow());
                    movePiece(4, selectedPiece.getRow());
                } else if (col == 7) {
                    selectedPiece = getPieceByCoordinate(9, selectedPiece.getRow());
                    movePiece(6, selectedPiece.getRow());
                }
                selectedPiece = tmpKingPiece;
            }

            moveHistoryListView.getItems().add(0, "Moved " + selectedPiece.getName() + " from (" + selectedPiece.getCol() + ", " + selectedPiece.getRow() + ") to (" + col + ", " + row + ")");

            movePiece(col, row);
            isWhiteTurn = !isWhiteTurn;
            if (FLIPBOARD_ENABLED) flipBoard();
        }
    }

    private void flipBoard() {
        board.setRotate(board.getRotate() + 180);

        for (Node node : board.getChildren()) {
            if (node.getId() == null) continue;
            if (node.getId().startsWith("white_") || node.getId().startsWith("black_")){
                node.setRotate(node.getRotate() + 180);
            }
        }
    }

    private void getMoveableCells(PieceInfo pieceInfo) {
        int col = pieceInfo.getCol();
        int row = pieceInfo.getRow();

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

//            For castling
        String queenSideRookId = pieceInfo.getIsWhite() ? "white_rook_10" : "black_rook_10";
        String kingSideRookId = pieceInfo.getIsWhite() ? "white_rook_20" : "black_rook_20";
        PieceInfo queenSideRook = getPieceById(queenSideRookId);
        PieceInfo kingSideRook = getPieceById(kingSideRookId);

        for (int[] move : pieceInfo.getMoves(col, row)) {
            int moveCol = move[0];
            int moveRow = move[1];
            if (moveCol < 0 || moveCol >= BOARD_COL_SIZE || moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
//                    check if move is valid and block "moveable" from being placed
            PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
//                path += "- " + targetCellPiece.getName() + " " + moveCol + " " + moveRow + " \n";

            boolean isRowMove = moveRow == row;
            boolean isColMove = moveCol == col;
            boolean isMovingUp = moveRow < row;
            boolean isMovingDown = moveRow > row;
            boolean isMovingRight = moveCol > col;
            boolean isMovingLeft = moveCol < col;
            boolean isEnemyPiece = !targetCellPiece.isUnoccupied() && targetCellPiece.getIsWhite() != pieceInfo.getIsWhite();

            switch (pieceInfo.getName()) {
                case "rook":
                    markRookMoves(
                            pieceInfo, targetCellPiece,
                            isColMove, isRowMove, isMovingDown, isMovingRight, isMovingLeft, isMovingUp,
                            rookXp, rookXn, rookYp, rookYn
                    );
                    break;
                case "king":
//                        if King has not moved
//                        if left or right Rook has not moved
//                        if path in between is clear
//                        check if path King x+1&x+2 or x-1&x-2 is clear from enemy checking path
//                        mark moveable at x+2 or x-2

//                        if King has not moved
                    if (targetCellPiece.hasNotMoved()) {
//                            if left or right Rook has not moved
                        if (queenSideRook.getId() != null) {
//                                if path in between is clear
                            if (
                                getPieceByCoordinate(4, targetCellPiece.getRow()).isUnoccupied() &&
                                getPieceByCoordinate(3, targetCellPiece.getRow()).isUnoccupied() &&
                                getPieceByCoordinate(2, targetCellPiece.getRow()).isUnoccupied() &&
                                getPieceByCoordinate(1, targetCellPiece.getRow()).isUnoccupied()
                            ) {
//                                    (TODO) check if path King x+1&x+2 or x-1&x-2 is clear from enemy checking path
//                                     mark moveable at x+2 or x-2
                                markMoveableCell(3, targetCellPiece.getRow());
                            }
                        }
                        if (kingSideRook.getId() != null) {
//                                if path in between is clear
                            if (
                                getPieceByCoordinate(6, targetCellPiece.getRow()).isUnoccupied() &&
                                getPieceByCoordinate(7, targetCellPiece.getRow()).isUnoccupied() &&
                                getPieceByCoordinate(8, targetCellPiece.getRow()).isUnoccupied()
                            ) {
//                                    (TODO) check if path King x+1&x+2 or x-1&x-2 is clear from enemy checking path
//                                     mark moveable at x+2 or x-2
                                markMoveableCell(7, targetCellPiece.getRow());
                            }
                        }
                    }
                case "knight":
                    if (targetCellPiece.isUnoccupied()) {
                        markMoveableCell(moveCol, moveRow);
                    } else if (isEnemyPiece) {
                        markMoveableCell(moveCol, moveRow);
                    }
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
                        col == moveCol &&
                        (
                            !targetCellPiece.isUnoccupied() &&
                            targetCellPiece.getIsWhite() == isWhiteTurn
                        )
                    ) {
                        pawnY.setIsBlocked(true);
                        break;
                    }
//                            pawn is blocked by enemy, prevent from placing moveable at 2 front
                    if (
                        pawnY.getIsBlocked() &&
                        col == moveCol &&
                        (
                            row + 2 == moveRow ||
                            row - 2 == moveRow
                        )
                    ) {
                        break;
                    }

//                            Pawn with id that ends with 0 indicates it is it's first move
                    if (pieceInfo.hasNotMoved()) {
//                              Spawn moveable if forward is empty or forward left or right has enemy piece
                        if (
                            moveCol == col ||
                            (
                                !targetCellPiece.isUnoccupied() &&
                                isEnemyPiece
                            )
                        ) {
//                                    Prevent spawning of moveable if forward same col has enemy piece
                            if (
                                moveCol == col &&
                                !targetCellPiece.isUnoccupied() &&
                                isEnemyPiece
                            ) {
                                pawnY.setIsBlocked(true);
                                break;
                            }
                            markMoveableCell(moveCol, moveRow);
                        }
//                                Indicate pawn is blocked if forward path contains enemy piece
                        if (
                            moveCol == col &&
                            !targetCellPiece.isUnoccupied() &&
                            isEnemyPiece
                        ) {
                            pawnY.setIsBlocked(true);
                        }
                    } else {
//                                Ignore all moveable cell if length is longer than 1
                        if (
                            moveRow - row > 1 ||
                            moveRow - row < -1
                        ) continue;
//                                Spawn moveable if cell in-front forward is empty
                        if (
                            moveCol == col
                        ) {
                            if (targetCellPiece.isUnoccupied()) {
                                markMoveableCell(moveCol, moveRow);
                            }
                        } else {
//                                    Spawn moveable if forward left or right has enemy piece
                            if (
                                !targetCellPiece.isUnoccupied() &&
                                isEnemyPiece
                            ) {
                                markMoveableCell(moveCol, moveRow);
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
                        if (targetCellPiece.isUnoccupied()) {
                            markMoveableCell(moveCol, moveRow);
                        }
                    }
//                            special x and y moves

//                            y axis check
                    if (isColMove) {
                        if (isMovingDown) {
//                                    checking v direction
                            if (targetCellPiece.isUnoccupied()) {
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
                            if (targetCellPiece.isUnoccupied()) {
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
                            if (targetCellPiece.isUnoccupied()) {
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
                            if (targetCellPiece.isUnoccupied()) {
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
        if (pieceInfo.isSentinel()) {
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
        if (targetCellPiece.isUnoccupied()) {
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
        board.add(new Rook(false, "10"), 0, 0);
        board.add(new Sentinel(false, "10"), 1, 0);
        board.add(new Knight(false, "10"), 2, 0);
        board.add(new Bishop(false, "10"), 3, 0);
        board.add(new Queen(false, "10"), 4, 0);
        board.add(new King(false, "10"), 5, 0);
        board.add(new Bishop(false, "20"), 6, 0);
        board.add(new Knight(false, "20"), 7, 0);
        board.add(new Sentinel(false, "20"), 8, 0);
        board.add(new Rook(false, "20"), 9, 0);
        for (int i = 1; i <= 9; i++) {
            board.add(new Pawn(false, String.valueOf(i * 10)), i - 1, 1);
        }
        board.add(new Pawn(false, String.valueOf(110)), 9, 1);

        board.add(new Rook(true, "10"), 0, 7);
        board.add(new Sentinel(true, "10"), 1, 7);
        board.add(new Knight(true, "10"), 2, 7);
        board.add(new Bishop(true, "10"), 3, 7);
        board.add(new Queen(true, "10"), 4, 7);
        board.add(new King(true, "10"), 5, 7);
        board.add(new Bishop(true, "20"), 6, 7);
        board.add(new Knight(true, "20"), 7, 7);
        board.add(new Sentinel(true, "20"), 8, 7);
        board.add(new Rook(true, "20"), 9, 7);
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

    private PieceInfo getPieceById(String id) {
        PieceInfo pieceInfo = new PieceInfo();
        board.getChildren().forEach(node -> {
            if (node.getId() != null && node.getId().equals(id)) {
                pieceInfo.parseId(node.getId());
                int col = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);

                pieceInfo.setCol(col);
                pieceInfo.setRow(row);
            }
        });
        return pieceInfo;
    }

    private void removePieceById(String id) {
        for (Node node : board.getChildren()) {
            if (node.getId() == null) continue;
            if (node.getId().equals(id)) {
//                System.out.println("Removing piece with ID: " + node.getId());
                board.getChildren().remove(node);
                break;
            }
        }
    }

    private void capturePiece(PieceInfo targetCellPiece) {
        if (targetCellPiece.isUnoccupied()) return;
        System.out.println("Captured piece: " + targetCellPiece.getId());
        removePieceById(targetCellPiece.getId());

        if (targetCellPiece.getIsWhite()) {
            for (int i = 0; i < whiteCaptured.length; i++) {
                if (whiteCaptured[i] == null) {
                    whiteCaptured[i] = targetCellPiece;
                    break;
                }
            }
        } else {
            for (int i = 0; i < blackCaptured.length; i++) {
                if (blackCaptured[i] == null) {
                    blackCaptured[i] = targetCellPiece;
                    break;
                }
            }
        }

        updateCapturedPiecesDisplay();
    }

    private void updateCapturedPiecesDisplay() {
        //        display all captured pieces
        whiteCapturedPieceDisplay.getChildren().clear();
        blackCapturedPieceDisplay.getChildren().clear();
        String whiteCapturedPieces = "";
        int whiteCapturedIndex = 1;
        int whiteCapturedCount = 0;
        for (PieceInfo piece : whiteCaptured) {
            if (piece == null) continue;
            whiteCapturedCount += 1;
            whiteCapturedPieces += piece.getId() + ", ";

            if (whiteCapturedIndex > 3) {
                continue;
            }

            Piece sidePanelPiece = getPieceByName(piece);
            whiteCapturedPieceDisplay.getChildren().add(sidePanelPiece);

            whiteCapturedIndex++;
        }
        System.out.println("White captured: " + whiteCapturedPieces);
        if (whiteCapturedCount > 3) {
            for (Node node : whiteCapturedPieceDisplay.getChildren()) {
                if (node.getId() == null) continue;
                if (node.getId().equals("white_captured_text")) {
                    whiteCapturedPieceDisplay.getChildren().remove(node);
                    break;
                }
            }
            Text whiteCapturedText = new Text(" +" + (whiteCapturedCount - 3) + " more");
            whiteCapturedText.setId("white_captured_text");
            whiteCapturedPieceDisplay.getChildren().add(whiteCapturedText);
        }

        String blackCapturedPieces = "";
        int blackCapturedIndex = 1;
        int blackCapturedCount = 0;
        for (PieceInfo piece : blackCaptured) {
            if (piece == null) continue;
            blackCapturedCount += 1;
            blackCapturedPieces += piece.getId() + ", ";

            if (blackCapturedIndex > 3) {
                continue;
            }

            Piece sidePanelPiece = getPieceByName(piece);
            blackCapturedPieceDisplay.getChildren().add(sidePanelPiece);

            blackCapturedIndex++;
        }
        System.out.println("Black captured: " + blackCapturedPieces);
        if (blackCapturedCount > 3) {
            for (Node node : blackCapturedPieceDisplay.getChildren()) {
                if (node.getId() == null) continue;
                if (node.getId().equals("black_captured_text")) {
                    blackCapturedPieceDisplay.getChildren().remove(node);
                    break;
                }
            }
            Text blackCapturedText = new Text(" +" + (blackCapturedCount - 3) + " more");
            blackCapturedText.setId("black_captured_text");
            blackCapturedPieceDisplay.getChildren().add(blackCapturedText);
        }
    }

    private void movePiece(int col, int row) {
        if (selectedPiece == null) {
            System.out.println("No piece selected, move action cancelled.");
            return;
        }
        System.out.println("Moving piece: " + selectedPiece.getId() + " from (" + selectedPiece.getCol() + ", " + selectedPiece.getRow() + ") to (" + col + ", " + row + ")");
        PieceInfo targetCellPiece = getPieceByCoordinate(col, row);
        if (
            targetCellPiece.isUnoccupied() ||
            targetCellPiece.getIsWhite() != selectedPiece.getIsWhite()
        ) {
//        if piece is capture-able, remove it
            capturePiece(targetCellPiece);
        } else {
            return;
        }
        removePieceById(selectedPiece.getId());

        markPieceMoved(selectedPiece);

//        Create a new piece and add it to the board
        Piece piece = getPieceByName(selectedPiece);
        if (!isWhiteTurn && FLIPBOARD_ENABLED) piece.setRotate(180);
        board.add(piece, col, row);

        moveHistory.add("Moved " + selectedPiece.getName() + " from (" + selectedPiece.getCol() + ", " + selectedPiece.getRow() + ") to (" + col + ", " + row + ")");

        selectedPiece = null;
        isWhiteChecked = false;
        isBlackChecked = false;

        PieceInfo newPiece = getPieceByCoordinate(col, row);
        checkIsChecked(newPiece);
    }

    public Piece getPieceByName(PieceInfo piece) {
        return switch (piece.getName()) {
            case "pawn" -> new Pawn(piece.getIsWhite(), piece.getUniqueId());
            case "rook" -> new Rook(piece.getIsWhite(), piece.getUniqueId());
            case "knight" -> new Knight(piece.getIsWhite(), piece.getUniqueId());
            case "bishop" -> new Bishop(piece.getIsWhite(), piece.getUniqueId());
            case "queen" -> new Queen(piece.getIsWhite(), piece.getUniqueId());
            case "king" -> new King(piece.getIsWhite(), piece.getUniqueId());
            case "sentinel" -> new Sentinel(piece.getIsWhite(), piece.getUniqueId());
            default -> null;
        };
    }

    private void checkIsChecked(PieceInfo piece) {
        getMoveableCells(piece);
        board.getChildren().forEach(node -> {
            if (node.getId() != null && node.getId().startsWith("none_moveable_")) {
                int col = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);
//                Get piece at grid ignoring highlights
                PieceInfo targetCellPiece = new PieceInfo();
                board.getChildren().forEach(n -> {
                    if (GridPane.getColumnIndex(n) == col && GridPane.getRowIndex(n) == row) {
                        if (n.getClass().getSimpleName().equals("Pane")) return;
                        if (n.getId() == null) return;
                        if (n.getId().startsWith("none_moveable_")) return;
                        targetCellPiece.parseId(n.getId());
                    }
                });
                targetCellPiece.setCol(col);
                targetCellPiece.setRow(row);
                if (targetCellPiece.isKing()) {
                    System.out.println("King is checked");
                    if (piece.getIsWhite()) {
                        isWhiteChecked = true;
                    } else {
                        isBlackChecked = true;
                    }
                }
            }
        });
        removeHighlight();
//        (TODO) detect sentinel capture path
    }

    private void markPieceMoved(PieceInfo selectedPiece) {
        if (selectedPiece.hasNotMoved()) {
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
        if (getPieceByCoordinate(col, row).isMoveable()) return;
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
            } else if (node.getId() != null && node.getId().startsWith("none_moveable_")) {
                nodesToRemove.add(node);
            } else if (node.getClass().getSimpleName().equals("StackPane")) {
                nodesToRemove.add(node);
            } else if (node.getClass().getSimpleName().equals("Rectangle")) {
                nodesToRemove.add(node);
            }
        });

        board.getChildren().removeAll(nodesToRemove);
    }
}
