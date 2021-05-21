package org.volkov.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.volkov.Minesweeper;
import org.volkov.view.HexagonalField;

public class Controller implements HexagonalField.OnHexClickListener {

    @FXML
    private Label flagsRemain;


    @FXML
    private Pane field;

    private int minesCount;
    private int fieldWidth, fieldHeight;

    private Minesweeper minesweeper;
    private HexagonalField hexagonalField;

    private boolean wasFirstMove = false;
    private boolean finish = false;

    public void init() {
        finish = false;
        wasFirstMove = false;
        hexagonalField = new HexagonalField((int) field.getWidth(), (int) field.getHeight(), fieldWidth, fieldHeight, this);
        field.getChildren().clear();
        hexagonalField.addToPane(field);
        minesweeper = new Minesweeper(minesCount, fieldHeight, fieldWidth);

        flagsRemain.setText(String.valueOf(minesweeper.getFlagsAvailable()));
    }


    public void setMinesCount(int minesCount) {
        this.minesCount = minesCount;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    @Override
    public void onHexClick(HexagonalField.Hex mine) {

        if (finish) {
            return;
        }

        if (minesweeper.isFlagged(mine.getX(), mine.getY()) || minesweeper.isOpened(mine.getX(), mine.getY()))
            return;

        if (!wasFirstMove) {
            minesweeper.initField(mine.getY(), mine.getX());
            wasFirstMove = true;
        }

        if (!minesweeper.isMine(mine.getX(), mine.getY())) {

            mine.showNearMinesCount(minesweeper.getNearMinesCount(mine.getX(), mine.getY()));

            for (Minesweeper.Cell cell : minesweeper.getNearestSafeNeighbours(mine.getY(), mine.getX())) {
                minesweeper.open(cell.getX(), cell.getY());
                hexagonalField.showMinesNear(cell.getX(), cell.getY(), minesweeper.getNearMinesCount(cell.getX(), cell.getY()));
            }
        } else {
            mine.showMine();

            for (Minesweeper.Mine m: minesweeper.getMines()) {
                hexagonalField.showMine(m.getX(), m.getY());
            }

            finish = true;
            showLoseMessage();



        }
        minesweeper.open(mine.getX(), mine.getY());

        if (minesweeper.getFlagsAvailable() == 0 && minesweeper.allMinesFlagged()) {
            finish = true;
            showWinMessage();
        }

    }

    @Override
    public void onHexFlagged(HexagonalField.Hex hex) {

        if (finish || !wasFirstMove)
            return;

        if (minesweeper.isOpened(hex.getX(), hex.getY()))
            return;

        if (minesweeper.isFlagged(hex.getX(), hex.getY())) {
            minesweeper.clearFlag(hex.getX(), hex.getY());
            hex.clearFlag();

        } else {
            if (minesweeper.getFlagsAvailable() == 0)
                return;

            minesweeper.setFlag(hex.getX(), hex.getY());
            hex.showFlag();
        }

        flagsRemain.setText(String.valueOf(minesweeper.getFlagsAvailable()));

        if (minesweeper.getFlagsAvailable() == 0 && minesweeper.allMinesFlagged()) {
            finish = true;
            showWinMessage();
        }
    }

    public void onRestartClick() {
        init();
    }

    private void showLoseMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Вы проиграли!");
        alert.setTitle("Увы");
        alert.setContentText("Попробуйте начать заново");
        alert.showAndWait();
    }

    private void showWinMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Вы выиграли!");
        alert.setTitle("Ура");
        alert.setContentText("Вам удалось победить, но попробуйте повторить на более высокой сложности");
        alert.showAndWait();
    }
}
