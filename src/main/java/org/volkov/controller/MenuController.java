package org.volkov.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.volkov.view.MenuView;

public class MenuController extends MenuView {

    @FXML
    private Button startButton;

    @FXML
    private Spinner<Integer> fieldWidthSpinner, fieldHeightSpinner;

    @FXML
    private Spinner<Integer> minesCountSpinner;

    @FXML
    public void onStartClick() {
        int minesCount = minesCountSpinner.getValue();
        int fieldWidth = fieldWidthSpinner.getValue();
        int fieldHeight = fieldHeightSpinner.getValue();

        if (minesCount < fieldWidth * fieldHeight) {
            Stage currentStage = (Stage) startButton.getScene().getWindow();
            showGameScene(currentStage, minesCount, fieldWidth, fieldHeight);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Некорректные начальные парамеры!");
            alert.setTitle("Некорректные начальные парамеры!");
            alert.setContentText("Количество мин должно быть меньше количества клеток поля!");
            alert.showAndWait();
        }

    }
}