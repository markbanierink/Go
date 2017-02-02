package game;

import com.nedap.go.gui.GoGUIIntegrator;
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
import static helper.enums.Resources.*;

/**
 * The Game class provides the game itself. It manages the players, the board and the rules.
 * @author Mark Banierink
 */
public class Game {

    private static final int EARLY_ENDING_SCORE = -1;

    public List<Player> players = new ArrayList<>();                            // public for testing
    public Board board;                                                         // public for testing
    public Stone turn = BLACK;                                                  // public for testing
    public int turnCounter = 1;                                                 // public for testing
    public int passCounter = 0;                                                 // public for testing
    public List<Board> boardHistory = new ArrayList<>();                        // public for testing
    public static GoGUIIntegrator goGui;                                        // public for testing
    public int movesPerTurn = 1;                                                // public for testing
    public int playersPerGame = 2;                                              // public for testing
    public Map<Stone, List<List<Integer>>> territories = new HashMap<>();       // public for testing
    private boolean future;

    /**
     * Constructor of the game. Calls storeBoard() immediately to set a blank board in the board history
     * @param boardSize the size of the board on which the game is to be played
     */
    public Game(int boardSize, int movesPerTurn, int playersPerGame, boolean future) {
        board = new Board(boardSize);
        this.future = future;
        this.movesPerTurn = movesPerTurn;
        this.playersPerGame = playersPerGame;
        for (int i = 1; i <= getPlayersPerGame(); i++) {
            territories.put(Stone.values()[i], new ArrayList<>());
        }
        startGUI();
        storeBoard(board);
    }

    private void startGUI() {
        if (!guiIsAvailable()) {
            goGui = new GoGUIIntegrator(false, true, board.getBoardSize());
            goGui.startGUI();
        }
    }

    private boolean guiIsAvailable() {
        return goGui != null;
    }

    public Board getBoard() {
        return board;
    }

    private void setBoardHistory(Board board) {
        boardHistory.add(board);
    }

    /**
     * Returns the turn
     * @return the Stone of the turn
     */
    public Stone getTurn() {
        return turn;
    }

    private int XYToIndex(int x, int y) {
        return (y * board.getBoardSize() + 1) + x;
    }

    /**
     * Translates an index number into a board coordinate
     * @param index is an int between 1 and the number of board positions (boardSize*boardSize) (both inclusive)
     * @return integer array containing respectively x and y
     */
    public int[] indexToXY(int index) {
        int x = (index - 1) % board.getBoardSize();
        int y = (int)Math.floor((index - 1) / board.getBoardSize());
        return new int[] {x, y};
    }

    private boolean hasChains(Stone stone) {
        return getChains(stone) != null;
    }

    private List<List<Integer>> getChains(Stone stone) {
        return territories.get(stone);
    }

    private boolean hasFreedom(int index) {
        return degreesOfFreedom(index) > 0;
    }

    private int degreesOfFreedom(int index) {
        Set<Integer> coordinates = neighbours(index);
        int degrees = 0;
        for (Integer coordinate : coordinates) {
            int[] xy = indexToXY(coordinate);
            if (board.isEmpty(xy[0], xy[1])) {
                degrees++;
            }
        }
        return degrees;
    }

    private Stone getStone(int index) {
        return board.getField(indexToXY(index)[0], indexToXY(index)[1]);
    }

    private Set<Integer> neighbours(int index) {
        int boardSize = board.getBoardSize();
        int[] pos = indexToXY(index);
        int[] coordinates = {pos[0], pos[1] - 1, pos[0] + 1, pos[1], pos[0], pos[1] + 1, pos[0] - 1, pos[1]};
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
        for (Stone listedStone : territories.keySet()) {
            if (!listedStone.equals(stone)) {
                updatePositions(listedStone);
            }
        }
        updatePositions(stone);
    }

    private void updatePositions(Stone stone) {
        if (hasChains(stone)) {
            Iterator<List<Integer>> iterator = getChains(stone).iterator();
            while (iterator.hasNext()) {
                List<Integer> chain = iterator.next();
                if (!hasDegreesOfFreedom(chain)) {
                    captureChain(chain);
                    iterator.remove();
                }
            }
        }
    }

    private void captureChain(List<Integer> chain) {
        System.out.println("chain captured");
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
        return players;
    }

    /**
     * Determines the maximum amount of players, based on the amount of available stones
     * @return int with the max amount of players
     */
    private int getPlayersPerGame() {
        return playersPerGame;
    }

    private int numPlayers() {
        return players.size();
    }

    private boolean isTurn(Stone stone) {
        return stone.equals(turn);
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
        players.add(player);
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
        players.remove(player);
    }

    /**
     * Returns the player, based on the assigned Stone
     * @param stone to find the Player of
     * @return Player to which the Stone is assigned, null if none was found
     */
    public Player getPlayerByStone(Stone stone) {
        for (Player listedPlayer : players) {
            if (listedPlayer.getStone().equals(stone)) {
                return listedPlayer;
            }
        }
        return null;
    }

    private void nextTurn() {
        storeBoard(board);
        if (turnCounter % movesPerTurn == 0) {
            turn = turn.nextStone(numPlayers());
        }
        turnCounter++;
    }

    private void placeStone(int x, int y, Stone stone) {
        board.setField(x, y, stone);
        if (guiIsAvailable() && !future) {
            goGui.addStone(x, y, stone2bool(stone));
        }
    }

    private void removeStone(int x, int y) {
        board.setFieldEmpty(x, y);
        if (guiIsAvailable()) {
            goGui.removeStone(x, y);
        }
    }

    /**
     * Pass this turn
     * @return String with the end score if the game is over after this turn, empty string if not
     */
    public String pass() {
        if (turn == Stone.values()[1]) {
            resetPassCounter();
        }
        increasePassCounter();
        if (isFinished()) {
            return endString(false);
        }
        nextTurn();
        return "";
    }

    /**
     * Returns the END command string with the premature ending score
     * @return END string with the premature ending score
     */
    public String tableFlip() {
        return endString(true);
    }

    /**
     * Returns the END command string with the premature ending score
     * @return END string with the premature ending score
     */
    public String opponentGone() {
        return endString(true);
    }

    private String endString(boolean premature) {
        String scoreString = END.toString();
        int i = 0;
        int stoneIndex = 0;
        for (Stone stone : Stone.values()) {
            if (i > stoneIndex && i <= playersPerGame) {
                if (premature) {
                    scoreString += SPACE.toString() + EARLY_ENDING_SCORE;
                }
                else {
                    scoreString += SPACE.toString() + getScore(stone);
                }
            }
            i++;
        }
        goGui = null;
        return scoreString;
    }

    public boolean isFinished() {
        return passCounter >= numPlayers();
    }

    private void increasePassCounter() {
        passCounter++;
    }

    private void resetPassCounter() {
        passCounter = 0;
    }

    private int getScore(Stone stone) {
        int score = 0;

        score += getStoneScore(stone);
        System.out.print(stone + ": " + score);
        score += getTerritoryScore(stone);
        System.out.println(" / " + stone + ": " + score);
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
        System.out.println("TerritoryScore");
        int boardIndexSize = board.getBoardSize() * board.getBoardSize();
        Set<Integer> allChainIndices = new HashSet<>();
        int score = 0;
        for (int i = 1; i <= boardIndexSize; i++) {
            System.out.println("all indices");
            if (getStone(i).equals(EMPTY)) {
                System.out.println("EMPTY");
            }
            else {
                System.out.println("NOT EMPTY");
            }
            if (!allChainIndices.contains(i) && getStone(i).equals(EMPTY)) {
                System.out.println(i + ": " + getStone(i));
                //if (getStone(i).equals(EMPTY)) {
                Set<Integer> chain = new HashSet<>();
                chain.add(i);
                chain = increaseChain(i, chain, stone);
                System.out.println("chain");
                //                temp(chain);
                if (chain != null && chain.size() > 0) {
                    System.out.println(chain.size());
                    score += chain.size();
                    allChainIndices.addAll(chain);
                }
            }
        }
        return score;
    }

    private Set<Integer> increaseChain(int index, Set<Integer> chain, Stone stone) {
        for (Integer coordinate : neighbours(index)) {
            if ((!getStone(coordinate).equals(EMPTY) && !getStone(coordinate).equals(stone)) || chain.contains(coordinate)) {
                return null;
            }
            else if (getStone(coordinate).equals(EMPTY)) {
                chain.add(coordinate);
                increaseChain(coordinate, chain, stone);
            }
        }
        return chain;
    }

    private void temp(Set<Integer> chain) {
        System.out.println("Printing chain");
        for (Integer element : chain) {
            System.out.println(element);
        }
    }

    /**
     * Places a Stone on the board and the GUI and changes the turn
     * @param stone of the Player that is moving
     * @param x integer of the x coordinate of the position the Stone is placed
     * @param y integer of the y coordinate of the position the Stone is placed
     */
    public void move(Stone stone, int x, int y) {
        placeStone(x, y, stone);
        updateGame(stone, x, y);
        resetPassCounter();
        storeBoard(board);
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
     * @return true if the Stone may Tableflip, false if it may not
     */
    public boolean isValidTableFlip(Stone stone) {
        return isTurn(stone);
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
        if (!isTurn(stone)) {                                                  // Check if it is this player's turn
            result = NOT_TURN.toString();
        }
        else if (!board.isValidField(x, y)) {                                  // Check if the position exists
            result = NOT_FIELD.toString();
        }
        else if (!board.isEmpty(x, y)) {                                       // Check if the desired position is free
            result = NOT_FREE_FIELD.toString();
        }
        else {
            Game newGame = new Game(board.getBoardSize(), movesPerTurn, playersPerGame, true);
            Game futureGame = copyThisGame(newGame, stone);
            futureGame.move(stone, x, y);
            if (boardExists(futureGame.getBoard())) {                           // Check if it doesn't match a previous situation
                result = KO.toString();
            }
        }
        return result;
    }

    private void storeBoard(Board board) {
        setBoardHistory(board.boardCopy());
    }

    private List<Integer> copyIntegerList(List<Integer> list) {
        List<Integer> newList = new ArrayList<>();
        for (Integer index : list) {
            newList.add(index);
        }
        return newList;
    }

    private List<List<Integer>> copyListList (List<List<Integer>> list) {
        List<List<Integer>> newList = new ArrayList<>();
        for (List chain : list) {
            newList.add(copyIntegerList(chain));
        }
        return newList;
    }

    private Map<Stone, List<List<Integer>>> copyMap (Map<Stone, List<List<Integer>>> map) {
        Map<Stone, List<List<Integer>>> newTerritories = new HashMap<>();
        for (Stone stone : map.keySet()) {
            newTerritories.put(stone, copyListList(map.get(stone)));
        }
        return newTerritories;
    }

    public Game copyThisGame(Game futureGame, Stone stone) {          // public for testing
        futureGame.players.addAll(players);
        futureGame.turn = stone;
        futureGame.board = board.boardCopy();
        futureGame.turnCounter = turnCounter;
        futureGame.passCounter = passCounter;
        futureGame.boardHistory.addAll(boardHistory);
        futureGame.movesPerTurn = movesPerTurn;
        futureGame.playersPerGame = playersPerGame;
        futureGame.territories.putAll(copyMap(territories));
        return futureGame;
    }

    private boolean boardExists(Board board) {
        for (Board historicBoard : boardHistory) {
            if (board.equals(historicBoard)) {
                return true;
            }
        }
        return false;
    }
}
