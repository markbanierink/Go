package game;

import helper.enums.Stone;
import java.util.Arrays;
import java.util.Objects;

import static helper.enums.Stone.*;

/**
 * The Board class represents the board on which the game is played
 * @author Mark Banierink
 */
public class Board {

    private int boardSize;
    private Stone[][] fields;

    /**
     * The constructor of the board
     * @param boardSize integer representing the board size
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        fields = new Stone[boardSize][boardSize];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                setFieldEmpty(i, j);
            }
        }
    }

    protected boolean isEmpty(int x, int y) {
        return (getField(x, y) == EMPTY);
    }

    public Stone getField(int x, int y) {
        Stone result = null;
        if (isValidField(x, y)) {
            result = fields[x][y];
        }
        return result;
    }

    protected boolean isValidField(int x, int y) {
        return ((x >= 0 && x < boardSize) && (y >= 0 && y < boardSize));
    }

    /**
     * returns the size of this board
     * @return integer with the board size
     */
    public int getBoardSize() {
        return boardSize;
    }

    protected Board setField(int x, int y, Stone stone) {
        if (isValidField(x, y)) {
            fields[x][y] = stone;
        }
        return this;
    }

    public void setFieldEmpty(int x, int y) {
        if (!isEmpty(x, y)) {
            setField(x, y, EMPTY);
        }
    }

    protected Board boardCopy() {
        Board newBoard = new Board(boardSize);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                newBoard.setField(i, j, getField(i, j));
            }
        }
        return newBoard;
    }

    /**
     * This method compares two board instances. It overrides the original equals method
     * @param object The Board object that it compares this object with
     * @return True if both Board instances contain the same occupation, False if not
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!(object instanceof Board)) {
            return false;
        }
        Board board = (Board)object;
        if (boardSize != board.getBoardSize()) {
            return false;
        }
        else {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (!getField(i, j).equals(board.getField(i, j))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Overrides the hashCode method
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(boardSize, fields);
    }
}
