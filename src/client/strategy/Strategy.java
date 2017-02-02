package client.strategy;

import client.Client;
import game.Game;
import helper.enums.Stone;

import static helper.CommandToolbox.*;

/**
 * Abstract class describing the main functions of a client.strategy. The real client.strategy is implemented in a subclass
 *
 * @author Mark Banierink
 */
public abstract class Strategy implements Runnable {

    protected Game game;
    protected Stone stone;
    private Client client;

    public Strategy(Game game, Stone stone, Client client) {
        this.game = game;
        this.stone = stone;
        this.client = client;
    }

    public void run() {
        String command = determineMove();
        handleMove(command);
    }

    private void handleMove(String command) {
        client.handleServerInput(command);
    }

    public String determineMove() {
        int[] xy = createMove();
        if (xy[0] == -1) {
            return createCommandPass();
        }
        else {
            return createCommandMove(xy[0], xy[1]);
        }
    }

    protected abstract int[] createMove();
}
