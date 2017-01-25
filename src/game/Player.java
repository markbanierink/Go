package game;

import helper.Stone;

/**
 * Created by mark.banierink on 16-1-2017.
 * Player of the game. Used by both Server and Client
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
    }

    public String getName() {
        return this.name;
    }

    /**
     * Gets the Stone of this Player
     * @return Stone if set, else Null
     */
    public Stone getStone() {
        return this.stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

}
