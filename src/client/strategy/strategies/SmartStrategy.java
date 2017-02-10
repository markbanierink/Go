package client.strategy.strategies;

import client.Client;
import game.Game;
import game.Player;
import helper.enums.Stone;
import client.strategy.Strategy;
import java.util.Random;

/**
 * This class represents the smart strategy that is used to play the game
 * @author Mark Banierink
 */
public class SmartStrategy extends Strategy {

    private static final int RIDGE_RATIO = 5;

    private Game game;
    private Stone stone;
    private int maxCalculationTime;
    private Client client;
    private long maxTime;
    private int maxTries;

    public SmartStrategy(Game game, Stone stone, int maxCalculationTime, Client client) {
        super(game, stone, maxCalculationTime, client);
        this.game = game;
        this.stone = stone;
        this.maxCalculationTime = maxCalculationTime;
        this.client = client;
        maxTime = System.currentTimeMillis() + maxCalculationTime;
        maxTries = game.getBoard().getBoardSize();
    }

    @Override
    protected int[] createMove() {
        int[] move;
        if (offensiveIsEffective()) {
            move = offensiveMove();
        }
        else if (defensiveIsEffective()) {
            move = defensiveMove();
        }
        else {
            move = randomMove();
        }
        return move;
    }

    private int[] randomMove() {
        return (new RandomStrategy(game, stone, maxCalculationTime, client).createMove());
    }

    private boolean offensiveIsEffective() {
        return offensiveMove()[0] != -1;
    }

    private int[] offensiveMove() {
        int[] move = new int[]{-1, -1};
        int field = 1;
        int boardSize = (int)Math.pow(game.getBoard().getBoardSize(), 2);
        while (field <= boardSize && System.currentTimeMillis() < maxTime) {
            int[] xy = game.indexToXY(field);
            if (game.isValidMove(stone, xy[0], xy[1])) {
                Game futureGame = game.copyThisGame();
                futureGame.move(stone, xy[0], xy[1]);
                if (relativeScore(futureGame) > relativeScore(game) + 1) {
                    move = xy;
                }
            }
            field++;
        }
        return move;
    }

    private boolean defensiveIsEffective() {
        return defensiveMove()[0] != -1;
    }

    private int[] defensiveMove() {
        int[] move = new int[]{-1, -1};
        int tryCounter = 0;
        while (tryCounter < maxTries && System.currentTimeMillis() < maxTime) {
            int x;
            int y;
            if ((new Random()).nextBoolean()) {
                x = getCornerPosition();
                y = getLinedPosition();
            }
            else {
                x = getLinedPosition();
                y = getCornerPosition();
            }
            if (game.isValidMove(stone, x, y)) {
                return new int[]{x, y};
            }
            tryCounter++;
        }
        return move;
    }

    private int getLinedPosition() {
        int boardSize = game.getBoard().getBoardSize();
        return (new Random()).nextInt(boardSize);
    }

    private int getCornerPosition() {
        int position;
        int boardSize = game.getBoard().getBoardSize();
        int ridge = Math.round(boardSize / RIDGE_RATIO);
        int distance = (int) Math.round((new Random()).nextGaussian() * ridge);
        if ((new Random()).nextBoolean()) {
            position = boardSize - ridge + distance;
        }
        else {
            position = ridge + distance;
        }
        return position;
    }

    private int relativeScore(Game futureGame) {
        int totalScore = 0;
        for (Player player : futureGame.getPlayers()) {
            if (player != futureGame.getPlayerByStone(stone)) {
                totalScore += futureGame.getScore(player.getStone());
            }
        }
        return futureGame.getScore(stone) - totalScore;
    }
}

