package com.example.mouseracegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class AvoidElement extends GameElement {

    private boolean movingRight;
    private Timeline movementTimeline;

    public AvoidElement() {
        this.shape = new Circle(15, Color.RED);
        this.isCollectible = false;
        this.movingRight = true;

        // Initialize the movement timeline
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> changeDirection()));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();

        // Update position every frame
        Timeline positionUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> updatePosition()));
        positionUpdater.setCycleCount(Timeline.INDEFINITE);
        positionUpdater.play();
    }

    private void changeDirection() {
        movingRight = !movingRight;
    }

    private void updatePosition() {
        double currentX = shape.getTranslateX();
        double newX = movingRight ? currentX + 10 : currentX - 10;

        // Ensure the element stays within the game bounds
        if (newX < 0) {
            newX = 0;
            movingRight = false;
        } else if (newX > 770) { // Assuming game pane width is 800 and shape width is 30
            newX = 770;
            movingRight = true;
        }

        shape.setTranslateX(newX);
    }

    @Override
    public void onClicked() {
        // Implement avoid logic
        System.out.println("AvoidElement clicked!");
    }
}
