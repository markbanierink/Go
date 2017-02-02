package game;

import com.nedap.go.gui.GoGUIIntegrator;
import helper.enums.Stone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;

import static helper.enums.Keyword.*;
import static helper.enums.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 21-1-2017.
 *
 * @author Mark Banierink
 */
public class GameTest {

    private static final int BOARD_SIZE = 9;
    private static final int MOVES_PER_TURN = 1;
    private static final int PLAYERS_PER_GAME = 2;
    private Game game;
    private Player player1;
    private Player player2;
    private Player player3;
    private Board board = new Board(BOARD_SIZE);

    @BeforeEach
    void setUp() {
        this.game = new Game(BOARD_SIZE, MOVES_PER_TURN, PLAYERS_PER_GAME, false);
        player1 = new Player("Joop");
        player2 = new Player("Piet");
        player3 = new Player("Henk");
        player1.setStone(BLACK);
        player2.setStone(WHITE);
        game.addPlayer(player1);
        game.addPlayer(player3, YELLOW);
    }

    @Test
    void testGetBoard() {
        assertEquals(board, game.getBoard());
    }

    @Test
    void testGetTurn() {
        assertEquals(BLACK, game.getTurn());
    }

    @Test
    void testGetPlayers() {
        assertEquals(player1, game.getPlayers().get(0));
        assertEquals(player3, game.getPlayers().get(1));
    }

    @Test
    void testAddPlayer1() {
        assertEquals(2, game.getPlayers().size());
        assertTrue(game.getPlayers().get(1).getStone().equals(YELLOW));
    }

    @Test
    void testAddPlayer2() {
        assertTrue(game.getPlayers().get(0).getStone() != null);
    }

    @Test
    void testRemovePlayer() {
        game.addPlayer(player2);
        game.removePlayer(player2);
        assertEquals(2, game.getPlayers().size());
        assertFalse(game.getPlayers().contains(player2));
    }

    @Test
    void testGetPlayerByStone() {
        assertTrue(player1.equals(game.getPlayerByStone(BLACK)) || player1.equals(game.getPlayerByStone(WHITE)));
        assertEquals(player3, game.getPlayerByStone(YELLOW));
        assertEquals(null, game.getPlayerByStone(BLUE));
    }

    @Test
    void testPass() {
        assertEquals("", game.pass());
        assertEquals(WHITE, game.getTurn());
        assertTrue(game.pass().length() > 0);
    }

    @Test
    void tableFlip() {
        assertTrue(game.tableFlip().length() > 0);
    }

    @Test
    void testIsFinished() {
        for (int i = 0; i < game.getPlayers().size(); i++) {
            assertFalse(game.isFinished());
            game.pass();
        }
        assertTrue(game.isFinished());
    }

    @Test
    void testMove() {
        game.move(BLACK, 2, 2);
        assertEquals(game.getBoard().getField(2, 2), BLACK);
    }

    @Test
    void testIsValidPass() {
        assertTrue(game.isValidPass(BLACK));
        assertFalse(game.isValidPass(WHITE));
        assertFalse(game.isValidPass(BLUE));
    }

    @Test
    void testIsValidTableFlip() {
        assertTrue(game.isValidTableFlip(BLACK));
        assertFalse(game.isValidTableFlip(WHITE));
        assertFalse(game.isValidTableFlip(BLUE));
    }

    @Test
    void testIsValidMove() {
        assertTrue(game.isValidMove(BLACK, 1, 1));
        assertFalse(game.isValidMove(WHITE, 0, 0));
        game.move(BLACK, 3, 1);
        assertFalse(game.isValidMove(WHITE, 3, 1));
        game.move(WHITE, 3, 4);
        game.move(BLACK, 2, 2);
        game.move(WHITE, 2, 3);
        game.move(BLACK, 4, 2);
        game.move(WHITE, 4, 3);
        game.move(BLACK, 3, 3);
        game.move(WHITE, 3, 2);
        assertFalse(game.isValidMove(BLACK, 3, 3));
    }

    @Test
    void testCheckMoveValidity() {
        assertEquals(VALID.toString(), game.checkMoveValidity(BLACK, 1, 0));
    }

    @Test
    void testCopyThisGame() {
        Game futureGame = new Game(game.board.getBoardSize(), game.movesPerTurn, game.playersPerGame, true);
        futureGame = game.copyThisGame(futureGame, BLACK);
        futureGame.turn = WHITE;
        futureGame.board.setField(3, 4, WHITE);
        assertFalse(game.getTurn().equals(futureGame.getTurn()));
        assertEquals(WHITE, futureGame.getBoard().getField(3, 4));
        assertFalse(game.getBoard().getField(3, 4).equals(WHITE));
    }
}