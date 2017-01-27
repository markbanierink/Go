package helper.enums;

import java.util.Random;

/**
 * Provides the Stones used in the game
 *
 * @author Mark Banierink
 */
public enum Stone {

    EMPTY, BLACK, WHITE, RED, GREEN, BLUE, YELLOW;

    /**
     * Method to get the Stone of the opponent
     * @param numPlayers int to know when to go to the first
     * @return Enum of the next stone in the Enum list, EMPTY if the current stone is out of range numPlayers or if no match is found
     */
    public Stone nextStone(int numPlayers) {
        int i = 0;
        for (Stone stone : Stone.values()) {
            if (this == stone) {
                if (i == numPlayers) {
                    i = 0;
                }
                else if (i > numPlayers) {
                    i = -1;
                }
                return Stone.values()[i + 1];
            }
            i++;
        }
        return EMPTY;
    }

    /**
     * Random generator to choose a random stone
     * @param numPlayers int as a limit of the Stones to choose from
     * @return Stone Enum, but never the first element (EMPTY)
     */
    public static Stone randomStone(int numPlayers) {
        int randomValue = (new Random()).nextInt(numPlayers) + 1;
        return Stone.values()[randomValue];
    }

}