package com.example.element;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.example.element.ElementSettings.*;

public class AvoidElement extends GameElement {

    private boolean movingRight;
    private final Timeline movementTimeline;
    private final Timeline positionUpdater;

    public AvoidElement() {
        this.shape = new Circle(RADIUS, Color.RED);
        this.isCollectible = false;
        this.movingRight = true;

        // Initialize the movement timeline
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(DURATION_SECONDS_CIRCLE ), e -> changeDirection()));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();

        // Update position every frame
        positionUpdater = new Timeline(new KeyFrame(Duration.millis(DURATION_MILLIS), e -> updatePosition()));
        positionUpdater.setCycleCount(Timeline.INDEFINITE);
        positionUpdater.play();
    }

    private void changeDirection() {
        movingRight = !movingRight;
    }

    private void updatePosition() {
        double currentX = shape.getTranslateX();
        double newX = movingRight ? currentX + MOVING : currentX - MOVING;

        // Ensure the element stays within the game bounds
        if (newX < 0) {
            newX = 0;
            movingRight = false;
        } else if (newX > PANE_WIDTH-RADIUS*2) { // Assuming game pane width is 800 and shape width is 30
            newX = PANE_WIDTH-RADIUS*2;
            movingRight = true;
        }

        shape.setTranslateX(newX);
    }


    @Override
    public void stopMovement() {
        movementTimeline.stop();
        positionUpdater.stop();
    }
}
