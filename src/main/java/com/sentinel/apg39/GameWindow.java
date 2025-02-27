package com.sentinel.apg39;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.sentinel.apg39.SentinelGame.*;

public class GameWindow {
    private GridPane board;
    private ListView<String> moveHistoryListView;
    private HBox whiteCapturedPieceDisplay;
    private HBox blackCapturedPieceDisplay;

    public GameWindow(GridPane board, ListView<String> moveHistoryListView, HBox whiteCapturedPieceDisplay, HBox blackCapturedPieceDisplay) {
        this.board = board;
        this.moveHistoryListView = moveHistoryListView;
        this.whiteCapturedPieceDisplay = whiteCapturedPieceDisplay;
        this.blackCapturedPieceDisplay = blackCapturedPieceDisplay;
    }

    public VBox leftWindowPanel() {
        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER_LEFT);

//        Username
        Text usernameLabel = new Text("Username: ");
        usernameLabel.setFill(Color.WHITE);

        Text username = new Text("xyz");
        username.setFill(Color.WHITE);

        HBox usernameDisplay = new HBox();
//        usernameDisplay.setStyle("-fx-background-color: #fff");
        usernameDisplay.getChildren().addAll(usernameLabel, username);
        usernameDisplay.autosize();
        int usernamePadding = (CELL_SIZE - (int) usernameDisplay.getHeight()) / 2;
        usernameDisplay.setPadding(new Insets(usernamePadding, 0, usernamePadding, 0));


        mainPanel.getChildren().add(usernameDisplay);

//        Board
        VBox.setMargin(board, new Insets(10, 0, 10, 0));
        mainPanel.getChildren().add(board);

//        Resign Button
        StackPane resignButton = new StackPane();
        resignButton.setAlignment(Pos.TOP_LEFT);
        Image resignImg = new Image("file:src/main/resources/button-imgs/ResignButton.png");
        Rectangle resignRec = new Rectangle(100, 35);
        resignRec.setFill(new ImagePattern(resignImg));
        StackPane.setMargin(resignRec, new Insets(10, 0, 10, 0));

        resignButton.setOnMouseClicked(event -> {
            System.out.println("Resign button clicked");
        });
//
        resignButton.getChildren().addAll(resignRec);

        mainPanel.getChildren().add(resignButton);

        HBox.setMargin(mainPanel, new Insets(0, WINDOW_MARGIN, 0, WINDOW_MARGIN));
        return mainPanel;
    }

    public VBox rightWindowPanel() {
        final int PANEL_VERTICAL_MARGIN = (WINDOW_HEIGHT - (CELL_SIZE * BOARD_ROW_SIZE) ) / 2;
        final int CENTER_BOX_HEIGHT = 250;

        VBox sidePanel = new VBox();
        sidePanel.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(sidePanel, new Insets(0, WINDOW_MARGIN, 0, 0));

//        Pause button
        Rectangle pauseRec = new Rectangle(35, 35);
        Image pauseImg = new Image("file:src/main/resources/button-imgs/PauseButton.png");
        pauseRec.setFill(new ImagePattern(pauseImg));
        pauseRec.setOnMouseClicked(event -> {
            System.out.println("pause button clicked");
        });
        VBox.setMargin(pauseRec, new Insets(WINDOW_MARGIN, 0, 0, 0));
        sidePanel.getChildren().add(pauseRec);
        sidePanel.autosize();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPrefWidth(275);
        mainPanel.setPrefHeight(440);

        VBox.setMargin(mainPanel, new Insets(PANEL_VERTICAL_MARGIN - sidePanel.getHeight(), 0, PANEL_VERTICAL_MARGIN, 0));

        mainPanel.setStyle("-fx-border-width: 1; -fx-border-color: black;");

//        White captured label
        StackPane whiteCapturedLabel = new StackPane();
        Rectangle whiteCapturedRec = new Rectangle(100, 25);
        Image whiteCapturedImg = new Image("file:src/main/resources/images/WhiteCapturedText.png");
        whiteCapturedRec.setFill(new ImagePattern(whiteCapturedImg));
        whiteCapturedLabel.getChildren().add(whiteCapturedRec);

//        Black captured label
        StackPane blackCapturedLabel = new StackPane();
        Rectangle blackCapturedRec = new Rectangle(100, 25);
        Image blackCapturedImg = new Image("file:src/main/resources/images/BlackCapturedText.png");
        blackCapturedRec.setFill(new ImagePattern(blackCapturedImg));
        blackCapturedLabel.getChildren().add(blackCapturedRec);

//        main Content box
        VBox moveContent = new VBox();
        moveContent.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        moveContent.setPrefHeight(CENTER_BOX_HEIGHT);
        VBox.setMargin(moveContent, new Insets(10, 10, 10, 10));
        moveContent.setAlignment(Pos.CENTER);

        HBox h2 = new HBox();
        Text gameInfo = new Text("Game Info");
        gameInfo.setFill(Color.WHITE);
        h2.setPadding(new Insets(5, 0, 5, 10));
        h2.getChildren().addAll(gameInfo);

        moveContent.getChildren().addAll(h2, moveHistoryListView);

        VBox whiteCapturedDisplay = new VBox();
        whiteCapturedPieceDisplay.setPrefHeight(CELL_SIZE);
        whiteCapturedPieceDisplay.setAlignment(Pos.CENTER);
        whiteCapturedDisplay.getChildren().addAll(whiteCapturedLabel, whiteCapturedPieceDisplay);

        VBox blackCapturedDisplay = new VBox();
        blackCapturedPieceDisplay.setPrefHeight(CELL_SIZE);
        blackCapturedPieceDisplay.setAlignment(Pos.CENTER);
        blackCapturedDisplay.getChildren().addAll(blackCapturedLabel, blackCapturedPieceDisplay);


        mainPanel.getChildren().addAll(whiteCapturedDisplay, moveContent, blackCapturedDisplay);
        mainPanel.setStyle("-fx-background-color: #5B3E35; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 10px;");

        sidePanel.getChildren().add(mainPanel);

        return sidePanel;
    }
}
