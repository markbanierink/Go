package game;

import helper.enums.Stone;

import static helper.enums.Stone.*;

/**
 * Player of the game. Used by both Server and Client
 *
 * @author Mark Banierink
 */
public class Player {

    private Stone stone;
    private String name;

    /**
     * Constructor of the player
     * @param name String with the name of the player
     */
    public Player(String name) {
        this.name = name;
        stone = EMPTY;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the Stone of this Player
     * @return Stone if set, else Null
     */
    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }
}
