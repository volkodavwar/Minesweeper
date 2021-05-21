package org.volkov.view;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class HexagonalField {


    private final Hex[][] hexes;

    static Image flagImage, mineImage;

    public HexagonalField(int width, int height, int hexesInWidth, int hexesInHeight, OnHexClickListener onHexClickListener){
        int hexWidth = (int) ((double) width / hexesInWidth);
        int hexHeight = (int) ((double) height / hexesInHeight);

        int rWidth = (int) ((double) hexWidth / Math.sqrt(3)); // радиус от длины
        int rHeight = (int) ((double) height / (2 * hexesInHeight)); // радиус от высоты

        // выбираем меньший из радиусов
        int hexRadius;
        if (rWidth < rHeight) {
            hexRadius = rWidth;
            hexHeight = hexRadius * 2;
        } else {
            hexRadius = rHeight;
            hexWidth = (int) (hexRadius * Math.sqrt(3));
        }

        flagImage = new Image("flag_icon.png", hexWidth, hexHeight, false, true);
        mineImage = new Image("mine_icon.png", hexWidth, hexHeight, false, true);

        hexes = new Hex[hexesInHeight][hexesInWidth];

        for (int i = 0; i < hexesInHeight; i++) {
            for (int j = 0; j < hexesInWidth; j++) {
                int y = i * hexRadius * 3 / 2;
                int offset = (i % 2 == 0) ? 0 : hexWidth / 2;
                hexes[i][j] = new Hex(j * hexWidth + offset, y, hexRadius, j, i, onHexClickListener);
            }
        }
    }

    public void addToPane(Pane pane) {
        for (Hex[] hexesRow : hexes) {
            for (Hex hex : hexesRow) {
                if (hex != null) {

                    hex.addToPane(pane);
                }
            }
        }
    }


    public void showMinesNear(int x, int y, int minesCount) {
        hexes[y][x].showNearMinesCount(minesCount);
    }

    public void showMine(int x, int y) {
        hexes[y][x].showMine();
    }


    public static class Hex extends Polygon {

        private final int x, y;

        private final StackPane layout = new StackPane();

        public Hex(double xInPx, double yInPx, int radius, int x, int y, OnHexClickListener onHexClickListener) {
            // creates the polygon using the corner coordinates
            double v = Math.sqrt(3) / 2.0;

            this.x = x;
            this.y = y;

            getPoints().addAll(
                    xInPx, yInPx,
                    xInPx, yInPx + radius,
                    xInPx + radius * v, yInPx + radius * (3.0 / 2.0),
                    xInPx + radius * Math.sqrt(3), yInPx + radius,
                    xInPx + radius * Math.sqrt(3), yInPx,
                    xInPx + radius * v, yInPx - radius/2
            );

            layout.setLayoutX(xInPx);
            layout.setLayoutY(yInPx);
            layout.setMaxSize(radius * v, radius * v);
            layout.setOnMouseClicked(getOnMouseClicked());

            // set up the visuals and a click listener for the tile
            setFill(Color.ANTIQUEWHITE);
            setStrokeWidth(1);
            setStroke(Color.BLACK);
            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    onHexClickListener.onHexClick(this);
                } else {
                    onHexClickListener.onHexFlagged(this);
                }

            });
        }

        public void addToPane(Pane pane) {
            layout.getChildren().add(this);
            pane.getChildren().add(layout);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void showNearMinesCount(int minesNear) {

            setFill(Color.LIGHTGRAY);
            if (minesNear > 0) {
                Label label = new Label(String.valueOf(minesNear));
                switch (minesNear) {

                    case 1: label.setTextFill(Color.BLUE); break;
                    case 2: label.setTextFill(Color.GREEN); break;
                    case 3: label.setTextFill(Color.RED); break;
                    case 4: label.setTextFill(Color.VIOLET); break;
                    case 5: label.setTextFill(Color.BROWN); break;
                }
                ((Pane) getParent()).getChildren().add(label);
                double fontSize = ((Pane) label.getParent()).getHeight() / 3;
                label.setStyle("-fx-font-size: " + fontSize + "px");
            }
        }

        private ImageView flag;

        public void showFlag() {
            flag = new ImageView(HexagonalField.flagImage);
            flag.setOnMouseClicked(getOnMouseClicked());
            layout.getChildren().add(flag);
        }

        public void clearFlag() {
            layout.getChildren().remove(flag);
            flag = null;
        }

        public void showMine() {
            ImageView mine = new ImageView(HexagonalField.mineImage);
            mine.setOnMouseClicked(getOnMouseClicked());
            layout.getChildren().add(mine);
        }

    }

    public interface OnHexClickListener {
        void onHexClick(Hex hex);
        void onHexFlagged(Hex hex);
    }

}

