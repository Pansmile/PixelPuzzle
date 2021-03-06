package com.pansmileSoftware.pixelPuzzle.model;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.WritableImage;

/**
 * An instance of this class keeps data all about single puzzle's part.
 */
public class Square {
    private WritableImage image;
    private double width;
    private final int initialNumber;
    private int currentNumber;
    private double positionX;
    private double positionY;
    private boolean isEmpty = false;
    private Bounds bounds;
    private int sideSize;

    public Square(WritableImage image, int number, int sideSize) {
        this.image = image;
        initialNumber = number;
        currentNumber = initialNumber;
        width = image.getWidth();
        this.sideSize = sideSize;

        moveToNumber(number, sideSize);
    }

    //Computing layout position of a square on canvas.
    private double[] getXY(int number, int sideSize) {
        double[] XYArray = new double[2];
        for (int i = sideSize, cycleCount = 0; i <= sideSize * sideSize; i += sideSize, cycleCount++) {
            int offset = i - sideSize;
            if (number < i) {
                XYArray[0] = number * width - offset * width;
                XYArray[1] = cycleCount * width;
                break;

            }
        }

        return XYArray;
    }

    //Changing or setting position variables;
    public void moveToNumber(int newNumber, int sideSize) {
        currentNumber = newNumber;
        double[] tmp = getXY(newNumber, sideSize);
        positionX = tmp[0];
        positionY = tmp[1];
        bounds = new BoundingBox(positionX, positionY, width, width);
    }

    //Defining if the Empty Square is a neighbour of current square;
    private boolean canBeMoved(int numberOfEmpty, int sideSize) {
        double[] layoutOfEmpty = getXY(numberOfEmpty, sideSize);
        double currentLayoutIndex, emptyLayoutIndex;
        if (positionX == layoutOfEmpty[0]) {
            currentLayoutIndex = positionY;
            emptyLayoutIndex = layoutOfEmpty[1];
        } else if (positionY == layoutOfEmpty[1]) {
            currentLayoutIndex = positionX;
            emptyLayoutIndex = layoutOfEmpty[0];
        } else {
            return false;
        }

        return currentLayoutIndex - width == emptyLayoutIndex || currentLayoutIndex + width == emptyLayoutIndex;
    }

    public static void  move(Square square, Square emptySquare) {
        if (emptySquare.isEmpty()) {
            int numberOfEmpty = emptySquare.getCurrentNumber();
            if (square.canBeMoved(numberOfEmpty, square.sideSize)) {
                int squareNumber = square.getCurrentNumber();
                square.moveToNumber(numberOfEmpty, square.sideSize);
                emptySquare.moveToNumber(squareNumber, square.sideSize);
            }

        }
    }

    public double getPositionX() {
        return positionX;
    }
    public double getPositionY() {
        return positionY;
    }

    public WritableImage getImage() {
        return image;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }
    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isOnInitialPlace() {
        return initialNumber == currentNumber;
    }

    public int getInitialNumber() {
        return initialNumber;
    }
    public int getCurrentNumber() {
        return currentNumber;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public double getWidth() {
        return width;
    }

}
