package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static helper.enums.Stone.*;

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

    @BeforeEach
    void setUp() {
        this.game = new Game(BOARD_SIZE, MOVES_PER_TURN, PLAYERS_PER_GAME);
        Player player1 = new Player("Mark");
        Player player2 = new Player("Piet");
        Player player3 = new Player("Joop");
        player1.setStone(BLACK);
        player2.setStone(WHITE);
        player3.setStone(RED);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
    }

    @Test
    void testGo() {

    }

    @Test
    void testGetOpponents() {

    }

    @Test
    void testGetBoard() {

    }

    @Test
    void testGetPlayers() {

    }

    @Test
    void testMaxPlayers() {

    }

    @Test
    void testAddPlayer() {

    }

    @Test
    void testRemovePlayer() {

    }

    @Test
    void testGetPlayerByStone() {

    }

    @Test
    void testPass() {

    }

    @Test
    void testTableFlip() {

    }

    @Test
    void testMove() {

    }

    @Test
    void testIsValidPass() {

    }

    @Test
    void testIsValidTableFlip() {

    }

    @Test
    void testCheckMoveValidity() {

    }
}