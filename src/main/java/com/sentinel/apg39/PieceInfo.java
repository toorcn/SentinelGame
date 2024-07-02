package com.sentinel.apg39;

class PieceInfo {
    private String name;
    private boolean isWhite;
    private String id;

    public PieceInfo() {
        this.id = null;
        this.name = "empty";
    }

    public PieceInfo(String id) {
        this.id = id;
        this.name = id.split("_")[1];
        this.isWhite = id.split("_")[0].equals("white");
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
        return id.split("_")[2];
    }

    public int[][] getMoves(int col, int row) {
        int[][] moves = new int[0][0];
        if (name.equals("pawn")) {
            if (isWhite) {
                moves = new int[][]{{col, row - 1}, {col, row - 2}};
            } else {
                moves = new int[][]{{col, row + 1}, {col, row + 2}};
            }
        }
        return moves;
    }
}
