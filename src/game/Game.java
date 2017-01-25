package game;

import com.nedap.go.gui.GoGUIIntegrator;
import helper.Stone;

import java.util.ArrayList;
import java.util.List;

import static helper.Keyword.*;
import static helper.Stone.*;
import static helper.Strings.*;

/**
 * Created by mark.banierink on 16-1-2017.
 * The Game class provides the game itself. It manages the players, the board and the rules.
 */
public class Game {

    private List<Player> players = new ArrayList<>();
    private Board board;
    private Stone turn = BLACK;
    private int turnCounter = 1;
    private int passCounter = 0;
    private List<Board> boardHistory = new ArrayList<>();
    private GoGUIIntegrator goGui;

    private int movesPerTurn = 1;

    /**
     * Constructor of the game. Calls copyBoard(getBoard()).
     * @param boardSize the size of the board on which the game is to be played
     */
    public Game(int boardSize) {
        this.board = new Board(boardSize);
        copyBoard(getBoard());
    }

    /**
     * Starts the game by assigning stones to players (assignStones()) and starting the GUI
     * 2 players must have been added to the players List
     */
    public void startGame() {
        assignStones();
        goGui = new GoGUIIntegrator(true, true, getBoard().getBoardSize());
        getGui().startGUI();
    }

    /**
     * Gives the opponent of the given player
     * @param player the player of which the opponent is to be found
     * @return List containing Player objects that are the opponents of the given player
     */
    public List<Player> getOpponents(Player player) {
        List<Player> opponents = new ArrayList<>();
        for (Player listedPlayer : getPlayers()) {
            if (!listedPlayer.equals(player)) {
                opponents.add(listedPlayer);
            }
        }
        return opponents;
    }

    private int getMovesPerTurn() {
        return this.movesPerTurn;
    }

    private void setMovesPerTurn(int movesPerTurn) {
        this.movesPerTurn =  movesPerTurn;
    }

    public Board getBoard() {
        return this.board;
    }

    private List<Board> getBoardHistory() {
        return this.boardHistory;
    }

    private void setBoardHistory(Board board) {
        getBoardHistory().add(board);
    }

    private int getTurnNumber() {
        return this.turnCounter;
    }

    private Stone getTurn() {
        return this.turn;
    }

    /**
     * returns the list of Players
     * @return List with Player objects
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Determines the maximum amount of players, based on the amount of available stones
     * @return int with the max amount of players
     */
    public int maxPlayers() {
        return Stone.values().length - 1;
    }

    private int numPlayers() {
        return getPlayers().size();
    }

    private GoGUIIntegrator getGui() {
        return this.goGui;
    }

    private boolean isTurn(Stone stone) {
        return stone.equals(getTurn());
    }

    private static boolean stone2bool(Stone stone) {
        return stone.equals(WHITE);
    }

    /**
     * Adds a player to the list of players
     * @param player Player object to add
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    /**
     * Removes a player from the players list
     * @param player The Player that is removed
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    /**
     * Returns the player, based on the assigned Stone
     * @param stone The Stone to find the Player of
     * @return Player object to which the Stone is assigned, null if none was found
     */
    public Player getPlayerByStone(Stone stone) {
        for (Player listedPlayer : getPlayers()) {
            if (listedPlayer.getStone() == stone) {
                return listedPlayer;
            }
        }
        return null;
    }

    private void nextTurn() {
        copyBoard(getBoard());
        if (getTurnNumber() % movesPerTurn == 0) {
            this.turn = getTurn().nextStone(numPlayers());
        }
        turnCounter++;
    }

    private void assignStones() {
        for (Player player : getPlayers()) {
            Stone stone = randomStone(numPlayers());
            while (getPlayerByStone(stone) != null) {           // possible perpetual loop, needs improvement
                stone = randomStone(numPlayers());
            }
            player.setStone(stone);
        }
    }

    private void placeStone(int x, int y, Stone stone) {
        getBoard().setField(x, y, stone);
        getGui().addStone(x, y, stone2bool(stone));
    }

    /**
     * Pass this turn
     * @return String with the end score if the game is over after this turn, empty string if not
     */
    public String pass() {
        if (getTurn() == Stone.values()[1]) {
            resetPassCounter();
        }
        increasePassCounter();
        if (isFinished()) {
            return END + scoreString();
        }
        nextTurn();
        return "";
    }

    /**
     *
     */
    public void tableflip() {
                                                                // wat moet er nu gebeuren?
    }

    private String scoreString() {
        String scoreString = "";
        for (Player player : getPlayers()) {
            scoreString += SPACE.toString() + player.getStone() + SPACE + getScore(player.getStone());
        }
        return scoreString;
    }

    private boolean isFinished() {
        return getPassCounter() == numPlayers();
    }

    private void increasePassCounter() {
        this.passCounter++;
    }

    private void resetPassCounter() {
        this.passCounter = 0;
    }

    private int getPassCounter() {
        return this.passCounter;
    }

    private int getScore(Stone stone) {
        return 0;
    }

    /**
     * Places a Stone on the board and the GUI and changes the turn
     * @param stone The Stone to be set
     * @param x integer of the x coordinate of the position the Stone is placed
     * @param y integer of the y coordinate of the position the Stone is placed
     */
    public void move(Stone stone, int x, int y) {
        placeStone(x, y, stone);
        resetPassCounter();
        nextTurn();
        getGui().addStone(x, y, stone2bool(stone));
    }

    /**
     * Checks if a Pass command is valid
     * @param stone Stone of the player requesting
     * @return true if the Stone may Pass, false if it may not
     */
    public boolean isValidPass(Stone stone) {
        return isTurn(stone);
    }

    /**
     * Checks if a Tableflip command is valid
     * @param stone Stone of the player requesting
     * @return true if the Stone may Tableflip, false if it may not
     */
    public boolean isValidTableflip(Stone stone) {
        return isTurn(stone);
    }

    /**
     * Checks the validity of a Move command
     * @param stone The Stone of the requesting Player
     * @param x integer of the x coordinate the Stone is to be placed
     * @param y integer of the y coordinate the Stone is to be placed
     * @return String VALID if the move is allowed or a String with the reason why it isn't allowed
     */
    public String checkMoveValidity(Stone stone, int x, int y) {
        String result = VALID.toString();
        if (!isTurn(stone)) {                                                               // Check if it is this player's turn
            result = NOT_TURN.toString();
        } else if (!getBoard().isField(x, y)) {                                             // Check if the position exists
            result = NOT_FIELD.toString();
        } else if (!getBoard().isEmpty(x, y)) {                                             // Check if the desired position is free
            result = NOT_FREE_FIELD.toString();
        } else if (boardExists(getBoard().boardCopy().setField(x, y, stone))){  // Check if it doesn't match a previous situation
            result = KO.toString();
        }
        return result;
    }

    private void copyBoard(Board board) {
        setBoardHistory(board.boardCopy());
    }

    private boolean boardExists(Board board) {
        for (Board historicBoard : getBoardHistory()) {
            if (board.equals(historicBoard)) {
                return true;
            }
        }
        return false;
    }

}
