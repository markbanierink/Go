package helper;

/**
 * Created by mark.banierink on 16-1-2017.
 * Provides the Stones used in the game
 */
public enum Stone {

    EMPTY, BLACK, WHITE;

    /**
     * Method to get the Stone of the opponent
     * @return BLACK if current stone is WHITE, WHITE if current Stone is BLACK
     */
    public Stone other() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }

    /**
     * Random generator to choose a random stone
     * @return BLACK or WHITE
     */
    public static Stone randomStone() {
       if (Math.random() < 0.5) {
           return BLACK;
       } else {
           return WHITE;
       }
    }

}
