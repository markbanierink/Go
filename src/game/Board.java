package game;

import helper.Stone;

import static helper.Stone.*;

/**
 * Created by mark.banierink on 16-1-2017.
 * The Board class represents the board on which the game is played
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
        this.fields = new Stone[boardSize][boardSize];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                setFieldEmpty(i, j);
            }
        }
    }

    protected boolean isEmpty(int x, int y) {
        return (getField(x, y) == EMPTY);
    }

    private Stone getField(int x, int y) {
        Stone result = null;
        if (isField(x, y)) {
            result = this.fields[x][y];
        }
        return result;
    }

    protected boolean isField(int x, int y) {
        return ( (x >= 0 && x < getBoardSize()) && (y >= 0 && y < getBoardSize()) );
    }

    /**
     * returns the size of this board
     * @return integer with the board size
     */
    public int getBoardSize() {
        return this.boardSize;
    }

    protected Board setField(int x, int y, Stone stone) {
        if (isField(x, y)) {
            this.fields[x][y] = stone;
        }
        return this;
    }

    private void setFieldEmpty(int x, int y) {
        if (!isEmpty(x, y)) {
            setField(x, y, EMPTY);
        }
    }

    protected Board boardCopy() {
        Board newBoard = new Board(getBoardSize());
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                newBoard.setField(i, j, this.getField(i, j));
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
        if (this.getBoardSize() != board.getBoardSize()) {
            return false;
        } else {
            for (int i = 0; i < this.getBoardSize(); i++) {
                for (int j = 0; j < this.getBoardSize(); j++) {
                    if (!this.getField(i, j).equals(board.getField(i, j))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
