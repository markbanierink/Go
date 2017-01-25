package helper;

import java.util.Random;

/**
 * Created by mark.banierink on 16-1-2017.
 * Provides the Stones used in the game
 */
public enum Stone {

    EMPTY, BLACK, WHITE, RED, GREEN, BLUE, YELLOW;

    /**
     * Method to get the Stone of the opponent
     * @return Enum of the next stone in the Enum list, EMPTY if the current stone cannot be found
     */
    public Stone nextStone(int numPlayers) {
        int i = 0;
        Stone[] stones = Stone.values();
        for (Stone stone : stones) {
            if (this == stone) {
                if (i == numPlayers) {
                    i = 0;
                } else if (i > numPlayers) {
                    i = -1;
                }
                return Stone.values()[i+1];
            }
            i++;
        }
        return EMPTY;
    }

    /**
     * Random generator to choose a random stone
     * @return Stone Enum, but never the first element (EMPTY)
     */
    public static Stone randomStone(int numPlayers) {
        int randomValue = (new Random()).nextInt(numPlayers) + 1;
        return Stone.values()[randomValue];
    }

}