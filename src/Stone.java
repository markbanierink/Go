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
}
