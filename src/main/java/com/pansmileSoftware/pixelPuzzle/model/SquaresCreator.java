package com.pansmileSoftware.pixelPuzzle.model;


import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;


/**
 * This class creates an Array of Squares which keeps all data of puzzle's parts.
 */
public class SquaresCreator {

    private SquaresCreator() {}

    public static Square[] createSquares(Image image, int sideSize) {
        double sideLength = image.getWidth()/sideSize;
        Square[] squares = new Square[sideSize * sideSize];
        PixelReader reader = image.getPixelReader();

        int offsetX = 0;
        int offsetY = 0;
        int number = 0;
        for (int i = 0; i < sideSize; i++) {
            for (int j = 0; j < sideSize; j++) {
                WritableImage tmp = new WritableImage(reader, offsetX, offsetY, (int) sideLength, (int) sideLength);
                Square square = new Square(tmp, number, sideSize);
                if (number == sideSize * sideSize -1) {
                    square.setEmpty(true);
                }
                squares[number] = square;
                number++;
                offsetX += sideLength;
            }
            offsetX = 0;
            offsetY += sideLength;
        }
        return squares;
    }

}
