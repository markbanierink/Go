package helper;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public enum Stone {

    EMPTY, BLACK, WHITE;

    public Stone other() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }

    public static Stone randomStone() {
       if (Math.random() < 0.5) {
           return BLACK;
       } else {
           return WHITE;
       }
    }

}
