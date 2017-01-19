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
    private int boardsize;
    private Stone turn;
    private int turnCounter;
    private HashMap<Integer, Board> boardHistory;
    private GoGUIIntegrator goGui;


    Game(Player player1, Player player2, int boardsize) {
        this.boardsize = boardsize;
        this.players.add(player1);
        this.players.add(player2);
        this.board = new Board(boardsize);
        boardHistory = new HashMap<>();
        turn = Stone.BLACK;
        turnCounter = 1;
        goGui = new GoGUIIntegrator(true, true, board.getBoardsize());
        goGui.startGUI();
    }

    private Stone randomStone() {
        return Stone.BLACK;                                         // LEKKER HIGH-TECH, AANPASSEN..
    }

    public int getBoardsize() {
        return this.boardsize;
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
            this.board.setField(x, y, player.getStone());
            copyBoard();
            nextTurn();
            goGui.addStone(x, y, stone2bool(player.getStone()));
            //player1.confirmMove(x, y);
            //player2.confirmMove(x, y);
        }
    }

    private boolean isValidMove(Player player, int x, int y) {
        boolean result = true;
        // Check if it is this player's turn
        if (!isTurn(player)) {
            result = false;
        }
        // Check if the desired position is free
        if (!board.isEmpty(x, y)) {
            result = false;
        }
        // Check if it doesn't match a previous situation
        if (boardExists()){
            result = false;
        }
        // Check if it isn't a suicide move
        // Etc.
        return result;
    }

    private void copyBoard() {
        Board boardCopy = new Board(this.board.getBoardsize());
        for (int i = 0; i < this.board.getBoardsize(); i++) {
            for (int j = 0; j < this.board.getBoardsize(); j++) {
                boardCopy.setField(i, j, this.board.getField(i, j));
            }
        }
        boardHistory.put(getTurnNumber(), boardCopy);
    }

    private boolean boardExists() {
        boolean result = false;
        for (HashMap.Entry<Integer, Board> historicBoard : boardHistory.entrySet()) {
            if (this.board.equals(historicBoard.getValue())) {                                    // NAKIJKEN OF EQUALS GOED IS!!!
                result = true;
            }
        }
        return result;
    }

    private boolean isTurn(Player player) {
        return player.getStone().equals(this.getTurn());
    }

    private int getTurnNumber() {
        return this.turnCounter;
    }

    private Stone getTurn() {
        return this.turn;
    }

    private void nextTurn() {
        this.turn = this.turn.other();
        turnCounter++;
    }

    private static boolean stone2bool(Stone stone) {
        return stone.equals(Stone.WHITE);
    }

}
