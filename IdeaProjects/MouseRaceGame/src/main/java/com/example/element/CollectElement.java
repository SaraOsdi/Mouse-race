package com.example.element;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.example.element.ElementSettings.*;

public class CollectElement extends GameElement {

    private boolean movingUp;
    private final Timeline movementTimeline;
    private final Timeline positionUpdater;

    public CollectElement() {
        this.shape = new Rectangle(SQUARE_EDGE, SQUARE_EDGE, Color.GREEN);
        this.isCollectible = true;
        this.movingUp = true;

        // Initialize the movement timeline
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(DURATION_SECONDS), e -> changeDirection()));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();

        // Update position every frame
        positionUpdater = new Timeline(new KeyFrame(Duration.millis(DURATION_MILLIS), e -> updatePosition()));
        positionUpdater.setCycleCount(Timeline.INDEFINITE);
        positionUpdater.play();
    }

    private void changeDirection() {
        movingUp = !movingUp;
    }

    private void updatePosition() {
        double currentY = shape.getTranslateY();
        double newY = movingUp ? currentY - MOVING : currentY + MOVING;

        // Ensure the element stays within the game bounds
        if (newY < 0) {
            newY = 0;
            movingUp = false;
        } else if (newY > PANE_HEIGHT-SQUARE_EDGE) { // Assuming game pane height is 600 and shape height is 30
            newY = PANE_HEIGHT-SQUARE_EDGE;
            movingUp = true;
        }

        shape.setTranslateY(newY);
    }


    @Override
    public void stopMovement() {
        movementTimeline.stop();
        positionUpdater.stop();
    }

    @Override
    public ElementStatus onClick() {
        return ElementStatus.REMOVE;
    }
}
