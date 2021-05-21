package org.volkov;

import javafx.util.Pair;

import java.util.*;

public class Minesweeper {

    private final int minesCount;
    private final int height, width;

    private final Cell[][] field;

    private final Collection<Mine> mines = new HashSet<>();

    private int flagsAvailable;


    public Minesweeper(int minesCount, int height, int width) {
        if (minesCount <= 0)
            throw new IllegalArgumentException("Mines count must be positive!");

        if (height <= 0 || width <= 0)
            throw new IllegalArgumentException("Size of field must be positive!");

        this.minesCount = minesCount;
        this.height = height;
        this.width = width;

        flagsAvailable = minesCount;

        field = new Cell[height][width];

        for (int i  = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = new Cell(j, i);
            }
        }
    }

    public int getNearMinesCount(int x, int y) {
        return field[y][x].minesNear;
    }

    public boolean isMine(int x, int y) {
        return field[y][x].isMine();
    }

    public boolean isOpened(int x, int y) {
        return field[y][x].opened;
    }

    public boolean isFlagged(int x, int y) {
        return field[y][x].isFlagged;
    }

    public void setFlag(int x, int y) {
        field[y][x].isFlagged = true;
        flagsAvailable--;
    }

    public void clearFlag(int x, int y) {
        field[y][x].isFlagged = false;
        flagsAvailable++;
    }

    public void open(int x, int y) {
        field[y][x].opened = true;
    }

    public boolean allMinesFlagged() {
        for (Mine mine : mines) {
            if (!mine.isFlagged())
                return false;
        }
        return true;
    }

    public Collection<Mine> getMines() {
        return mines;
    }


    public void initField(int openedY, int openedX) {
        // инициализация мин
        Collection<Pair<Integer, Integer>> minesPositions = generateMinesPositions(new Pair<>(openedY, openedX));

        for (Pair<Integer, Integer> position : minesPositions) {
            Mine mine = new Mine(position.getValue(), position.getKey());
            field[position.getKey()][position.getValue()] = mine;
            mines.add(mine);
        }

        // init count of near mines
        for (Pair<Integer, Integer> position : minesPositions) {
            for (Cell mineNeighbour : getNeighbours(position.getKey(), position.getValue())) {
                mineNeighbour.minesNear++;
            }
        }



        field[openedY][openedX].opened = true;
    }

    public Collection<Cell> getNearestSafeNeighbours(int y, int x) {

        Set<Cell> resultNeighbours = new HashSet<>();

        if (getNearMinesCount(x, y) != 0) {
            return resultNeighbours;
        }


            Set<Cell> neighbours = new HashSet<>(getNeighbours(y, x));
        neighbours.removeIf(Cell::isMine);

        Set<Cell> addedNeighbours = new HashSet<>();

        while (true) {
            boolean changes = false;
            for (Cell neighbour : neighbours) {
                if (neighbour.minesNear == 0) {
                    addedNeighbours.addAll(getNeighbours(neighbour.y, neighbour.x));
                    changes = true;
                }
                resultNeighbours.add(neighbour);
            }
            neighbours.clear();
            addedNeighbours.removeIf(Cell::isMine);
            addedNeighbours.removeAll(resultNeighbours);
            neighbours.addAll(addedNeighbours);
            if (!changes)
                break;
        }

        return resultNeighbours;
    }


    private Cell getTopLeft(int y, int x) {
        if (y > 0) {
            if (y % 2 != 0) {
                    return field[y - 1][x];
            } else {
                if (x > 0)
                    return field[y - 1][x - 1];
            }
        }
        return null;
    }

    private Cell getTopRight(int y, int x) {
        if (y > 0) {
            if (y % 2 == 0) {
                return field[y - 1][x];
            } else {
                if (x < width -1)
                    return field[y - 1][x + 1];
            }
        }
        return null;
    }

    private Cell getLeft(int y, int x) {
        if (x > 0)
            return field[y][x - 1];
        return null;
    }

    private Cell getRight(int y, int x) {
        if (x < width - 1)
            return field[y][x + 1];
        return null;
    }

    private Cell getBottomLeft(int y, int x) {
        if (y < height - 1) {
            if (y % 2 != 0) {
                return field[y + 1][x];
            } else {
                if (x > 0)
                    return field[y + 1][x - 1];
            }
        }
        return null;
    }

    private Cell getBottomRight(int y, int x) {
        if (y < height - 1) {
            if (y % 2 == 0) {
                return field[y + 1][x];
            } else {
                if (x < width -1)
                    return field[y + 1][x + 1];
            }
        }
        return null;
    }

    public Collection<Cell> getNeighbours(int y, int x) {
        Collection<Cell> neighbours = new HashSet<>(6);

        Cell left = getLeft(y, x);
        if (left != null) {
            neighbours.add(left);
        }

        Cell right = getRight(y, x);
        if (right != null) {
            neighbours.add(right);
        }

        Cell topLeft = getTopLeft(y, x);
        if (topLeft != null) {
            neighbours.add(topLeft);
        }

        Cell topRight = getTopRight(y, x);
        if (topRight != null) {
            neighbours.add(topRight);
        }

        Cell bottomLeft = getBottomLeft(y, x);
        if (bottomLeft != null) {
            neighbours.add(bottomLeft);
        }

        Cell bottomRight = getBottomRight(y, x);
        if (bottomRight != null) {
            neighbours.add(bottomRight);
        }
        return neighbours;
    }

    private Collection<Pair<Integer, Integer>> generateMinesPositions(Pair<Integer, Integer> sureNotMine) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>(width * height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                positions.add(i * width + j, new Pair<>(i, j));
            }
        }
        Collections.shuffle(positions);

        if (positions.subList(0, minesCount).contains(sureNotMine)) {
            positions.remove(sureNotMine);
        }

        return positions.subList(0, minesCount);
    }

    public int getFlagsAvailable() {
        return flagsAvailable;
    }


    public static class Cell {

        private final int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int minesNear = 0;
        private boolean isFlagged;

        private boolean opened;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isFlagged() {
            return isFlagged;
        }

        public int getMinesNear() {
            return minesNear;
        }

        public boolean isMine() {
            return false;
        }

        public boolean isOpened() {
            return opened;
        }
    }

    public static class Mine extends Cell {

        public Mine(int x, int y) {
            super(x, y);
        }

        @Override
        public boolean isMine() {
            return true;
        }
    }
}
