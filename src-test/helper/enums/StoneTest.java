package helper.enums;

import org.junit.jupiter.api.Test;

import static helper.enums.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 25-1-2017.
 */
class StoneTest {

    private static final int NUM_PLAYERS = 2;

    @Test
    void testNextStone() {
        assertEquals(WHITE, BLACK.nextStone(NUM_PLAYERS));
        assertEquals(BLACK, WHITE.nextStone(NUM_PLAYERS));
        assertEquals(EMPTY, GREEN.nextStone(NUM_PLAYERS));
    }

    @Test
    void testRandomStone() {
        int black = 0;
        int white = 0;
        for (int i = 0; i < 100; i++) {      // Loop for bigger chance on full coverage
            Stone stone = randomStone(NUM_PLAYERS);
            if (stone.equals(BLACK)) {
                black++;
            }
            else if (stone.equals(WHITE)) {
                white++;
            }
            assertFalse((stone.equals(EMPTY) || stone.equals(RED)) || stone.equals(GREEN) || stone.equals(BLUE) || stone.equals(YELLOW));
        }
        assertTrue(black > 40);
        assertTrue(white > 40);
    }
}