package com.sentinel.apg39;

import javafx.application.Application;
import javafx.geometry.Pos;
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
    public ListView<String> moveHistoryListView = new ListView<>();

    private PieceInfo selectedPiece = null;
    private boolean isWhiteTurn = true;
    public PieceInfo[] whiteCaptured = new PieceInfo[16];
    public PieceInfo[] blackCaptured = new PieceInfo[16];
    public boolean isWhiteChecked = false;
    public boolean isBlackChecked = false;
    public PieceInfo checkingPiece = null;
    public List<List<Integer>> checkedPiecePath = new ArrayList<>();
    public List<List<Integer>> pathToKing = new ArrayList<>();

    public GridPane board = getGameBoard();
    public HBox whiteCapturedPieceDisplay = new HBox();
    public HBox blackCapturedPieceDisplay = new HBox();

    int sentinelXpMoveable = -1;
    int sentinelXnMoveable = -1;
    int sentinelYpMoveable = -1;
    int sentinelYnMoveable = -1;

    @Override
    public void start(Stage stage) {
//      Main layout
        HBox window = new HBox();
        GameWindow gw = new GameWindow(board, moveHistoryListView, whiteCapturedPieceDisplay, blackCapturedPieceDisplay);
        window.getChildren().addAll(gw.leftWindowPanel(), gw.rightWindowPanel());
        window.setStyle("-fx-background-image: url('file:src/main/resources/images/DarkBg.png');");

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

            removeInvalidMoveableCells();

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

            if (selectedPiece.isPawn()) {
//            Convert pawn to queen when it reaches the end of the board
                if (row == 0) {
                    if (selectedPiece.getIsWhite()) {
                        removePieceById(selectedPiece.getId());
                        board.add(new Queen(true, "9" + selectedPiece.getUniqueId()), col, 0);
                    }
                } else if (row == 9) {
                    if (!selectedPiece.getIsWhite()) {
                        removePieceById(selectedPiece.getId());
                        board.add(new Queen(false, "9" + selectedPiece.getUniqueId()), col, 9);
                    }
                }
//                Capture en passant

//                check if en passant valid if selected is at row 2 (white) or 5 (black)
                if (
                    (selectedPiece.getIsWhite() && row == 2) ||
                    (!selectedPiece.getIsWhite() && row == 5)
                ) {
//                    check if enemy is first move two forward by row 3 or 4 and color
                    int capturingPieceRow = selectedPiece.getIsWhite() ? row + 1 : row - 1;
                    PieceInfo targetCellPiece = getPieceByCoordinate(col, capturingPieceRow);

                    if (
                        (targetCellPiece.isPawn() && targetCellPiece.getIsWhite() != selectedPiece.getIsWhite()) &&
                        ( // ensure the piece only derived from same row
                            (col == 9 && Integer.parseInt(targetCellPiece.getUniqueId()) - 2 == col) ||
                            Integer.parseInt(targetCellPiece.getUniqueId()) - 1 == col
                        )
                    ) {
                        capturePiece(targetCellPiece);
                    }
                }
            }

//            moveHistoryListView.getItems().add(0, "Moved " + selectedPiece.getIsWhiteString() + " " + selectedPiece.getName() + " from (" + selectedPiece.getCol() + ", " + selectedPiece.getRow() + ") to (" + col + ", " + row + ")");
            moveHistoryListView.getItems().add(0, selectedPiece.getIsWhiteString() + " " + selectedPiece.getName() + " to " + getDisplayNotation(col, row));

            movePiece(col, row);
            isWhiteTurn = !isWhiteTurn;
            if (FLIPBOARD_ENABLED) flipBoard();
        }
    }

    private void removeInvalidMoveableCells() {
        //            if is checked, remove the "moveable" highlight cells from the piece that can not block the capture
        if (isWhiteChecked || isBlackChecked) {
//                find which moveable cell can prevent the king from capture

//                check which path of the array can lead to the capture of the king

//                get King path
            PieceInfo kingPiece = null;
            for (Node node : board.getChildren()) {
                if (node.getId() == null) continue;
                if (isBlackChecked && node.getId().startsWith("white_king")) {
                    kingPiece = getPieceByCoordinate(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                    break;
                } else if (isWhiteChecked && node.getId().startsWith("black_king")) {
                    kingPiece = getPieceByCoordinate(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                    break;
                }
            }

//                (Capture checking or king move) Pawn, King, Knight
//                (Block + axis) Rook, Queen
//                (Block x axis) Bishop, Queen
//                (King can move out of the line, do a recheck function) Sentinel

            PieceInfo rookPath = new PieceInfo();
            rookPath.parseId(kingPiece.getIsWhiteString() + "_rook_999");
            int[][] mockRookPath = rookPath.getMoves(kingPiece.getCol(), kingPiece.getRow());

            PieceInfo bishopPath = new PieceInfo();
            bishopPath.parseId(kingPiece.getIsWhiteString() + "_bishop_999");
            int[][] mockBishopPath = bishopPath.getMoves(kingPiece.getCol(), kingPiece.getRow());

            int cDLength = checkingPiece.getCol() - kingPiece.getCol();
            int cDWidth = checkingPiece.getRow() - kingPiece.getRow();
            double checkingDistance = Math.sqrt(Math.pow(cDLength, 2.0) + Math.pow(cDWidth, 2.0));
//                System.out.printf("cDLength: %d\ncDWidth: %d\nL pow: %f\nCD: %f", cDLength, cDWidth, Math.pow(cDLength, 2.0), checkingDistance);

            for (List<Integer> path : checkedPiecePath) {
                int pathCol = path.get(0);
                int pathRow = path.get(1);
//                    Capture checking piece
                if (checkingPiece.getCol() == pathCol && checkingPiece.getRow() == pathRow) {
                    pathToKing.add(path);
                    continue;
                }

                int mDLength = pathCol - kingPiece.getCol();
                int mDWidth = pathRow - kingPiece.getRow();
                double moveDistance = Math.sqrt(Math.pow(mDLength, 2.0) + Math.pow(mDWidth, 2.0));
//                    Blocking + axis (Rook, Queen)
                if (checkingPiece.isRook() || checkingPiece.isQueen()) {
                    for (int[] p : mockRookPath) {
                        int moveCol = p[0];
                        int moveRow = p[1];
                        if (moveCol < 0 || moveCol >= BOARD_COL_SIZE || moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
//                            System.out.println("col: " + p[0] + ", row: " + p[1]);
                        if (
                            (moveCol == pathCol) &&
                            (moveRow == pathRow) &&
                            (moveDistance < checkingDistance)
                        ) {
                            pathToKing.add(path);
                        }
                    }
                }

//                    Blocking x axis (Bishop, Queen)
                if (checkingPiece.isQueen() || checkingPiece.isBishop()) {
                    for (int[] p : mockBishopPath) {
                        int moveCol = p[0];
                        int moveRow = p[1];
                        if (moveCol < 0 || moveCol >= BOARD_COL_SIZE || moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
//                            System.out.println("col: " + p[0] + ", row: " + p[1]);
                        if (
                            (moveCol == pathCol) &&
                            (moveRow == pathRow) &&
                            (moveDistance < checkingDistance)
                        ) {
//                                check if movement piece is of vertical or horizontal only move and prevent the intersect of diagonal moves
                            if (
                                (
                                    (pathCol == kingPiece.getCol()) ||
                                    (pathRow == kingPiece.getRow())
                                ) &&
                                    checkingPiece.isQueen()
                            ) {
                                continue;
                            }
//                            System.out.printf("(%d, %d) - %f < %f\n", pathCol, pathRow, moveDistance, checkingDistance);
                            pathToKing.add(path);
                        }
                    }
                }
            }
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

        for (int[] move : pieceInfo.getMoves(col, row)) {
            int moveCol = move[0];
            int moveRow = move[1];
            if (moveCol < 0 || moveCol >= BOARD_COL_SIZE || moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
//                    check if move is valid and block "moveable" from being placed
            PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);

            PieceMove pieceMove = new PieceMove(board, pieceInfo, targetCellPiece, isWhiteTurn, col, row, moveCol, moveRow, pathToKing);

            switch (pieceInfo.getName()) {
                case "rook":
                    pieceMove.markRookMoves(rookXp, rookXn, rookYp, rookYn);
                    break;
                case "king":
                    pieceMove.markKingMoves();
                    pieceMove.markKnightMoves();
                    break;
                case "knight":
                    pieceMove.markKnightMoves();
                    break;
                case "bishop":
                    pieceMove.markBishopMoves(bishopXpYp, bishopXpYn, bishopXnYp, bishopXnYn);
                    break;
                case "queen":
                    pieceMove.markRookMoves(queenXp, queenXn, queenYp, queenYn);
                    pieceMove.markBishopMoves(queenXpYp, queenXpYn, queenXnYp, queenXnYn);
                    break;
                case "pawn":
                    pieceMove.markPawnMoves(pawnY);
                    break;
                case "sentinel":
                    pieceMove.markSentinelMoves(
                            sentinelXp, sentinelXn, sentinelYp, sentinelYn,
                            sentinelXpMoveable, sentinelXnMoveable, sentinelYpMoveable, sentinelYnMoveable
                    );
                    sentinelXpMoveable = pieceMove.getSentinelXpMoveableValue();
                    sentinelXnMoveable = pieceMove.getSentinelXnMoveableValue();
                    sentinelYpMoveable = pieceMove.getSentinelYpMoveableValue();
                    sentinelYnMoveable = pieceMove.getSentinelYnMoveableValue();
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

    public static void main(String[] args) { launch(args); }

    private static GridPane getGameBoard() {
//        Chessboard
        GridPane board = new GridPane();

// Create squares and add them to the board
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                StackPane sp = new StackPane();

                Pane square = new Pane();
                square.setPrefSize(CELL_SIZE, CELL_SIZE);
                square.setStyle((row + col) % 2 == 0 ? "-fx-background-color: #5d4037;" : "-fx-background-color: #795548;");

                sp.getChildren().add(square);

                if (col == 0 || row == 7) {
                    String label = "";
                    if (col == 0 && row == 7) {
                        StackPane sp1 = new StackPane();
                        Text t1 = new Text("1");
                        t1.setStyle("-fx-fill: white;");
                        sp1.getChildren().add(t1);
                        sp1.setAlignment(Pos.TOP_LEFT);
                        sp.getChildren().add(sp1);
                        label = "A";
                        sp.setAlignment(Pos.BOTTOM_RIGHT);
                    } else if (col == 0) {
                        label = String.valueOf(8-row);
                        sp.setAlignment(Pos.TOP_LEFT);
                    } else {
                        label = getCharForNumber(col+1);
                        sp.setAlignment(Pos.BOTTOM_RIGHT);
                    }
                    Text text = new Text(label);
                    text.setStyle("-fx-fill: white;");
                    sp.getChildren().add(text);
                }

                sp.setId("board_" + col + "_" + row);
                board.add(sp, col, row);
            }
        }
        return board;
    }

    private String getDisplayNotation(int col, int row) {
        return getCharForNumber(col + 1) + (8 - row);
    }

    private static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
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
                    if (!node.getId().startsWith("board_")) {
                        pieceInfo.parseId(node.getId());
                    }
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

        moveHistoryListView.getItems().add(0, targetCellPiece.getIsWhiteString() + " " + targetCellPiece.getName() + " is captured");

        updateCapturedPiecesDisplay();
    }

    private void updateCapturedPiecesDisplay() {
        //        display all captured pieces
        whiteCapturedPieceDisplay.getChildren().clear();
        blackCapturedPieceDisplay.getChildren().clear();
        String whiteCapturedPieces = "";
        int whiteCapturedIndex = 1;
        int whiteCapturedCount = 0;
//        start looping from the back of the array
        for (int i = whiteCaptured.length - 1; i >= 0; i--) {
            PieceInfo piece = whiteCaptured[i];
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
//        start looping from the back of the array
        for (int i = blackCaptured.length - 1; i >= 0; i--) {
            PieceInfo piece = blackCaptured[i];
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
        checkingPiece = null;
        checkedPiecePath.clear();
        pathToKing.clear();

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

        if (piece.isSentinel()) {
            List<PieceInfo> sentinelCapturePath = new ArrayList<>();
            board.getChildren().forEach(node -> {
//            check for sentinel
                if (node.getId() != null && node.getId().startsWith("none_moveable_")) {
                    int col = GridPane.getColumnIndex(node);
                    int row = GridPane.getRowIndex(node);
                    int[][] sentinelYnPath = new int[][]{
                            {piece.getCol(), piece.getRow() - 1}, {piece.getCol(), piece.getRow() - 2}, {piece.getCol(), piece.getRow() - 3}, {piece.getCol(), piece.getRow() - 4}, {piece.getCol(), piece.getRow() - 5}, {piece.getCol(), piece.getRow() - 6}, {piece.getCol(), piece.getRow() - 7}
                    };
                    int[][] sentinelYpPath = new int[][]{
                            {piece.getCol(), piece.getRow() + 1}, {piece.getCol(), piece.getRow() + 2}, {piece.getCol(), piece.getRow() + 3}, {piece.getCol(), piece.getRow() + 4}, {piece.getCol(), piece.getRow() + 5}, {piece.getCol(), piece.getRow() + 6}, {piece.getCol(), piece.getRow() + 7}
                    };
                    int[][] sentinelXnPath = new int[][]{
                            {piece.getCol() - 1, piece.getRow()}, {piece.getCol() - 2, piece.getRow()}, {piece.getCol() - 3, piece.getRow()}, {piece.getCol() - 4, piece.getRow()}, {piece.getCol() - 5, piece.getRow()}, {piece.getCol() - 6, piece.getRow()}, {piece.getCol() - 7, piece.getRow()}, {piece.getCol() - 8, piece.getRow()}, {piece.getCol() - 9, piece.getRow()}
                    };
                    int[][] sentinelXpPath = new int[][]{
                            {piece.getCol() + 1, piece.getRow()}, {piece.getCol() + 2, piece.getRow()}, {piece.getCol() + 3, piece.getRow()}, {piece.getCol() + 4, piece.getRow()}, {piece.getCol() + 5, piece.getRow()}, {piece.getCol() + 6, piece.getRow()}, {piece.getCol() + 7, piece.getRow()}, {piece.getCol() + 8, piece.getRow()}, {piece.getCol() + 9, piece.getRow()}
                    };

                    //                    get which direction move is made
                    if (col == piece.getCol()) {
                        //                        move is made on y axis
                        if (row > piece.getRow()) {
                            //                            move is made in v direction
                            for (int[] move : sentinelYpPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
                                if (moveRow > sentinelYpMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == piece.getIsWhite()) continue;
                                sentinelCapturePath.add(targetCellPiece);
                            }
                        } else {
                            //                            move is made in ^ direction
                            for (int[] move : sentinelYnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveRow < 0 || moveRow >= BOARD_ROW_SIZE) continue;
                                if (moveRow < sentinelYnMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == piece.getIsWhite()) continue;
                                sentinelCapturePath.add(targetCellPiece);
                            }
                        }
                    } else if (row == piece.getRow()) {
                        //                        move is made on x axis
                        if (col > piece.getCol()) {
                            //                            move is made in > direction
                            for (int[] move : sentinelXpPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveCol < 0 || moveCol >= BOARD_COL_SIZE) continue;
                                if (moveCol > sentinelXpMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == piece.getIsWhite()) continue;
                                sentinelCapturePath.add(targetCellPiece);
                            }
                        } else {
                            //                            move is made in < direction
                            for (int[] move : sentinelXnPath) {
                                int moveCol = move[0];
                                int moveRow = move[1];
                                if (moveCol < 0 || moveCol >= BOARD_COL_SIZE) continue;
                                if (moveCol < sentinelXnMoveable) continue;
                                PieceInfo targetCellPiece = getPieceByCoordinate(moveCol, moveRow);
                                if (targetCellPiece.getIsWhite() == piece.getIsWhite()) continue;
                                sentinelCapturePath.add(targetCellPiece);
                            }
                        }
                    }
                }
            });
            for (PieceInfo targetCellPiece : sentinelCapturePath) {
                markMoveableCell(targetCellPiece.getCol(), targetCellPiece.getRow());
            }
        }

        List<List<Integer>> tempCheckedPiecePath = new ArrayList<>();
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
                        if (n.getId().startsWith("board_")) return;
                        if (n.getId().startsWith("none_moveable_")) return;
                        targetCellPiece.parseId(n.getId());
                    }
                });
                targetCellPiece.setCol(col);
                targetCellPiece.setRow(row);
                tempCheckedPiecePath.add(List.of(col, row));
                if (targetCellPiece.isKing()) {
                    System.out.println("King is checked - " + targetCellPiece.getId());
                    moveHistoryListView.getItems().add(0, targetCellPiece.getIsWhiteString() + " " + targetCellPiece.getName() + " is checked!");
                    if (piece.getIsWhite()) {
                        isWhiteChecked = true;
                    } else {
                        isBlackChecked = true;
                    }
                }
            }
        });
        removeHighlight();

        if (isWhiteChecked || isBlackChecked) {
            checkedPiecePath = tempCheckedPiecePath;
            checkingPiece = piece;
        }
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
        if (!pathToKing.isEmpty()) {
            boolean flag = true;
            for (List<Integer> path : pathToKing) {
                int pathCol = path.get(0);
                int pathRow = path.get(1);

                if (pathCol == col && pathRow == row) {
                    System.out.println("Legal blocking move: " + col + ", " + row);
                    flag = false;
                    break;
                }
            }
            if (flag) return;
        }
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
            } else if (
                node.getClass().getSimpleName().equals("StackPane") &&
                !node.getId().startsWith("board_")
            ) {
                nodesToRemove.add(node);
            } else if (node.getClass().getSimpleName().equals("Rectangle")) {
                nodesToRemove.add(node);
            }
        });

        board.getChildren().removeAll(nodesToRemove);
    }
}
