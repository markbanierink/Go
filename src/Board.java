/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Board {

    private int boardsize;
    private Stone[][] fields;

    public Board(int boardsize) {
        this.boardsize = boardsize;
        this.fields = new Stone[boardsize][boardsize];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < getBoardsize(); i++) {
            for (int j = 0; j < getBoardsize(); j++) {
                setField(i, j, Stone.EMPTY);
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
        return ( (x >= 0 && x < getBoardsize()) && (y >= 0 && y < getBoardsize()) );
    }

    public int getBoardsize() {
        return this.boardsize;
    }

    public void setField(int x, int y, Stone stone) {
        if (isEmpty(x, y)) {
            this.fields[x][y] = stone;
        }
    }

}
