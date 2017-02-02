package strategy.strategies;

import client.Client;
import game.Game;
import helper.enums.Stone;
import java.util.Random;
import strategy.Strategy;

/**
 * This class represents the Random Strategy, meaning the next move is randomly determined
 *
 * @author Mark Banierink
 */
public class RandomStrategy extends Strategy {

    private static final int MAX_TRIES = 50;
    private long maxTime;

    public RandomStrategy(Game game, Stone stone, int maxCalculationTime, Client client) {
        super(game, stone, client);
        maxTime = System.currentTimeMillis() + maxCalculationTime;
    }

    protected int[] createMove() {
        int boardSize = game.getBoard().getBoardSize()*game.getBoard().getBoardSize();
        boolean validMove = false;
        int[] xy = new int[2];
        int tryCounter = 1;
        while (!validMove && tryCounter < MAX_TRIES && System.currentTimeMillis() < maxTime) {
            int randomValue = (new Random()).nextInt(boardSize) + 1;
            xy = game.indexToXY(randomValue);
            validMove = game.isValidMove(stone, xy[0], xy[1]);
            tryCounter++;
        }
        if (!validMove) {
           xy = new int[]{-1, -1};
        }
        try {
            if (maxTime - System.currentTimeMillis() > 0) {
                Thread.sleep(maxTime - System.currentTimeMillis());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return xy;
    }
}
