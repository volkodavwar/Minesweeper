package org.volkov.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.volkov.controller.Controller;
import org.volkov.main.MainFx;

import java.io.IOException;

public class MenuView {

    public void showGameScene(Stage stage, int minesCount, int fieldWidth, int fieldHeight) {
        Parent root = null;
        Controller controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/minesweeper_layout.fxml"));
            root = loader.load();

            controller = loader.getController();
            controller.setMinesCount(minesCount);
            controller.setFieldWidth(fieldWidth);
            controller.setFieldHeight(fieldHeight);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Scene scene = new Scene(root, MainFx.WIDTH, MainFx.HEIGHT);

        stage.setScene(scene);
        stage.show();

        controller.init();
    }
}
