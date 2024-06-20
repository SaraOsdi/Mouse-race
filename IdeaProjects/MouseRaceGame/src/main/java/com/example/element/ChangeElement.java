package com.example.element;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class ChangeElement extends GameElement {

    private boolean isCollectState;
    private final Timeline changeStateTimeline;
    private final Timeline rotationTimeline;

    public ChangeElement() {
        // Create a triangle shape
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                0.0, 0.0,  // Vertex 1
                30.0, 0.0, // Vertex 2
                15.0, 30.0 // Vertex 3
        );
        triangle.setFill(Color.GREEN);

        this.shape = triangle;
        this.isCollectible = true;
        this.isCollectState = true;

        // Initialize the change state timeline
        changeStateTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> changeState()));
        changeStateTimeline.setCycleCount(Timeline.INDEFINITE);
        changeStateTimeline.play();

        // Initialize the rotation timeline
        rotationTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> rotate()));
        rotationTimeline.setCycleCount(Timeline.INDEFINITE);
        rotationTimeline.play();
    }

    private void changeState() {
        if (isCollectState) {
            shape.setFill(Color.RED);
            isCollectible = false;
        } else {
            shape.setFill(Color.GREEN);
            isCollectible = true;
        }
        isCollectState = !isCollectState;
    }

    private void rotate() {
        shape.setRotate(shape.getRotate() + 5);
    }


    @Override
    public void stopMovement() {
        changeStateTimeline.stop();
        rotationTimeline.stop();
    }
}
