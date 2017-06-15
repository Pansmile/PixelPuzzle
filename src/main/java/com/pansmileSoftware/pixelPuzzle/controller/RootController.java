package com.pansmileSoftware.pixelPuzzle.controller;

import com.pansmileSoftware.pixelPuzzle.app.Puzzle;
import com.pansmileSoftware.pixelPuzzle.model.ResourceManager;
import com.pansmileSoftware.pixelPuzzle.model.Square;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class controls the behaviour of the app.
 */
public class RootController {
    private boolean isStarted = false;
    private boolean isDefaultImage = true;

    @FXML
    private Spinner<Integer> sideSizeSpnr;
    @FXML
    private Spinner<Integer> shuffleStepSpnr;
    @FXML
    private Canvas canvas;

    public RootController(){}

    @FXML
    public void initialize() {
        sideSizeSpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                2, 10, 3));
        shuffleStepSpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, 10, 1));

        ResourceManager.getSideSizeProperty().bind(sideSizeSpnr.valueProperty());
        sideSizeSpnr.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (sideSizeSpnr.getValue() == 7) {
                try {
                    ResourceManager.loadImage(null, 721);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isStarted) {
                shuffleSquares();
            }
        });

    }

    @FXML
    public void mouseDragDropped(DragEvent dragEvent) {
        final Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            try {
                FileInputStream file = new FileInputStream(dragboard.getFiles().get(0));
                ResourceManager.setImageChanged(true);
                ResourceManager.loadImage(file, 720);

                isStarted = false;
                isDefaultImage = false;
                draw();

            } catch (IOException e) {
                e.printStackTrace();
            }
            dragEvent.setDropCompleted(true);
        }
        dragEvent.consume();
    }

    @FXML
    private  void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();

        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");

        if (db.hasFiles()) {
            if (isAccepted) {
                e.acceptTransferModes(TransferMode.COPY);
            }
        } else {
            e.consume();
        }
    }

    @FXML
    public void move(MouseEvent event) {
        if (isStarted) {
            Square[] squares = ResourceManager.getSquares();
            for (Square square : ResourceManager.getSquares()) {
                Bounds bounds = square.getBounds();

                if (bounds.contains(event.getSceneX(), event.getSceneY())) {
                    Square.move(square, squares[squares.length -1]);
                    ResourceManager.playRandom();
                    break;
                }
            }
            if (isComplete()) {
                isStarted = false;
                ResourceManager.playFinal();
            }
            draw();
        }
    }

    public void start(MouseEvent event) {
        isStarted = true;
        shuffleSquares();
    }

    private void shuffleSquares() {
        if (isStarted) {
            moveAllBack();
        }

        ArrayList<Integer> randomList = new ArrayList<>();
        int sideSize = sideSizeSpnr.getValue();
        for (int j = 0; j < sideSize * sideSize - 1; j++) {
            randomList.add(j);
        }

        Square[] squares = ResourceManager.getSquares();
        Collections.shuffle(randomList);
        while (!isShuffled()){

            for (int i = 0; i < shuffleStepSpnr.getValue() * shuffleStepSpnr.getValue(); i++) {
                for (int randomInt  : randomList) {
                    Square square = squares[randomInt];
                    Square empty = squares[squares.length - 1];
                    Square.move(square, empty);
                }
            }

        }
        draw();
    }

    private boolean isShuffled() {
        boolean isShuffled = true;
        for (Square square : ResourceManager.getSquares()) {
            if (square.isOnInitialPlace()) {
                isShuffled = false;
                break;
            }
        }
        return isShuffled;
    }

    public void draw() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (Square square : ResourceManager.getSquares()) {
            WritableImage image;
            double x = square.getPositionX();
            double y = square.getPositionY();
            double width = square.getWidth();
            if (square.isEmpty() && isStarted) {
                if (isDefaultImage) {
                   double tmpWidth = ResourceManager.getImage().getWidth()/ResourceManager.getSideSizeProperty().get();
                     Image empty = new Image(Puzzle.class.getResourceAsStream("/images/empty.png"),
                            tmpWidth, tmpWidth, true, false);
                    context.drawImage(empty, x, y, width, width);

                } else {
                    image = new WritableImage((int)square.getWidth(),(int) square.getWidth());
                    for (int i = 0; i < image.getWidth(); i++) {
                        for (int j = 0; j < image.getWidth(); j++) {
                            image.getPixelWriter().setColor(j, i, Color.WHITE);
                        }
                    }
                    context.drawImage(image, x, y, width, width);
                }

            } else {
                image = square.getImage();
                context.drawImage(image, x, y, width, width);
            }
        }
    }

    private boolean isComplete() {
        boolean isComplete = true;
        for (Square square : ResourceManager.getSquares()) {
            if (!square.isOnInitialPlace()) {
                isComplete = false;
                break;
            }
        }
        return isComplete;
    }

    private void moveAllBack() {
        for (Square square : ResourceManager.getSquares()) {
            square.moveToNumber(square.getInitialNumber(), ResourceManager.getSideSizeProperty().get());
        }
    }

}