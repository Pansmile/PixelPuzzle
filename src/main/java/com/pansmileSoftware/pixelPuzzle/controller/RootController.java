package com.pansmileSoftware.pixelPuzzle.controller;

import com.pansmileSoftware.pixelPuzzle.app.Puzzle;
import com.pansmileSoftware.pixelPuzzle.model.ResourceManager;
import com.pansmileSoftware.pixelPuzzle.model.Square;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;




import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by studio on 12.06.17.
 */
public class RootController {
    private boolean isStarted = false;
    private boolean isDefaultImage = true;


    @FXML
    private Button multiFuncBttn;
    @FXML
    private Spinner<Integer> sideSizeSpnr;
    @FXML
    private Spinner<Integer> shuffleStepSpnr;
    @FXML
    private StackPane viewPane;
    @FXML
    private Canvas canvas;



    public RootController(){

    }


    @FXML
    public void initialize() {
        sideSizeSpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10, 3));
        shuffleStepSpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));



        ResourceManager.getSideSizeProperty().bind(sideSizeSpnr.valueProperty());
        sideSizeSpnr.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (sideSizeSpnr.getValue() == 7) {
                    try {
                        ResourceManager.scaleImage(721);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isStarted) {
                    shuffleSquares();
                }
            }
        });


    }

    public Button getMultiFuncBttn() {
        return multiFuncBttn;
    }

    public Spinner<Integer> getSideSizeSpnr() {
        return sideSizeSpnr;
    }

    public Spinner<Integer> getShuffleStepSpnr() {
        return shuffleStepSpnr;
    }

    public Pane getViewPane() {
        return viewPane;
    }





    public void start(MouseEvent event) {
        isStarted = true;
        shuffleSquares();
    }


    @FXML
    public void mouseDragDropped(DragEvent dragEvent) {
        final Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            try {
                InputStream file = new FileInputStream(dragboard.getFiles().get(0));
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
        getMultiFuncBttn().setDisable(false);
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
            if (square.isEmpty() && isStarted) {
                if (isDefaultImage) {
                   double tmpWidth = ResourceManager.getImage().getWidth()/ResourceManager.getSideSizeProperty().get();
                     Image empty = new Image(Puzzle.class.getResourceAsStream("/images/empty.png"),
                            tmpWidth, tmpWidth, true, false);
                    context.drawImage(empty,square.getPositionX(), square.getPositionY(), square.getWidth(), square.getWidth());

                } else {
                    image = new WritableImage((int)square.getWidth(),(int) square.getWidth());
                    for (int i = 0; i < image.getWidth(); i++) {
                        for (int j = 0; j < image.getWidth(); j++) {
                            image.getPixelWriter().setColor(j, i, Color.WHITE);
                        }
                    }
                    context.drawImage(image, square.getPositionX(), square.getPositionY(), square.getWidth(), square.getWidth());
                }

            } else {
                image = square.getImage();
                context.drawImage(image, square.getPositionX(), square.getPositionY(), square.getWidth(), square.getWidth());

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
            square.moveToNumber(square.getInitialNumber(), ResourceManager.getSideSizeProperty().get(), 1);
        }
    }

    public void setDefaultImage(boolean isDefault) {
        this.isDefaultImage = isDefault;
    }
}
