package com.example.element;

import javafx.scene.shape.Shape;

public abstract class GameElement {
    protected Shape shape;
    boolean isCollectible;


    public Shape getShape() {
        return shape;
    }

    public boolean isCollectible() {
        return isCollectible;
    }

    public abstract void stopMovement();
}
