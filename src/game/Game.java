package game;

import com.nedap.go.gui.GoGUIIntegrator;
import helper.enums.Keyword;
import helper.enums.Stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    private static final int EARLY_ENDING_SCORE = -1;

    private List<Player> players = new ArrayList<>();
    private Board board;
    private Stone turn = BLACK;
    private int turnCounter = 1;
    private int passCounter = 0;
    private List<Board> boardHistory = new ArrayList<>();
    private static GoGUIIntegrator goGui;
    private int movesPerTurn = 1;
    private int playersPerGame = 2;
    private Map<Stone, List<List<Integer>>> territories = new HashMap<>();

    /**
     * Constructor of the game. Calls copyBoard(getBoard()).
     * @param boardSize the size of the board on which the game is to be played
     */
    public Game(int boardSize, int movesPerTurn, int playersPerGame) {
        this.board = new Board(boardSize);
        this.movesPerTurn = movesPerTurn;
        this.playersPerGame = playersPerGame;
        for (int i = 1; i <= getPlayersPerGame(); i++) {
            getTerritories().put(Stone.values()[i], new ArrayList<>());
        }
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

    /**
     * Returns the turn
     * @return the Stone of the turn
     */
    public Stone getTurn() {
        return this.turn;
    }

    private int XYToIndex(int x, int y) {
        return (y * getBoard().getBoardSize() + 1) + x;
    }

    private int[] indexToXY(int index) {
        int x = index % getBoard().getBoardSize() - 1;
        int y = (int)Math.floor((index - 1) / getBoard().getBoardSize());
        return new int[] {x, y};
    }

    private Map<Stone, List<List<Integer>>> getTerritories() {
        return this.territories;
    }

    private boolean hasChains(Stone stone) {
        return getChains(stone) != null;
    }

    private List<List<Integer>> getChains(Stone stone) {
        return getTerritories().get(stone);
    }

    private boolean hasFreedom(int index) {
        return degreesOfFreedom(index) > 0;
    }

    private int degreesOfFreedom(int index) {
        Set<Integer> coordinates = neighbours(index);
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

    private Set<Integer> neighbours(int index) {
        int boardSize = getBoard().getBoardSize();
        int[] pos = indexToXY(index);
        int[] coordinates = {pos[0] + 1, pos[1], pos[0] - 1, pos[1], pos[0], pos[1] + 1, pos[0], pos[1] - 1};
        Set<Integer> indices = new HashSet<>();
        for (int i = 0; i < coordinates.length; i += 2) {
            if (coordinates[i] >= 0 && coordinates[i] < boardSize && coordinates[i + 1] >= 0 && coordinates[i + 1] < boardSize) {
                indices.add(XYToIndex(coordinates[i], coordinates[i + 1]));
            }
        }
        return indices;
    }

    private List<Integer> newChain(int index) {
        List<Integer> chain = new ArrayList<>();
        chain.add(index);
        return chain;
    }

    private void addNewChain(Stone stone, List<Integer> chain) {
        getChains(stone).add(chain);
    }

    private void mergeChains(Stone stone, int index) {
        List<Integer> newChain = newChain(index);
        addNewChain(stone, newChain);
        for (int neighbourIndex : neighbours(index)) {
            Iterator<List<Integer>> iterator = getChains(stone).iterator();
            while (iterator.hasNext()) {
                List<Integer> chain = iterator.next();
                if (chain.contains(neighbourIndex) && chain != newChain) {
                    if (newChain.addAll(chain)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void updateGame(Stone stone, int x, int y) {
        mergeChains(stone, XYToIndex(x, y));
        updateOpponents(stone);
    }

    private void updateOpponents(Stone stone) {
        for (Stone opponentsStone : getTerritories().keySet()) {
            if (!opponentsStone.equals(stone) && hasChains(stone)) {
                Iterator<List<Integer>> iterator = getChains(opponentsStone).iterator();
                while (iterator.hasNext()) {
                    List<Integer> chain = iterator.next();
                    if (!hasDegreesOfFreedom(chain)) {
                        iterator.remove();
                        captureChain(chain);
                    }
                }
            }
        }
    }

    private void captureChain(List<Integer> chain) {
        for (Integer index : chain) {
            removeStone(indexToXY(index)[0], indexToXY(index)[1]);
        }
    }

    private boolean hasDegreesOfFreedom(List<Integer> chain) {
        return chainDegreesOfFreedom(chain) > 0;
    }

    private int chainDegreesOfFreedom(List<Integer> chain) {
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
    private int getPlayersPerGame() {
        return this.playersPerGame;
    }

    private int numPlayers() {
        return getPlayers().size();
    }

    private GoGUIIntegrator getGui() {
        return goGui;
    }

    private boolean isTurn(Stone stone) {
        return stone.equals(getTurn());
    }

    private static boolean stone2bool(Stone stone) {
        return stone.equals(WHITE);
    }

    /**
     * Adds a player to the list of players and assigns a given Stone to it
     * @param player to add
     * @param stone to assign to the player
     */
    public void addPlayer(Player player, Stone stone) {
        player.setStone(stone);
        this.players.add(player);
    }

    /**
     * Adds a player to the list of players and assigns a random free Stone to it
     * @param player to add
     */
    public void addPlayer(Player player) {
        addPlayer(player, getFreeStone());
    }

    private Stone getFreeStone() {
        Stone stone = randomStone(getPlayersPerGame());
        while (getPlayerByStone(stone) != null) {
            stone = stone.nextStone(getPlayersPerGame());
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
            if (listedPlayer.getStone().equals(stone)) {
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
            return scoreString(null);
        }
        nextTurn();
        return "";
    }

    /**
     * Returns the END command string with the TABLEFLIP-score
     * @return END string with the TABLEFLIP-score
     */
    public String tableFlip() {
        return scoreString(TABLEFLIPPED);
    }

    private String scoreString(Keyword keyword) {
        String scoreString = END.toString();
        for (Player player : getPlayers()) {
            if (keyword.equals(TABLEFLIPPED)) {
                scoreString += SPACE.toString() + EARLY_ENDING_SCORE;
            }
            else {
                scoreString += SPACE.toString() + getScore(player.getStone());
            }
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
        int score = 0;
        score += getStoneScore(stone);
        score += getTerritoryScore(stone);
        return score;
    }

    private int getStoneScore(Stone stone) {
        int stones = 0;
        for (List<Integer> chains : getChains(stone)) {
            stones += chains.size();
        }
        return stones;
    }

    private int getTerritoryScore(Stone stone) {
        int boardIndexSize = getBoard().getBoardSize() * getBoard().getBoardSize();
        List<Integer> allChainIndices = new ArrayList<>();
        int score = 0;
        for (int i = 1; i < boardIndexSize; i++) {
            if (!allChainIndices.contains(i)) {
                List<Integer> chain = increaseChain(i, new ArrayList<>(), stone);
                if (chain != null && chain.size() > 0) {
                    score += chain.size();
                    allChainIndices.addAll(chain);
                }
            }
        }
        return score;
    }

    private List<Integer> increaseChain(int index, List<Integer> chain, Stone stone) {
        for (Integer coordinate : neighbours(index)) {
            if (!getStone(coordinate).equals(EMPTY) && !getStone(coordinate).equals(stone)) {
                return null;
            }
            else if (getStone(coordinate).equals(EMPTY) && !chain.contains(coordinate)) {
                chain.add(coordinate);
                chain = increaseChain(coordinate, chain, stone);
                if (chain == null) {
                    return null;
                }
            }
        }
        return chain;
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
        if (guiIsAvailable()) {
            getGui().addStone(x, y, stone2bool(stone));
        }
        updateGame(stone, x, y);
        nextTurn();
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
