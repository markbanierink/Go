package game;

import com.nedap.go.gui.GoGUIIntegrator;
import helper.Stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static helper.Stone.*;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Game {

    private int gameNumber;
    private List<Player> players = new ArrayList<>();
    private Board board;
    private Stone turn = BLACK;
    private int turnCounter = 1;
    private int movesPerTurn = 1;
    private HashMap<Integer, String> boardHistory = new HashMap<>();
    private GoGUIIntegrator goGui;

    public Game(int boardsize, int gameNumber) {
        this.board = new Board(boardsize);
        this.gameNumber = gameNumber;
    }

    public int getGameNumber() {
        return this.gameNumber;
    }

    public void startGame() {
        assignStone();
        goGui = new GoGUIIntegrator(true, true, getBoard().getSize());
        getGui().startGUI();
    }

    public int getMovesPerTurn() {
        return this.movesPerTurn;
    }

    public void setMovesPerTurn(int movesPerTurn) {
        this.movesPerTurn =  movesPerTurn;
    }

    public Board getBoard() {
        return this.board;
    }

    private HashMap<Integer, String> getBoardHistory() {
        return this.boardHistory;
    }

    private int getTurnNumber() {
        return this.turnCounter;
    }

    private Stone getTurn() {
        return this.turn;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    private GoGUIIntegrator getGui() {
        return this.goGui;
    }

    private boolean isTurn(Player player) {
        return player.getStone().equals(getTurn());
    }

    private static boolean stone2bool(Stone stone) {
        return stone.equals(WHITE);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setGame(this);
    }

    public void removePlayer(Player player) {
        player.setGame(null);
        this.players.remove(player);
    }

    private void nextTurn() {
        if (getTurnNumber() % movesPerTurn == 0) {
            this.turn = getTurn().other();
        }
        turnCounter++;
    }

    private void assignStone() {
        Stone stone = randomStone();
        players.get(0).setStone(stone);
        players.get(1).setStone(stone.other());
    }

    private void placeStone(int x, int y, Stone stone) {
        getBoard().setField(x, y, stone);
        getGui().addStone(x, y, stone2bool(stone));
    }

    public void move(Player player, int x, int y) {
        if (isValidMove(player, x, y)) {
            placeStone(x, y, player.getStone());
            copyBoard();
            nextTurn();
            getGui().addStone(x, y, stone2bool(player.getStone()));
        }
    }

    public boolean isValidPass(Player player) {
        return isTurn(player);
    }

    public boolean isValidTableflip(Player player) {
        return isTurn(player);
    }

    public boolean isValidMove(Player player, int x, int y) {
        boolean result = true;
        if (!isTurn(player)) {              // Check if it is this player's turn
            result = false;
        }
        if (!getBoard().isField(x, y)) {    // Check if the position exists
            result = false;
        }
        if (!getBoard().isEmpty(x, y)) {    // Check if the desired position is free
            result = false;
        }
        if (boardExists()){                 // Check if it doesn't match a previous situation
            result = false;
        }
        return result;
    }

    private void copyBoard() {
        getBoardHistory().put(getTurnNumber(), getBoard().board2string());          // aanpassen naar dynamisch bord
    }

    private boolean boardExists() {
        for (HashMap.Entry<Integer, String> historicBoard : getBoardHistory().entrySet()) {
            if (getBoard().board2string().equals(historicBoard.getValue())) {
                return true;
            }
        }
        return false;
    }

}
