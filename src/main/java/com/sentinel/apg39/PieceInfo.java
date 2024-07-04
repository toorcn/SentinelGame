package com.sentinel.apg39;

class PieceInfo {
    private String name;
    private boolean isWhite;
    private String id;
    private int col;
    private int row;

    public PieceInfo() {
        this.id = null;
        this.name = "empty";
    }

    public void parseId(String id) {
        this.id = id;
        this.name = id.split("_")[1];
        this.isWhite = id.split("_")[0].equals("white");
    }

    public String getName() {
        return name;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public String getIsWhiteString() {
        return isWhite ? "white" : "black";
    }

    public String getId() {
        return id;
    }

    public String getUniqueId() {
        if (id == null) {
            return null;
        }
        return id.split("_")[2];
    }

    public boolean hasNotMoved() {
        if (id == null) return false;
        return getUniqueId().endsWith("0");
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void updateUniqueId(String newId) {
        this.id = id.split("_")[0] + "_" + id.split("_")[1] + "_" + newId;
    }

    public boolean isUnoccupied() {
        return name.equals("empty");
//        if (name.equals("empty") || name.equals("moveable")) {
//            return true;
//        }
//        return false;
    }

    public boolean isMoveable() {
        return name.equals("moveable");
    }

    public boolean isPawn() {
        return name.equals("pawn");
    }

    public boolean isKing() {
        return name.equals("king");
    }

    public boolean isQueen() {
        return name.equals("queen");
    }

    public boolean isRook() {
        return name.equals("rook");
    }

    public boolean isBishop() {
        return name.equals("bishop");
    }

    public boolean isKnight() {
        return name.equals("knight");
    }

    public boolean isSentinel() {
        return name.equals("sentinel");
    }

    public int[][] getMoves(int col, int row) {
        int[][] moves = new int[0][0];
        switch (name) {
            case "king":
                moves = new int[][]{{col + 1, row + 1}, {col + 1, row}, {col + 1, row - 1},
                        {col, row + 1}, {col, row - 1},
                        {col - 1, row + 1}, {col - 1, row}, {col - 1, row - 1}};
                break;
            case "queen":
                for (int i = 1; i < 10; i++) {
                    int[][] tempMoves = new int[][]{{col + i, row + i}, {col + i, row - i}, {col - i, row + i}, {col - i, row - i},
                            {col + i, row}, {col - i, row}, {col, row + i}, {col, row - i}};
                    int[][] newMoves = new int[moves.length + tempMoves.length][2];
                    System.arraycopy(moves, 0, newMoves, 0, moves.length);
                    System.arraycopy(tempMoves, 0, newMoves, moves.length, tempMoves.length);
                    moves = newMoves;
                }
                break;
            case "pawn":
                if (isWhite) {
                    moves = new int[][]{
                            {col, row - 1}, {col + 1, row - 1}, {col - 1, row - 1},
                            {col, row - 2}
                    };
                } else {
                    moves = new int[][]{
                            {col, row + 1}, {col + 1, row + 1}, {col - 1, row + 1},
                            {col, row + 2}
                    };
                }
                break;
            case "rook":
                for (int i = 1; i < 10; i++) {
                    int[][] tempMoves = new int[][]{{col + i, row}, {col - i, row}, {col, row + i}, {col, row - i}};
                    int[][] newMoves = new int[moves.length + tempMoves.length][2];
                    System.arraycopy(moves, 0, newMoves, 0, moves.length);
                    System.arraycopy(tempMoves, 0, newMoves, moves.length, tempMoves.length);
                    moves = newMoves;
                }
                break;
            case "bishop":
                for (int i = 1; i < 10; i++) {
                    int[][] tempMoves = new int[][]{{col + i, row + i}, {col + i, row - i}, {col - i, row + i}, {col - i, row - i}};
                    int[][] newMoves = new int[moves.length + tempMoves.length][2];
                    System.arraycopy(moves, 0, newMoves, 0, moves.length);
                    System.arraycopy(tempMoves, 0, newMoves, moves.length, tempMoves.length);
                    moves = newMoves;
                }
                break;
            case "knight":
                moves = new int[][]{{col + 2, row + 1}, {col + 2, row - 1}, {col - 2, row + 1}, {col - 2, row - 1},
                        {col + 1, row + 2}, {col + 1, row - 2}, {col - 1, row + 2}, {col - 1, row - 2}};
                break;
            case "sentinel":
                int[][] normalMoves = new int[][]{
                        {col + 1, row + 1}, {col + 1, row - 1},
                        {col - 1, row + 1}, {col - 1, row - 1}
                };
                for (int i = 1; i < 10; i++) {
                    int[][] tempMoves = new int[][]{
                            {col + i, row}, {col - i, row}, {col, row + i}, {col, row - i}
                    };
                    int[][] newMoves = new int[moves.length + tempMoves.length][2];
                    System.arraycopy(moves, 0, newMoves, 0, moves.length);
                    System.arraycopy(tempMoves, 0, newMoves, moves.length, tempMoves.length);
                    moves = newMoves;
                }
                int[][] newMoves = new int[moves.length + normalMoves.length][2];
                System.arraycopy(moves, 0, newMoves, 0, moves.length);
                System.arraycopy(normalMoves, 0, newMoves, moves.length, normalMoves.length);
                moves = newMoves;
                break;
        }
        return moves;
    }
}
