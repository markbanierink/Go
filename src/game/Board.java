package game;

import helper.Stone;

import static helper.Stone.*;

/**
 * Created by mark.banierink on 16-1-2017.
 * The Board class represents the board on which the game is played
 */
public class Board {

    private int size;
    private Stone[][] fields;
    private String boardString;

    /**
     * The constructor of the board
     * @param boardsize integer representing the board size
     */
    public Board(int boardsize) {
        this.size = boardsize;
        this.fields = new Stone[boardsize][boardsize];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < getBoardsize(); i++) {
            for (int j = 0; j < getBoardsize(); j++) {
                emptyField(i, j);
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
        return ( (x >= 0 && x < getBoardsize()) && (y >= 0 && y < getBoardsize()) );
    }

    /**
     * returns the size of this board
     * @return integer with the board size
     */
    public int getBoardsize() {
        return this.size;
    }

    protected Board setField(int x, int y, Stone stone) {
        if (isField(x, y)) {
            this.fields[x][y] = stone;
        }
        return this;
    }

    private void emptyField(int x, int y) {
        if (!isEmpty(x, y)) {
            setField(x, y, EMPTY);
        }
    }

    protected Board boardCopy() {
        Board newBoard = new Board(getBoardsize());
        for (int i = 0; i < getBoardsize(); i++) {
            for (int j = 0; j < getBoardsize(); j++) {
                newBoard.setField(i, j, getField(i, j));
            }
        }
        return newBoard;
    }

    protected String boardToString() {
        String boardString = "";
        for (int i = 0; i < this.getBoardsize(); i++) {
            for (int j = 0; j < this.getBoardsize(); j++) {
                boardString += getField(i, j);
            }
        }
        return boardString;
    }

}
