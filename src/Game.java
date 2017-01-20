import com.nedap.go.gui.GoGUIIntegrator;
import helper.Stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
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
    private HashMap<Integer, String> boardHistory = new HashMap<>();
    private GoGUIIntegrator goGui;

    Game(int boardsize, int gameNumber) {
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

    public int getPlayerIndex(Player player) {
        return getPlayers().indexOf(player);
    }

    private GoGUIIntegrator getGui() {
        return this.goGui;
    }

    private boolean isSuicide() {                                   // UITWERKEN
        return false;
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
        this.turn = this.turn.other();
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

    private void removeStone(int x, int y) {
        getBoard().emptyField(x, y);
        getGui().removeStone(x, y);
    }

    public void handlePlayerInput(Player player, String string) {
        // bring player input to server?
    }

    public void handlePlayerOutput(Player player, String string) {
        // bring server output to player?
    }

    public static void handleClientCommand(String string) {
        String[] command = splitString(string);
    }

    private void commandMove(Player player, int x, int y) {

    }

    private void move(Player player, int x, int y) {
        if (isValidMove(player, x, y)) {
            getBoard().setField(x, y, player.getStone());
            copyBoard();
            nextTurn();
            getGui().addStone(x, y, stone2bool(player.getStone()));
        }
    }

    private boolean isValidMove(Player player, int x, int y) {
        boolean result = true;
        if (!isTurn(player)) {              // Check if it is this player's turn
            result = false;
        }
        if (!getBoard().isField(x, y)) {
            result = false;
        }
        if (!getBoard().isEmpty(x, y)) {    // Check if the desired position is free
            result = false;
        }
        if (boardExists()){                 // Check if it doesn't match a previous situation
            result = false;
        }
        if (isSuicide()) {                  // Check if it isn't a suicide move
            result = false;
        }
        // Etc.
        return result;
    }

    private void copyBoard() {
        getBoardHistory().put(getTurnNumber(), getBoard().board2string());
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
