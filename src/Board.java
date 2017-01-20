/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Board {

    private int size;
    private Stone[][] fields;

    public Board(int boardsize) {
        this.size = boardsize;
        this.fields = new Stone[boardsize][boardsize];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                emptyField(i, j);
            }
        }
    }

    public boolean isEmpty(int x, int y) {
        return (getField(x, y) == Stone.EMPTY);
    }

    public Stone getField(int x, int y) {
        Stone result = null;
        if (isField(x, y)) {
            result = this.fields[x][y];
        }
        return result;
    }

    private boolean isField(int x, int y) {
        return ( (x >= 0 && x < getSize()) && (y >= 0 && y < getSize()) );
    }

    public int getSize() {
        return this.size;
    }

    public void setField(int x, int y, Stone stone) {
        if (isField(x, y)) {
            this.fields[x][y] = stone;
        }
    }

    public void emptyField(int x, int y) {
        if (!isEmpty(x, y)) {
            setField(x, y, Stone.EMPTY);
        }
    }

}
