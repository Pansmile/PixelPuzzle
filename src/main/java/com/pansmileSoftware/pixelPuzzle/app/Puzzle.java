package com.pansmileSoftware.pixelPuzzle.app;

import com.pansmileSoftware.pixelPuzzle.controller.RootController;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.pansmileSoftware.pixelPuzzle.model.ResourceManager;

import java.io.File;

/**
 * Created by studio on 12.06.17.
 */
public class Puzzle extends Application{
    private IntegerProperty sideSizeProperty;
    private IntegerProperty shuffleStepProperty;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Puzzle.class.getResource("/views/rootView.fxml"));
        Parent root = loader.load();
        RootController controller = loader.getController();


        ResourceManager.loadImage(Puzzle.class.getResourceAsStream("/images/0.png"), 720);
        ResourceManager.initPlayers();
        controller.draw();


        Scene scene = new Scene(root, 720, 810);
        primaryStage.setScene(scene);
        primaryStage.setMaxWidth(720);
        primaryStage.setMaxHeight(810);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
