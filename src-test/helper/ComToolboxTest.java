package helper;

import org.junit.jupiter.api.Test;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
import static helper.Stone.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by mark.banierink on 25-1-2017.
 */
class ComToolboxTest {

    private static final String SPACE = " ";
    private static final String STRING = "name";
    private static final String INT = "9";
    private static final String STONE = BLACK.toString();

    private String go = GO + SPACE + STRING + SPACE + INT;
    private String cancel = CANCEL.toString();
    private String move = MOVE + SPACE + INT + SPACE + INT;
    private String pass = PASS.toString();
    private String tableFlip = TABLEFLIP.toString();
    private String chat = CHAT + SPACE + STRING;
    private String waiting = WAITING.toString();
    private String ready = READY + SPACE + STONE + SPACE + STRING + SPACE + INT;
    private String valid = VALID + SPACE + STONE + SPACE + INT + SPACE + INT;
    private String invalid = INVALID + SPACE + STONE + SPACE + STRING;
    private String passed = PASSED + SPACE + STONE;
    private String tableFlipped = TABLEFLIPPED + SPACE + STONE;
    private String warning = WARNING + SPACE + STRING;
    private String end = END + SPACE + INT + SPACE + INT;

    private String string2 = STRING + SPACE + CHAT;
    private String string3 = CANCEL + SPACE + STRING;
    private String string4 = WHITE + SPACE + STRING;
    private String string5 = PASSED + SPACE + STRING;

    private String[] splitString1 = {GO.toString(), STRING, INT};
    private String[] splitString2 = {"WHITE"};

    @Test
    void testGetKeyword() {
        assertEquals(GO, getKeyword(go));
        assertEquals(null, getKeyword(string2));
        assertEquals(CANCEL, getKeyword(string3));
        assertEquals(null, getKeyword(string4));
    }

    @Test
    void testSplitString() {
        assertArrayEquals(splitString1, splitString(go));
        assertArrayEquals(splitString2, splitString(WHITE.toString()));
    }

    @Test
    void testIsInteger() {
        assertTrue(isInteger(INT));
        assertFalse(isInteger(STRING));
        assertFalse(isInteger(go));
    }

    @Test
    void testIsBoardSize() {
        assertTrue(isBoardSize(9));
        assertFalse(isBoardSize(1));
        assertFalse(isBoardSize(132));
        assertFalse(isBoardSize(0));
        assertFalse(isBoardSize(-3));
        assertFalse(isBoardSize(8));
    }

    @Test
    void testCreateCommandGo() {
        assertEquals(go, createCommandGo(STRING, 9));
    }

    @Test
    void testIsValidCommand() {
        assertTrue(isValidCommand(GO, go));
        assertTrue(isValidCommand(WAITING, waiting));
        assertTrue(isValidCommand(CANCEL, cancel));
        assertTrue(isValidCommand(READY, ready));
        assertTrue(isValidCommand(MOVE, move));
        assertTrue(isValidCommand(VALID, valid));
        assertTrue(isValidCommand(INVALID, invalid));
        assertTrue(isValidCommand(PASS, pass));
        assertTrue(isValidCommand(PASSED, passed));
        assertTrue(isValidCommand(TABLEFLIP, tableFlip));
        assertTrue(isValidCommand(TABLEFLIPPED, tableFlipped));
        assertTrue(isValidCommand(CHAT, chat));
        assertTrue(isValidCommand(WARNING, warning));
        assertTrue(isValidCommand(END, end));

        assertFalse(isValidCommand(GO, waiting));
        assertFalse(isValidCommand(WAITING, go));
        assertFalse(isValidCommand(CANCEL, go));
        assertFalse(isValidCommand(READY, go));
        assertFalse(isValidCommand(MOVE, go));
        assertFalse(isValidCommand(VALID, go));
        assertFalse(isValidCommand(INVALID, go));
        assertFalse(isValidCommand(PASS, go));
        assertFalse(isValidCommand(PASSED, go));
        assertFalse(isValidCommand(TABLEFLIP, go));
        assertFalse(isValidCommand(TABLEFLIPPED, go));
        assertFalse(isValidCommand(CHAT, go));
        assertFalse(isValidCommand(WARNING, go));
        assertFalse(isValidCommand(END, go));

        assertFalse(isValidCommand(PASSED, string5));
    }

}