package com.example.mouseracegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MouseRaceApp extends Application {

    private Pane gamePane;
    private List<GameElement> elements;
    private Timeline gameLoop;
    private Button startButton;
    private Label timerLabel;
    private int elapsedTime;
    private Timeline timer;
    private Leaderboard leaderboard;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mouse Race Game");

        gamePane = new Pane();
        gamePane.setPrefSize(800, 600);

        startButton = new Button("Start");
        startButton.setOnAction(e -> startGame());

        timerLabel = new Label("Time: 0");
        timerLabel.setStyle("-fx-font-size: 24;");

        StackPane root = new StackPane();
        root.getChildren().addAll(gamePane, startButton, timerLabel);
        StackPane.setAlignment(startButton, Pos.CENTER); // Center the start button
        StackPane.setAlignment(timerLabel, Pos.TOP_CENTER); // Position the timer at the top center

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the leaderboard with SQLiteDatabase implementation
        leaderboard = new Leaderboard(new SQLiteDatabase());

        // Clear the leaderboard at startup
        //clearLeaderboard();
    }

    private void startGame() {
        startButton.setVisible(false); // Hide the start button
        gamePane.getChildren().clear();
        elements = new ArrayList<>();
        Random random = new Random();

        elapsedTime = 0;
        timerLabel.setText("Time: 0");

        // Create and position Collect elements
        for (int i = 0; i < 5; i++) {
            CollectElement collectElement = new CollectElement();
            positionElement(collectElement.getShape(), random);
            elements.add(collectElement);
            gamePane.getChildren().add(collectElement.getShape());
        }

        // Create and position Avoid elements
        for (int i = 0; i < 5; i++) {
            AvoidElement avoidElement = new AvoidElement();
            positionElement(avoidElement.getShape(), random);
            elements.add(avoidElement);
            gamePane.getChildren().add(avoidElement.getShape());
        }

        // Create and position Change elements
        for (int i = 0; i < 3; i++) {
            ChangeElement changeElement = new ChangeElement();
            positionElement(changeElement.getShape(), random);
            elements.add(changeElement);
            gamePane.getChildren().add(changeElement.getShape());
        }

        // Add mouse click handlers
        for (GameElement element : elements) {
            element.getShape().setOnMouseClicked(e -> {
                element.onClicked();
                if (element.isCollectible()) {
                    gamePane.getChildren().remove(element.getShape());
                    elements.remove(element);
                    checkVictoryCondition();
                } else {
                    gameOver();
                }
            });
        }

        // Set up game loop for element movements
        gameLoop = new Timeline(new KeyFrame(Duration.millis(100), e -> updateElements()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();

        // Set up timer for elapsed time
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime);
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void positionElement(javafx.scene.shape.Shape shape, Random random) {
        double x = random.nextDouble() * (gamePane.getWidth() - shape.getBoundsInLocal().getWidth());
        double y = random.nextDouble() * (gamePane.getHeight() - shape.getBoundsInLocal().getHeight());
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    private void updateElements() {
        for (GameElement element : elements) {
            element.update();
        }
    }

    private void checkVictoryCondition() {
        if (elements.stream().noneMatch(GameElement::isCollectible)) {
            gameLoop.stop();
            timer.stop();
            showVictoryScreen();
        }
    }

    private void gameOver() {
        gameLoop.stop();
        timer.stop();
        showGameOverScreen();
        startButton.setVisible(true); // Show the start button again
        disableElementInteractions(); // Disable interactions after game over
    }

    private void disableElementInteractions() {
        for (GameElement element : elements) {
            element.getShape().setOnMouseClicked(null);
        }
    }

    private void showVictoryScreen() {
        Label victoryLabel = new Label("Victory! Enter your name:");
        TextField nameField = new TextField();
        Button submitButton = new Button("Submit");

        VBox victoryBox = new VBox(10, victoryLabel, nameField, submitButton);
        victoryBox.setAlignment(Pos.CENTER);

        gamePane.getChildren().add(victoryBox);
        StackPane.setAlignment(victoryBox, Pos.CENTER);

        submitButton.setOnAction(e -> {
            String playerName = nameField.getText();
            if (!playerName.isEmpty()) {
                leaderboard.savePlayerTime(playerName, elapsedTime);
                gamePane.getChildren().remove(victoryBox);
                showLeaderboard();
                startButton.setVisible(true); // Show the start button again
            }
        });
    }

    private void showGameOverScreen() {
        Label gameOverLabel = new Label("Game Over!");
        Button restartButton = new Button("Restart");

        VBox gameOverBox = new VBox(10, gameOverLabel, restartButton);
        gameOverBox.setAlignment(Pos.CENTER);

        gamePane.getChildren().add(gameOverBox);
        StackPane.setAlignment(gameOverBox, Pos.CENTER);

        restartButton.setOnAction(e -> {
            gamePane.getChildren().remove(gameOverBox);
            startButton.setVisible(true); // Show the start button again
        });
    }

    private void showLeaderboard() {
        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);

        Label leaderboardLabel = new Label("Leaderboard (Top 3):");
        leaderboardBox.getChildren().add(leaderboardLabel);

        List<String> topPlayers = leaderboard.getTopPlayers();
        for (String player : topPlayers) {
            Label playerLabel = new Label(player);
            leaderboardBox.getChildren().add(playerLabel);
        }

        gamePane.getChildren().add(leaderboardBox);
        StackPane.setAlignment(leaderboardBox, Pos.CENTER);
    }

    private void clearLeaderboard() {
        leaderboard.clearLeaderboard();
        System.out.println("Leaderboard cleared.");
    }

    public static void main(String[] args) {
        // Initialize database and leaderboard table
        new SQLiteDatabase().initializeDatabase();
        launch(args);
    }
}
