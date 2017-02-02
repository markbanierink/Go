package helper;

import game.Game;
import game.Player;
import helper.enums.Stone;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

import static helper.CommandToolbox.*;
import static helper.enums.Keyword.*;
import static helper.enums.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 25-1-2017.
 */
class ComToolboxTest {

    private static final String SPACE = " ";
    private static final String NAME1 = "name";
    private static final String NAME2 = "anotherName";
    private static final String INT_STRING8 = "8";
    private static final String INT_STRING9 = "9";
    private static final int INT8 = 8;
    private static final int INT9 = 9;
    private static final Stone STONE1 = BLACK;
    private static final Stone STONE2 = WHITE;
    private static final int BOARD_SIZE_MIN = 5;
    private static final int BOARD_SIZE_MAX = 131;

    private Player newPlayer1;
    private Player newPlayer2;

    private String player = PLAYER.toString() + SPACE + NAME1;
    private String go = GO.toString() + SPACE + INT9;
    private String cancel = CANCEL.toString();
    private String move = MOVE + SPACE + INT9 + SPACE + INT9;
    private String pass = PASS.toString();
    private String tableFlip = TABLEFLIP.toString();
    private String chat = CHAT + SPACE + NAME1 + ":" + SPACE + NAME2;
    private String waiting = WAITING.toString();
    private String ready = READY + SPACE + INT9 + SPACE + STONE1.toString().toLowerCase() + SPACE + NAME1 + SPACE + STONE2.toString().toLowerCase() + SPACE + NAME2;
    private String valid = VALID + SPACE + STONE1.toString().toLowerCase() + SPACE + INT9 + SPACE + INT9;
    private String invalid = INVALID + SPACE + STONE1.toString().toLowerCase() + SPACE + NAME1;
    private String passed = PASSED + SPACE + STONE1.toString().toLowerCase();
    private String tableFlipped = TABLEFLIPPED + SPACE + STONE1.toString().toLowerCase();
    private String warning = WARNING + SPACE + NAME1;
    private String end = END + SPACE + INT8 + SPACE + INT9;

    private String[] splitString1 = {GO.toString(), INT_STRING9};
    private String[] splitString2 = {"WHITE"};
    private int[] scores = {INT8, INT9};
    private List<Player> players = new ArrayList<>();
    private Game game = new Game(5, 1, 2, false);

    private String commandPlayer;
    private String commandGo;
    private String commandWaiting;
    private String commandCancel;
    private String commandMove;
    private String commandPass;
    private String commandTableFlip;
    private String commandChat;
    private String commandReady;
    private String commandValid;
    private String commandInvalid;
    private String commandPassed;
    private String commandTableFlipped;
    private String commandWarning;
    private String commandEnd;

    @BeforeEach
    void setUp() {
        this.newPlayer1 = new Player(NAME1);
        this.newPlayer2 = new Player(NAME2);
        newPlayer1.setStone(BLACK);
        newPlayer2.setStone(WHITE);
        players.add(newPlayer1);
        players.add(newPlayer2);
        game.addPlayer(newPlayer1);
        game.addPlayer(newPlayer2);
        commandPlayer = createCommandPlayer(newPlayer1);
        commandGo = createCommandGo(INT9);
        commandWaiting = createCommandWaiting();
        commandCancel = createCommandCancel();
        commandMove = createCommandMove(INT9, INT9);
        commandPass = createCommandPass();
        commandTableFlip = createCommandTableFlip();
        commandChat = createCommandChat(NAME1, NAME2);
        commandReady = createCommandReady(game, newPlayer1);
        commandValid = createCommandValid(STONE1, INT9, INT9);
        commandInvalid = createCommandInvalid(STONE1, NAME1);
        commandPassed = createCommandPassed(STONE1);
        commandTableFlipped = createCommandTableFlipped(STONE1);
        commandWarning = createCommandWarning(NAME1);
        commandEnd = createCommandEnd(scores);
    }

    @Test
    void testSplitString() {
        assertArrayEquals(splitString1, splitString(go));
        assertArrayEquals(splitString2, splitString(WHITE.toString()));
    }

    @Test
    void testIsInteger() {
        assertTrue(isInteger(INT_STRING9));
        assertFalse(isInteger(NAME1));
        assertFalse(isInteger(go));
    }

    @Test
    void testIsBoardSize() {
        assertTrue(isValidBoardSize(9, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
        assertFalse(isValidBoardSize(1, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
        assertFalse(isValidBoardSize(132, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
        assertFalse(isValidBoardSize(0, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
        assertFalse(isValidBoardSize(-3, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
        assertFalse(isValidBoardSize(8, BOARD_SIZE_MIN, BOARD_SIZE_MAX));
    }

    @Test
    void testCreateCommandPlayer() {
        assertEquals(player, commandPlayer);
    }

    @Test
    void testCreateCommandGo() {
        assertEquals(go, commandGo);
    }

    @Test
    void testCreateCommandWait() {
        assertEquals(waiting, commandWaiting);
    }

    @Test
    void testCreateCommandCancel() {
        assertEquals(cancel, commandCancel);
    }

    @Test
    void testCreateCommandMove() {
        assertEquals(move, commandMove);
    }

    @Test
    void testCreateCommandPass() {
        assertEquals(pass, createCommandPass());
    }

    @Test
    void testCreateCommandTableFlip() {
        assertEquals(tableFlip, commandTableFlip);
    }

    @Test
    void testCreateCommandChat() {
        assertEquals(chat, commandChat);
    }

    @Test
    void testCreateCommandReady() {
        assertEquals(ready, commandReady);
    }

    @Test
    void testCreateCommandValid() {
        assertEquals(valid, commandValid);
    }

    @Test
    void testCreateCommandInvalid() {
        assertEquals(invalid, commandInvalid);
    }

    @Test
    void testCreateCommandPassed() {
        assertEquals(passed, commandPassed);
    }

    @Test
    void testCreateCommandTableFlipped() {
        assertEquals(tableFlipped, commandTableFlipped);
    }

    @Test
    void testCreateCommandWarning() {
        assertEquals(warning, commandWarning);
    }

    @Test
    void testCreateCommandEnd() {
        assertEquals(end, commandEnd);
    }

    @Test
    void testIsPlayerCommand() {
        assertTrue(isPlayerCommand(commandPlayer));
        assertFalse(isPlayerCommand(commandGo));
    }

    @Test
    void playerCommandArguments() {

    }

    @Test
    void testIsGoCommand() {
        assertTrue(isGoCommand(commandGo));
        assertFalse(isGoCommand(commandPlayer));
    }

    @Test
    void goCommandArguments() {

    }

    @Test
    void testIsWaitingCommand() {
        assertTrue(isWaitingCommand(commandWaiting));
        assertFalse(isWaitingCommand(commandGo));
    }

    @Test
    void waitingCommandArguments() {

    }

    @Test
    void testIsCancelCommand() {
        assertTrue(isCancelCommand(commandCancel));
        assertFalse(isCancelCommand(commandGo));
    }

    @Test
    void cancelCommandArguments() {

    }

    @Test
    void testIsReadyCommand() {
        assertTrue(isReadyCommand(commandReady));
        assertFalse(isReadyCommand(commandGo));
    }

    @Test
    void readyCommandArguments() {

    }

    @Test
    void testIsMoveCommand() {
        assertTrue(isMoveCommand(commandMove));
        assertFalse(isMoveCommand(commandGo));
    }

    @Test
    void moveCommandArguments() {

    }

    @Test
    void testIsValidCommand() {
        assertTrue(isValidCommand(commandValid));
        assertFalse(isValidCommand(commandGo));
    }

    @Test
    void validCommandArguments() {

    }

    @Test
    void testIsInvalidCommand() {
        assertTrue(isInvalidCommand(commandInvalid));
        assertFalse(isInvalidCommand(commandGo));
    }

    @Test
    void testInvalidCommandArguments() {

    }

    @Test
    void testIsPassCommand() {
        assertTrue(isPassCommand(commandPass));
        assertFalse(isPassCommand(commandGo));
    }

    @Test
    void passCommandArguments() {

    }

    @Test
    void testIsPassedCommand() {
        assertTrue(isPassedCommand(commandPassed));
        assertFalse(isPassedCommand(commandGo));
    }

    @Test
    void passedCommandArguments() {

    }

    @Test
    void testIsTableFlipCommand() {
        assertTrue(isTableFlipCommand(commandTableFlip));
        assertFalse(isTableFlipCommand(commandGo));
    }

    @Test
    void tableFlipCommandArguments() {

    }

    @Test
    void testIsTableFlippedCommand() {
        assertTrue(isTableFlippedCommand(commandTableFlipped));
        assertFalse(isTableFlippedCommand(commandGo));
    }

    @Test
    void tableFlippedCommandArguments() {

    }

    @Test
    void testIsChatCommand() {
        assertTrue(isChatCommand(commandChat));
        assertFalse(isChatCommand(commandGo));
    }

    @Test
    void chatCommandArguments() {

    }

    @Test
    void testIsWarningCommand() {
        assertTrue(isWarningCommand(commandWarning));
        assertFalse(isWarningCommand(commandGo));
    }

    @Test
    void warningCommandArguments() {

    }

    @Test
    void testIsEndCommand() {
        assertTrue(isEndCommand(commandEnd));
        assertFalse(isEndCommand(commandGo));
    }

    @Test
    void endCommandArguments() {

    }
}