package game;

import helper.enums.Stone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static helper.enums.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 25-1-2017.
 */
class PlayerTest {

    private final static String NAME = "Name";
    private final static Stone STONE = WHITE;
    private Player player1 = new Player(NAME);
    private Player player2 = new Player(NAME);

    @BeforeEach
    void setUp() {
        player1.setStone(STONE);
    }

    @Test
    void testGetName() {
        assertEquals(NAME, player1.getName());
    }

    @Test
    void testGetStone() {
        assertEquals(STONE, player1.getStone());
        assertEquals(EMPTY, player2.getStone());
    }

    @Test
    void testSetStone() {
        player2.setStone(STONE);
        assertEquals(STONE, player2.getStone());
    }
}