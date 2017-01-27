package game;

import com.nedap.go.gui.GoGUIIntegrator;
import helper.enums.Stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static helper.enums.Keyword.*;
import static helper.enums.Stone.*;
import static helper.enums.Strings.*;

/**
 * The Game class provides the game itself. It manages the players, the board and the rules.
 * @author Mark Banierink
 */
public class Game {

    private List<Player> players = new ArrayList<>();
    private Board board;
    private Stone turn = BLACK;
    private int turnCounter = 1;
    private int passCounter = 0;
    private List<Board> boardHistory = new ArrayList<>();
    private static GoGUIIntegrator goGui;
    private int movesPerTurn = 1;
    private HashMap<Stone, HashSet<Integer>> chains = new HashMap<>();

    /**
     * Constructor of the game. Calls copyBoard(getBoard()).
     * @param boardSize the size of the board on which the game is to be played
     */
    public Game(int boardSize, int movesPerTurn) {
        this.board = new Board(boardSize);
        this.movesPerTurn = movesPerTurn;
        startGUI();
        copyBoard(getBoard());
    }

    private void startGUI() {
        if (!guiIsAvailable()) {
            goGui = new GoGUIIntegrator(true, true, getBoard().getBoardSize());
            getGui().startGUI();
        }
    }

    private boolean guiIsAvailable() {
        return getGui() != null;
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

    private int coordinateToIndex(int x, int y) {
        return (y + 1) * getBoard().getBoardSize() + x;
    }

    private int[] indexToXY(int index) {
        int x = index % getBoard().getBoardSize() - 1;
        int y = (int)Math.floor(index / getBoard().getBoardSize()) - 1;
        return new int[] {x, y};
    }

    private HashMap<Stone, HashSet<Integer>> getChains() {
        return this.chains;
    }

    private void addNewChain(Stone stone, HashSet<Integer> chain) {
        getChains().put(stone, chain);
    }

    private boolean hasFreedom(int index) {
        return degreesOfFreedom(index) > 0;
    }

    private int degreesOfFreedom(int index) {
        HashSet<Integer> coordinates = surroundings(index);
        int degrees = 0;
        for (Integer coordinate : coordinates) {
            int[] xy = indexToXY(coordinate);
            if (getBoard().isEmpty(xy[0], xy[1])) {
                degrees++;
            }
        }
        return degrees;
    }

    private Stone getStone(int index) {
        return getBoard().getField(indexToXY(index)[0], indexToXY(index)[1]);
    }

    private HashSet<Integer> increaseChain(int index, HashSet<Integer> chain) {
        for (Integer coordinate : surroundings(index)) {
            if (getStone(coordinate) != null && getStone(coordinate).equals(getStone(index)) && !chain.contains(coordinate)) {
                chain.add(coordinate);
                chain = increaseChain(coordinate, chain);
            }
        }
        return chain;
    }

    private void findChains() {
        int boardSize = getBoard().getBoardSize()*getBoard().getBoardSize();
        for (int i = 1; i < boardSize; i++) {
            HashSet<Integer> chain = increaseChain(i, new HashSet<>());
            if (chain.size() > 0) {
                addNewChain(getStone(i), chain);
            }
        }
    }

    private HashSet<Integer> surroundings(int index) {
        int boardSize = getBoard().getBoardSize();
        int[] pos = indexToXY(index);
        int[] coordinates = {pos[0] + 1, pos[1], pos[0] - 1, pos[1], pos[0], pos[1] + 1, pos[0], pos[1] - 1};
        HashSet<Integer> indices = new HashSet<>();
        for (int i = 0; i < coordinates.length; i++) {
            if (coordinates[i*2] >= 0 && coordinates[i*2] < boardSize && coordinates[i*2+1] >= 0 && coordinates[i*2+1] < boardSize) {
                indices.add(coordinateToIndex(coordinates[i*2], coordinates[i*2+1]));
            }
        }
        return indices;
    }

    private void captureFields(HashSet<Integer> chain) {
        for (Integer index : chain) {
            captureField(index);
        }
    }

    private void captureField(int index) {
        removeStone(indexToXY(index)[0], indexToXY(index)[1]);
    }

    private boolean hasDegreesOfFreedom(HashSet<Integer> chain) {
        return chainDegreesOfFreedom(chain) > 0;
    }

    private int chainDegreesOfFreedom(HashSet<Integer> chain) {
        int degrees = 0;
        for (Integer index : chain) {
            if (hasFreedom(index)) {
                degrees++;
            }
        }
        return degrees;
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
     * Adds a player to the list of players and assigns a free Stone to it
     * @param player to add
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        player.setStone(getFreeStone());
    }

    private Stone getFreeStone() {
        Stone stone = randomStone(numPlayers());
        while (getPlayerByStone(stone) != null) {
            stone = stone.nextStone(numPlayers());
        }
        return stone;
    }

    /**
     * Removes a player from the players list
     * @param player that is removed
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    /**
     * Returns the player, based on the assigned Stone
     * @param stone to find the Player of
     * @return Player to which the Stone is assigned, null if none was found
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
        if (getTurnNumber() % getMovesPerTurn() == 0) {
            this.turn = getTurn().nextStone(numPlayers());
        }
        turnCounter++;
    }

    private void placeStone(int x, int y, Stone stone) {
        getBoard().setField(x, y, stone);

    }

    private void removeStone(int x, int y) {
        getBoard().setFieldEmpty(x, y);
        if (guiIsAvailable()) {
            getGui().removeStone(x, y);
        }
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
    public void tableFlip() {
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
        System.out.println(passCounter);
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
     * @param stone of the Player that is moving
     * @param x integer of the x coordinate of the position the Stone is placed
     * @param y integer of the y coordinate of the position the Stone is placed
     */
    public void move(Stone stone, int x, int y) {
        placeStone(x, y, stone);
        resetPassCounter();
        nextTurn();
        if (guiIsAvailable()) {
            getGui().addStone(x, y, stone2bool(stone));
        }
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
        return true;
        //return isTurn(stone);                                                     // TABLEFLIP is always allowed
    }

    public boolean isValidMove(Stone stone, int x, int y) {
        return checkMoveValidity(stone, x, y).equals(VALID.toString());
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
        if (!isTurn(stone)) {                                                       // Check if it is this player's turn
            result = NOT_TURN.toString();
        }
        else if (!getBoard().isField(x, y)) {                                       // Check if the position exists
            result = NOT_FIELD.toString();
        }
        else if (!getBoard().isEmpty(x, y)) {                                       // Check if the desired position is free
            result = NOT_FREE_FIELD.toString();
        }
        else if (boardExists(getBoard().boardCopy().setField(x, y, stone))) {       // Check if it doesn't match a previous situation
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
