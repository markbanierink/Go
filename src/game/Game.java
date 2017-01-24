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

    private List<Player> gamePlayers = new ArrayList<>();
    private Board board;
    private Stone turn = BLACK;
    private int turnCounter = 1;
    private int movesPerTurn = 1;
    private int passCounter = 0;
    private List<Board> boardHistory = new ArrayList<>();
    private GoGUIIntegrator goGui;

    /**
     * Constructor of the game
     * @param boardsize the size of the board on which the game is to be played
     */
    public Game(int boardsize) {
        this.board = new Board(boardsize);
        copyBoard(getBoard());
    }

    /**
     * Starts the game by assigning stones to players and starting the GUI
     * 2 players must have been added to the gamePlayers List
     */
    public void startGame() {
        assignStones();
        goGui = new GoGUIIntegrator(true, true, getBoard().getBoardsize());
        getGui().startGUI();
    }

    /**
     * Gives the opponent of the given player
     * @param player the player of which the opponent is to be found
     * @return List containing Player objects that are the opponents of the given player
     */
    public List<Player> getOpponents(Player player) {
        List<Player> opponents = new ArrayList<>();
        for (Player listedPlayer : getGamePlayers()) {
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
    public List<Player> getGamePlayers() {
        return this.gamePlayers;
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
        this.gamePlayers.add(player);
    }

    /**
     * Removes a player from the players list
     * @param player The Player that is removed
     */
    public void removePlayer(Player player) {
        this.gamePlayers.remove(player);
    }

    /**
     * Returns the player, based on the assigned Stone
     * @param stone The Stone to find the Player of
     * @return Player object to which the Stone is assigned, null if none was found
     */
    public Player getPlayerByStone(Stone stone) {
        for (Player listedPlayer : getGamePlayers()) {
            if (listedPlayer.getStone() == stone) {
                return listedPlayer;
            }
        }
        return null;
    }

    private void nextTurn() {
        if (getTurnNumber() % movesPerTurn == 0) {
            this.turn = getTurn().other();
        }
        turnCounter++;
    }

    private void assignStones() {
        Stone stone = randomStone();
        gamePlayers.get(0).setStone(stone);
        gamePlayers.get(1).setStone(stone.other());
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
        if (getTurn() == BLACK) {
            increasePassCounter();
            nextTurn();
        } else if (passCounter == 1){
            increasePassCounter();
        }
        if (isFinished()) {
            return END.toString() + SPACE + getScore(BLACK) + SPACE + getScore(WHITE);
        }
        return "";
    }

    /**
     *
     */
    public void tableflip() {
                                                                // wat moet er nu gebeuren?
    }

    private boolean isFinished() {
        return getPassCounter() == 2;
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
        copyBoard(getBoard());
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
            if (board.boardToString().equals(historicBoard.boardToString())) {
                return true;
            }
        }
        return false;
    }

}
