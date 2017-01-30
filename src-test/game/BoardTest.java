package game;

import org.junit.jupiter.api.*;

import static helper.enums.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 21-1-2017.
 */
public class BoardTest {

    private final static int BOARD_SIZE = 5;
    private final static int MOVES_PER_TURN = 1;
    private final static int PLAYERS_PER_GAME = 2;

    private Board board1 = new Board(BOARD_SIZE);
    private Board board2 = new Board(BOARD_SIZE);
    private Board board3 = new Board(BOARD_SIZE);
    private Board board4 = new Board(BOARD_SIZE + 1);
    private Game game = new Game(BOARD_SIZE, MOVES_PER_TURN, PLAYERS_PER_GAME);

    @BeforeEach
    void setUp() {
        board3.setField(0, 0, BLACK);
    }

    @Test
    void testIsEmpty() {
        assertTrue(board1.isEmpty(0, 0));
        assertFalse(board3.isEmpty(0, 0));
    }

    @Test
    void testIsField() {
        assertTrue(board1.isField(0, 0));
        assertFalse(board1.isField(BOARD_SIZE, BOARD_SIZE));
        assertFalse(board1.isField(-1, -1));
    }

    @Test
    void testGetBoardSize() {
        assertEquals(BOARD_SIZE, board1.getBoardSize());
    }

    @Test
    void testSetField() {
        Board boardNew = board1.setField(0, 0, BLACK);
        assertEquals(board3, boardNew);
    }

    @Test
    void testBoardCopy() {
        Board copy = board1.boardCopy();
        assertEquals(board1, copy);
    }

    @Test
    void testEquals() {
        assertFalse(board1.equals(null));
        assertTrue(board1.equals(board1));
        assertFalse(board1.equals(game));
        assertFalse(board1.equals(board4));
        assertFalse(board1.equals(board3));
        assertTrue(board1.equals(board2));
    }
}
