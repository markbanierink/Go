package client.strategy.strategies;

import client.Client;
import game.Game;
import helper.enums.Stone;
import java.util.Random;
import client.strategy.Strategy;

/**
 * This class represents the random strategy, meaning the next move is randomly determined
 *
 * @author Mark Banierink
 */
public class RandomStrategy extends Strategy {

    private int maxTries;
    private long maxTime;

    public RandomStrategy(Game game, Stone stone, int maxCalculationTime, Client client) {
        super(game, stone, maxCalculationTime, client);
        maxTime = System.currentTimeMillis() + maxCalculationTime;
        maxTries = game.getBoard().getBoardSize();
    }

    protected int[] createMove() {
        int boardSize = (int)Math.pow(game.getBoard().getBoardSize(), 2);
        int[] move = new int[]{-1, -1};
        int tryCounter = 1;
        while (move[0] == -1 && tryCounter < maxTries && System.currentTimeMillis() < maxTime) {
            int[] xy = game.indexToXY((new Random()).nextInt(boardSize) + 1);
            if (game.isValidMove(stone, xy[0], xy[1])) {
                move = xy;
            }
            tryCounter++;
        }
        return move;
    }
}
