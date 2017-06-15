package com.pansmileSoftware.pixelPuzzle.model;

import com.pansmileSoftware.pixelPuzzle.app.Puzzle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * This class takes care about all necessary resources.
 */
public class ResourceManager {
    private static Image image;
    private static File imageFile;
    private static byte[] imageBytes;
    private static boolean imageChanged;
    private static boolean isDefaultImage = true;
    private static IntegerProperty sideSizeProperty = new SimpleIntegerProperty();
    private static int lastSideSize;
    private static Square[] squares;
    private static MediaPlayer[] players;
    private static boolean[] isPlaying = new boolean[9];
    private static int previousNumber;


    public static void loadImage(File file, int requestedWidth) throws IOException {
        int finalWidthHeight;
        if (sideSizeProperty.get() == 7) {
            finalWidthHeight = 721;
        } else {
            finalWidthHeight = requestedWidth;
        }
        if (isDefaultImage) {
            InputStream stream = Puzzle.class.getResourceAsStream("/images/0.png");
            image = new Image(stream, finalWidthHeight, finalWidthHeight, false, false);
        } else {
            if (imageFile == null) {
                imageFile = file;
            }
            image = new Image(imageFile.toURI().toString(), finalWidthHeight, finalWidthHeight, false, false);
        }

        squares = SquaresCreator.createSquares(image,sideSizeProperty.get());
        lastSideSize = sideSizeProperty.get();
    }

    public static Image getImage() throws NullPointerException {
        return image;
    }

    public static Square[] getSquares() {
        if (squares == null || sideSizeProperty.get() != lastSideSize) {
            squares = SquaresCreator.createSquares(image, sideSizeProperty.get());
            lastSideSize = sideSizeProperty.get();
        }
        return squares;
    }

    public static void initPlayers() {
        players = new MediaPlayer[9];
        for (int i = 0; i < 9; i++) {
            players[i] = new MediaPlayer(new Media(Puzzle.class.getResource("/sounds/" + i + ".wav").toExternalForm()));
            if (i == 8) {
                players[i].setVolume(0.2);
            } else {
                players[i].setVolume(0.1);
            }
        }
        previousNumber = 0;
    }

    public static void playRandom() {
        Random random = new Random();
        int nextInt = random.nextInt(8);
        while (nextInt == previousNumber) {
            nextInt = random.nextInt(8);
        }
        previousNumber = nextInt;

        playNumber(nextInt);
    }

    public static void playFinal() {
       playNumber(players.length -1);
    }

    private static void playNumber(int number) {
        int secondsToWait;
        if (number == 8) {
            secondsToWait = 7;
        } else {
            secondsToWait = 2;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(secondsToWait), ae -> {
            if (isPlaying[number]) {
                players[number].stop();
                players[number].seek(Duration.seconds(0));
                isPlaying[number] = false;
            }

        }));

        if (!isPlaying[number]) {
            players[number].play();
            isPlaying[number] = true;
            timeline.play();

        } else {
            players[number].stop();
            players[number].seek(Duration.seconds(0));
            isPlaying[number] = false;
            playNumber(number);
        }
    }

    public static void setImageChanged(boolean hasChanged) {
        imageChanged = hasChanged;
    }
    public static void setIsDefaultImage(boolean isDefault) {
        isDefaultImage = isDefault;
    }

    public static IntegerProperty getSideSizeProperty() {
        return sideSizeProperty;
    }

}
