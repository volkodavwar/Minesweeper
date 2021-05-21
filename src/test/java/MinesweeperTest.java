import org.junit.jupiter.api.RepeatedTest;
import org.volkov.Minesweeper;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class MinesweeperTest {

    private Minesweeper minesweeper;
    private int minesCount, fieldWidth, fieldHeight;

    @RepeatedTest(100)
    public void testMinesweeper() {
        minesCount = ThreadLocalRandom.current().nextInt(2, 100 + 1);
        fieldWidth = ThreadLocalRandom.current().nextInt(2, 40 + 1);
        fieldHeight = ThreadLocalRandom.current().nextInt(2, 40 + 1);

        // количество бомб < количества ячеек поля
        assumeTrue(minesCount < fieldWidth * fieldHeight);

        minesweeper = new Minesweeper(minesCount, fieldHeight, fieldWidth);

        minesweeper.initField(0, 0);

        testCellsValues();
        testFirstMove();
        testMinesCount();
        testFlag();
    }


    private void testFlag() {
        assertEquals(minesCount, minesweeper.getFlagsAvailable());

        minesweeper.setFlag(0, 1);
        assertEquals(minesCount - 1, minesweeper.getFlagsAvailable());

        minesweeper.clearFlag(0, 1);
        assertEquals(minesCount, minesweeper.getFlagsAvailable());

        minesweeper.setFlag(0, 1);
        minesweeper.setFlag(1, 0);
        assertEquals(minesCount - 2, minesweeper.getFlagsAvailable());

    }

    private void testMinesCount() {
        assertEquals(minesCount, minesweeper.getMines().size());
    }

    private void testFirstMove() {
        // первая открытая клетка - не мина
        assertFalse(minesweeper.isMine(0, 0));
    }

    private void testCellsValues() {
        for (int i = 0; i < fieldHeight; i++) {
            for (int j = 0; j < fieldWidth; j++) {
                if (!minesweeper.isMine(j, i)) {
                    assertEquals(
                            minesweeper.getNeighbours(i, j).stream().filter(cell -> cell instanceof Minesweeper.Mine).count(),
                            minesweeper.getNearMinesCount(j, i),
                            "The nearest mines count not the same!"
                    );
                }
            }
        }
    }
}
