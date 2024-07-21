package com.sentinel.apg39;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class PieceMove {
    private GridPane board;

    private PieceInfo pieceInfo;
    private PieceInfo targetCellPiece;
    private boolean isWhiteTurn;
    private int col;
    private int row;
    private int moveCol;
    private int moveRow;

    boolean isRowMove;
    boolean isColMove;
    boolean isMovingUp;
    boolean isMovingDown;
    boolean isMovingRight;
    boolean isMovingLeft;
    boolean isEnemyPiece;

//    For castling
    PieceInfo queenSideRook;
    PieceInfo kingSideRook;

    int sentinelXpMoveable;
    int sentinelYpMoveable;
    int sentinelXnMoveable;
    int sentinelYnMoveable;

    List<List<Integer>> pathToKing = new ArrayList<>();

    public PieceMove(
            GridPane board, PieceInfo pieceInfo, PieceInfo targetCellPiece, boolean isWhiteTurn,
            int col, int row, int moveCol, int moveRow
    ) {
        this.board = board;
        this.pieceInfo = pieceInfo;
        this.targetCellPiece = targetCellPiece;
        this.isWhiteTurn = isWhiteTurn;
        this.col = col;
        this.row = row;
        this.moveCol = moveCol;
        this.moveRow = moveRow;

        isRowMove = moveRow == row;
        isColMove = moveCol == col;
        isMovingUp = moveRow < row;
        isMovingDown = moveRow > row;
        isMovingRight = moveCol > col;
        isMovingLeft = moveCol < col;
        isEnemyPiece = !targetCellPiece.isUnoccupied() && targetCellPiece.getIsWhite() != pieceInfo.getIsWhite();

        String queenSideRookId = pieceInfo.getIsWhite() ? "white_rook_10" : "black_rook_10";
        String kingSideRookId = pieceInfo.getIsWhite() ? "white_rook_20" : "black_rook_20";
        queenSideRook = getPieceById(queenSideRookId);
        kingSideRook = getPieceById(kingSideRookId);
    }

    public PieceMove(
            GridPane board, PieceInfo pieceInfo, PieceInfo targetCellPiece, boolean isWhiteTurn,
            int col, int row, int moveCol, int moveRow,
            List<List<Integer>> pathToKing
    ) {
        this(board, pieceInfo, targetCellPiece, isWhiteTurn, col, row, moveCol, moveRow);
        this.pathToKing = pathToKing;
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

    private void markMoveableCell(int col, int row) {
        if (getPieceByCoordinate(col, row).isMoveable()) return;
        if (this.pieceInfo.isKing()) {
            markMoveableCell(col, row, true);
            return;
        }
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

        StackPane stackPane = new StackPane(circle);
        String uniqueId2 = String.valueOf(System.currentTimeMillis());
        stackPane.setId("none_moveable_" + uniqueId2);
        board.add(stackPane, col, row);
    }

    private void markMoveableCell(int col, int row, boolean king) {
        if (king) {
            boolean flag = false;
            for (List<Integer> path : pathToKing) {
                int pathCol = path.get(0);
                int pathRow = path.get(1);

                if (pathCol == col && pathRow == row) {
                    flag = true;
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

        StackPane stackPane = new StackPane(circle);
        String uniqueId2 = String.valueOf(System.currentTimeMillis());
        stackPane.setId("none_moveable_" + uniqueId2);
        board.add(stackPane, col, row);
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

    public void markRookMoves(PieceBlocked rookXp, PieceBlocked rookXn, PieceBlocked rookYp, PieceBlocked rookYn) {
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

    public void markBishopMoves(PieceBlocked bishopXpYp, PieceBlocked bishopXpYn, PieceBlocked bishopXnYp, PieceBlocked bishopXnYn) {
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

    public void markKnightMoves() {
        if (targetCellPiece.isUnoccupied()) {
            markMoveableCell(moveCol, moveRow);
        } else if (isEnemyPiece) {
            markMoveableCell(moveCol, moveRow);
        }
    }

    public void markKingMoves() {
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
                    getPieceByCoordinate(4, targetCellPiece.getRow()).isUnoccupiedOrMoveable() &&
                    getPieceByCoordinate(3, targetCellPiece.getRow()).isUnoccupiedOrMoveable() &&
                    getPieceByCoordinate(2, targetCellPiece.getRow()).isUnoccupiedOrMoveable() &&
                    getPieceByCoordinate(1, targetCellPiece.getRow()).isUnoccupiedOrMoveable()
                ) {
//                                     mark moveable at x+2 or x-2
                    markMoveableCell(3, targetCellPiece.getRow());
                }
            }
            if (kingSideRook.getId() != null) {
//                                if path in between is clear
                if (
                    getPieceByCoordinate(6, targetCellPiece.getRow()).isUnoccupiedOrMoveable() &&
                    getPieceByCoordinate(7, targetCellPiece.getRow()).isUnoccupiedOrMoveable() &&
                    getPieceByCoordinate(8, targetCellPiece.getRow()).isUnoccupiedOrMoveable()
                ) {
//                                     mark moveable at x+2 or x-2
                    markMoveableCell(7, targetCellPiece.getRow());
                }
            }
        }
    }

    public void markPawnMoves(PieceBlocked pawnY) {
//        pawn is blocked by friendly
        if (
            col == moveCol &&
            (
                !targetCellPiece.isUnoccupied() &&
                targetCellPiece.getIsWhite() == isWhiteTurn
            )
        ) {
            pawnY.setIsBlocked(true);
            return;
        }
//        pawn is blocked by enemy, prevent from placing moveable at 2 front
        if (
            pawnY.getIsBlocked() &&
            col == moveCol &&
            (
                row + 2 == moveRow ||
                row - 2 == moveRow
            )
        ) {
            return;
        }

//        Pawn with id that ends with 0 indicates it is it's first move
        if (pieceInfo.hasNotMoved()) {
//            Spawn moveable if forward is empty or forward left or right has enemy piece
            if (
                moveCol == col ||
                (
                    !targetCellPiece.isUnoccupied() &&
                    isEnemyPiece
                )
            ) {
//                Prevent spawning of moveable if forward same col has enemy piece
                if (
                    moveCol == col &&
                    !targetCellPiece.isUnoccupied() &&
                    isEnemyPiece
                ) {
                    pawnY.setIsBlocked(true);
                    return;
                }
                markMoveableCell(moveCol, moveRow);
            }
//            Indicate pawn is blocked if forward path contains enemy piece
            if (
                moveCol == col &&
                !targetCellPiece.isUnoccupied() &&
                isEnemyPiece
            ) {
                pawnY.setIsBlocked(true);
            }
        } else {
//            Ignore all moveable cell if length is longer than 1
            if (
                moveRow - row > 1 ||
                moveRow - row < -1
            ) return;
//            Spawn moveable if cell in-front forward is empty
            if (
                moveCol == col
            ) {
                if (targetCellPiece.isUnoccupied()) {
                    markMoveableCell(moveCol, moveRow);
                }
            } else if (
                !targetCellPiece.isUnoccupied() &&
                isEnemyPiece
            ) {
//                Spawn moveable if forward left or right has enemy piece
                markMoveableCell(moveCol, moveRow);
            } else {
//                En Passant

//                check if piece is white, if at row 3, if left or right cell is black pawn, spawn moveable
                if (
                    (
                        pieceInfo.getIsWhite() &&
                        row == 3
                    ) ||
                    (
                        !pieceInfo.getIsWhite() &&
                        row == 4
                    ) &&
                        targetCellPiece.isUnoccupied()
                ) {
                    PieceInfo leftInRowCellPiece = getPieceByCoordinate(col - 1, row);
                    PieceInfo rightInRowCellPiece = getPieceByCoordinate(col + 1, row);

                    boolean isLeftPieceEnemy = pieceInfo.getIsWhite() != leftInRowCellPiece.getIsWhite() && !leftInRowCellPiece.isUnoccupied();
                    boolean isRightPieceEnemy = pieceInfo.getIsWhite() != rightInRowCellPiece.getIsWhite() && !rightInRowCellPiece.isUnoccupied();
                    if (
                        isLeftPieceEnemy &&
                        moveCol == col - 1
                    ) {
                        markMoveableCell(moveCol, moveRow);
                    }

                    if (
                        isRightPieceEnemy &&
                        moveCol == col + 1
                    ) {
                        markMoveableCell(moveCol, moveRow);
                    }
                }
            }
        }
    }

    public void markSentinelMoves(
            PieceBlocked sentinelXp, PieceBlocked sentinelXn, PieceBlocked sentinelYp, PieceBlocked sentinelYn,
            int sentinelXpMoveable, int sentinelXnMoveable, int sentinelYpMoveable, int sentinelYnMoveable
    ) {
        this.sentinelXpMoveable = sentinelXpMoveable;
        this.sentinelXnMoveable = sentinelXnMoveable;
        this.sentinelYpMoveable = sentinelYpMoveable;
        this.sentinelYnMoveable = sentinelYnMoveable;
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
                        moveRow > this.sentinelYpMoveable &&
                        !sentinelYp.getIsBlocked()
                    ) {
                        this.sentinelYpMoveable = moveRow;
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
                        this.sentinelYnMoveable = moveRow;
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
                        this.sentinelXpMoveable = moveCol;
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
                        this.sentinelXnMoveable = moveCol;
                    }
                }
            }
        }
    }

    public int getSentinelYpMoveableValue() {
        return sentinelYpMoveable;
    }

    public int getSentinelYnMoveableValue() {
        return sentinelYnMoveable;
    }

    public int getSentinelXpMoveableValue() {
        return sentinelXpMoveable;
    }

    public int getSentinelXnMoveableValue() {
        return sentinelXnMoveable;
    }
}
