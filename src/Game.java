import com.nedap.go.gui.GoGUIIntegrator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Game {

    private List<Player> players = new ArrayList<>();
    private Board board;
    private Stone turn = Stone.BLACK;;
    private int turnCounter = 1;
    private HashMap<Integer, Board> boardHistory = new HashMap<>();;
    private GoGUIIntegrator goGui;


    Game(Player player1, Player player2, int boardsize) {
        addPlayer(player1);
        addPlayer(player2);
        this.board = new Board(boardsize);
        goGui = new GoGUIIntegrator(true, true, getBoard().getSize());
        getGui().startGUI();
    }

    private Board getBoard() {
        return this.board;
    }

    private HashMap<Integer, Board> getBoardHistory() {
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

    private boolean isSuicide() {
        return false;
    }

    private boolean isTurn(Player player) {
        return player.getStone().equals(this.getTurn());
    }

    private static boolean stone2bool(Stone stone) {
        return stone.equals(Stone.WHITE);
    }

    private void addPlayer(Player player) {
        this.players.add(player);
    }

    private void nextTurn() {
        this.turn = this.turn.other();
        turnCounter++;
    }

    private Stone randomStone() {
        return Stone.BLACK;                                         // LEKKER HIGH-TECH, AANPASSEN..
    }

    private void placeStone(int x, int y, Stone stone) {
        getBoard().setField(x, y, stone);
        getGui().addStone(x, y, stone2bool(stone));
    }

    private void removeStone(int x, int y) {
        getBoard().emptyField(x, y);
        getGui().removeStone(x, y);
    }

    public void handleClientInput(String string) {

    }

    public static void handleClientCommand(String string) {
        String[] command = CommunicationToolbox.string2Command(string);
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
        Board boardCopy = new Board(getBoard().getSize());
        for (int i = 0; i < getBoard().getSize(); i++) {
            for (int j = 0; j < getBoard().getSize(); j++) {
                boardCopy.setField(i, j, getBoard().getField(i, j));
            }
        }
        getBoardHistory().put(getTurnNumber(), boardCopy);
    }

    private boolean boardExists() {
        boolean result = false;
        for (HashMap.Entry<Integer, Board> historicBoard : getBoardHistory().entrySet()) {
            if (getBoard().equals(historicBoard.getValue())) {                                    // NAKIJKEN OF EQUALS GOED IS!!!
                result = true;
            }
        }
        return result;
    }

}
