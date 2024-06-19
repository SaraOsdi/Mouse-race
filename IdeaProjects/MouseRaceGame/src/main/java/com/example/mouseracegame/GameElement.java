package com.example.mouseracegame;

import javafx.scene.shape.Shape;

public abstract class GameElement {
    protected Shape shape;
    protected boolean isCollectible;

    public abstract void onClicked();

    public Shape getShape() {
        return shape;
    }

    public boolean isCollectible() {
        return isCollectible;
    }

    public void setCollectible(boolean isCollectible) {
        this.isCollectible = isCollectible;
    }


    public void update() {

    }
}
