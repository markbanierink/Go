package strategy.strategies;

import client.Client;
import game.Game;
import helper.enums.Stone;
import strategy.Strategy;

/**
 * This class represents the strategy that is used to play the game
 *
 * @author Mark Banierink
 */
public class SmartStrategy extends Strategy {

    private long maxTime;

    public SmartStrategy(Game game, Stone stone, int maxCalculationTime, Client client) {
        super(game, stone, client);
    }

    @Override
    protected int[] createMove() {
        int boardSize = game.getBoard().getBoardSize();
        int border = boardSize + boardSize / 6;
        return null;
    }
}
