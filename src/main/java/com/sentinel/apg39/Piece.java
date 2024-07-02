package com.sentinel.apg39;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Piece extends ImageView {
    private boolean isWhite;

    public Piece(boolean isWhite, String id, Image image) {
        super(image);
        this.isWhite = isWhite;
        // Set initial fit width and height based on grid cell size
        setFitWidth(50); // Adjust this value as per your grid cell size
        setFitHeight(50); // Adjust this value as per your grid cell size
        setPreserveRatio(true); // Preserve aspect ratio
        setId((isWhite ? "white" : "black") + "_" + this.getClass().getSimpleName().toLowerCase() + "_" + id);

    }

    public boolean isWhite() {
        return isWhite;
    }
}

class Pawn extends Piece {
    public Pawn(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_pawn.png"));
    }

//    boolean getLegalMoves(int col, int row) {
//
//    }
}

class Rook extends Piece {
    public Rook(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_rook.png"));
    }
}

class Knight extends Piece {
    public Knight(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_knight.png"));
    }
}

class Bishop extends Piece {
    public Bishop(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_bishop.png"));
    }
}

class Queen extends Piece {
    public Queen(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_queen.png"));
    }
}

class King extends Piece {
    public King(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_king.png"));
    }
}

class Sentinel extends Piece {
    public Sentinel(boolean isWhite, String id) {
        super(isWhite, id, new Image("file:src/main/resources/images/piece_" + (isWhite ? "white" : "black") + "_sentinel.png"));
    }
}
