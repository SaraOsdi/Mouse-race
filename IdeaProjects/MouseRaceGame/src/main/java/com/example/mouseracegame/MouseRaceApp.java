package com.example.mouseracegame;

import com.example.element.AvoidElement;
import com.example.element.ChangeElement;
import com.example.element.CollectElement;
import com.example.element.GameElement;
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

import static com.example.mouseracegame.GameSettings.*;

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

        initializeGamePane();
        initializeStartButton();
        initializeTimerLabel();

        StackPane root = createRootLayout();
        Scene scene = new Scene(root, GAME_PANE_WIDTH, GAME_PANE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        leaderboard = new Leaderboard(new LeaderboardSQLiteDatabase());
    }

    private void initializeGamePane() {
        gamePane = new Pane();
        gamePane.setPrefSize(800, 600);
    }

    private void initializeStartButton() {
        startButton = new Button("Start");
        startButton.setOnAction(e -> startGame());
    }

    private void initializeTimerLabel() {
        timerLabel = new Label("Time: 0");
        timerLabel.setStyle(String.format("-fx-font-size: %d;", TIMER_LABEL_FONT_SIZE));
    }

    private StackPane createRootLayout() {
        StackPane root = new StackPane();
        root.getChildren().addAll(gamePane, startButton, timerLabel);
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setAlignment(timerLabel, Pos.TOP_CENTER);
        return root;
    }

    private void startGame() {
        startButton.setVisible(false);
        gamePane.getChildren().clear();
        elements = new ArrayList<>();
        Random random = new Random();

        elapsedTime = 0;
        timerLabel.setText("Time: 0");

        createAndPositionGameElements(random);

        addMouseClickHandlers();

        setupGameLoop();
        setupTimer();
    }

    private void createAndPositionGameElements(Random random) {
        createElements(CollectElement.class, ELEMENT_COUNT, random);
        createElements(AvoidElement.class, ELEMENT_COUNT, random);
        createElements(ChangeElement.class, CHANGE_ELEMENT_COUNT, random);
    }

    private void createElements(Class<? extends GameElement> elementType, int count, Random random) {
        try {
            for (int i = 0; i < count; i++) {
                GameElement element = elementType.getDeclaredConstructor().newInstance();
                positionElement(element.getShape(), random);
                elements.add(element);
                gamePane.getChildren().add(element.getShape());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void positionElement(javafx.scene.shape.Shape shape, Random random) {
        double x = random.nextDouble() * (gamePane.getWidth() - shape.getBoundsInLocal().getWidth());
        double y = random.nextDouble() * (gamePane.getHeight() - shape.getBoundsInLocal().getHeight());
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    private void addMouseClickHandlers() {
        elements.forEach(element -> element.getShape().setOnMouseClicked(e -> handleElementClick(element)));
    }

    private void handleElementClick(GameElement element) {
        if (element.isCollectible()) {
            gamePane.getChildren().remove(element.getShape());
            elements.remove(element);
            checkVictoryCondition();
        } else {
            gameOver();
        }
    }

    private void setupGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(GAME_LOOP_INTERVAL)));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }


    private void setupTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(TIMER_INTERVAL), e -> updateTimer()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }


    private void updateTimer() {
        elapsedTime++;
        timerLabel.setText("Time: " + elapsedTime);
    }

    private void checkVictoryCondition() {
        if (elements.stream().noneMatch(GameElement::isCollectible)) {
            stopGame();
            showVictoryScreen();
        }
    }

    private void gameOver() {
        stopGame();
        showGameOverScreen();
        startButton.setVisible(true);
    }

    private void stopGame() {
        gameLoop.stop();
        timer.stop();
        disableElementInteractions();
        stopElementMovements();
    }

    private void disableElementInteractions() {
        elements.forEach(element -> element.getShape().setOnMouseClicked(null));
    }


    private void stopElementMovements() {
        elements.forEach(GameElement::stopMovement);
    }

    private void showVictoryScreen() {
        VBox victoryBox = createVictoryBox();
        gamePane.getChildren().add(victoryBox);
        StackPane.setAlignment(victoryBox, Pos.CENTER);
    }

    private VBox createVictoryBox() {
        Label victoryLabel = new Label("Victory! Enter your name:");
        TextField nameField = new TextField();
        Button submitButton = new Button("Submit");

        VBox victoryBox = new VBox(10, victoryLabel, nameField, submitButton);
        victoryBox.setAlignment(Pos.CENTER);

        submitButton.setOnAction(e -> handleVictorySubmit(nameField, victoryBox));
        return victoryBox;
    }
    private void handleVictorySubmit(TextField nameField, VBox victoryBox) {
        String playerName = nameField.getText();
        if (!playerName.isEmpty()) {
            leaderboard.savePlayerTime(playerName, elapsedTime);
            gamePane.getChildren().remove(victoryBox);
            showLeaderboard();
            startButton.setVisible(true);
        }
    }

    private void showGameOverScreen() {
        VBox gameOverBox = createGameOverBox();
        gamePane.getChildren().add(gameOverBox);
        StackPane.setAlignment(gameOverBox, Pos.CENTER);
    }

    private VBox createGameOverBox() {
        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setStyle(String.format("-fx-font-size: %d; -fx-font-weight: bold;", GAME_OVER_LABEL_FONT_SIZE));

        VBox gameOverBox = new VBox(10, gameOverLabel, createLeaderboardBox());
        gameOverBox.setAlignment(Pos.CENTER);

        return gameOverBox;
    }

    private void showLeaderboard() {
        VBox leaderboardBox = createLeaderboardBox();
        gamePane.getChildren().add(leaderboardBox);
        StackPane.setAlignment(leaderboardBox, Pos.CENTER);
    }

    private VBox createLeaderboardBox() {
        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);

        Label leaderboardLabel = new Label("Leaderboard (Top " + TOP_PLAYERS_COUNT + "):");
        leaderboardBox.getChildren().add(leaderboardLabel);

        List<String> topPlayers = leaderboard.getTopPlayers(TOP_PLAYERS_COUNT);
        topPlayers.forEach(player -> leaderboardBox.getChildren().add(createPlayerLabel(player)));

        return leaderboardBox;
    }



    private Label createPlayerLabel(String player) {
        return new Label(player);
    }

    public static void main(String[] args) {
        new LeaderboardSQLiteDatabase().initializeDatabase();
        launch(args);
    }
}
