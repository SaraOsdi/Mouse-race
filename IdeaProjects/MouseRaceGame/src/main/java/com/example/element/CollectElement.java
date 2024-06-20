package com.example.element;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CollectElement extends GameElement {

    private boolean movingUp;
    private final Timeline movementTimeline;
    private final Timeline positionUpdater;

    public CollectElement() {
        this.shape = new Rectangle(30, 30, Color.GREEN);
        this.isCollectible = true;
        this.movingUp = true;

        // Initialize the movement timeline
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> changeDirection()));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();

        // Update position every frame
        positionUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> updatePosition()));
        positionUpdater.setCycleCount(Timeline.INDEFINITE);
        positionUpdater.play();
    }

    private void changeDirection() {
        movingUp = !movingUp;
    }

    private void updatePosition() {
        double currentY = shape.getTranslateY();
        double newY = movingUp ? currentY - 10 : currentY + 10;

        // Ensure the element stays within the game bounds
        if (newY < 0) {
            newY = 0;
            movingUp = false;
        } else if (newY > 570) { // Assuming game pane height is 600 and shape height is 30
            newY = 570;
            movingUp = true;
        }

        shape.setTranslateY(newY);
    }


    @Override
    public void stopMovement() {
        movementTimeline.stop();
        positionUpdater.stop();
    }
}
