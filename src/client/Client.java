package client;

import game.*;
import helper.*;

import helper.enums.Stone;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static helper.ComToolbox.*;
import static helper.ConsoleToolbox.*;
import static helper.enums.Keyword.*;
import static helper.enums.Stone.*;
import static helper.enums.Strings.*;

/**
 * The Client Class handles the client side of the game
 * It is extended by a ComputerClient or HumanClient
 *
 * @author Mark Banierink
 */
public class Client implements ServerClientInterface {

    private static final int DEFAULT_PORT = 2727;
    private static final int DEFAULT_MOVES_PER_TURN = 1;
    private static final int DEFAULT_BOARD_SIZE = 19;

    private static final int[] PORT_RANGE = {0, 65535};
    //private static final int[] MOVES_PER_TURN_RANGE = {1, 5};

    private ConsoleReader consoleReader;
    private Socket socket;
    private BufferedWriter serverInput;
    private Player player;
    private Game game;

    /**
     * The constructor can be called from the subclasses. No parameters or environmental variables are required
     */
    public Client() {
        this.consoleReader = new ConsoleReader(this);

        InetAddress inetAddress = getInetAddress();
        int port = getPortNumber();
        printOutput("Connecting to socket");
        this.socket = getSocket(inetAddress, port);
        printOutput("Connected to socket");
        Thread socketReader = new Thread(new SocketReader(this.socket, this), "SocketReader");
        socketReader.start();
        this.serverInput = createSocketWriter(this.socket);
        startNewPlayer();
        startNewGame();
        (new Thread(consoleReader, "ConsoleReader")).start();
    }

    private ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    private int getPortNumber() {
        return requestIntegerInput(getConsoleReader(), "Port number", DEFAULT_PORT, PORT_RANGE);
    }

    private int requestBoardSize() {
        int boardSize = requestIntegerInput(getConsoleReader(), "Preferred board size (odd)", DEFAULT_BOARD_SIZE, BOARD_SIZE_RANGE);
        if (isBoardSize(boardSize, BOARD_SIZE_RANGE)) {
            return boardSize;
        }
        else {
            return DEFAULT_BOARD_SIZE;
        }
    }

    private Player createPlayer() {
        return new Player(requestStringInput(consoleReader, "What is your name", null));
    }

    private Player getPlayer() {
        return this.player;
    }

    private void createGame(int boardSize, int movesPerTurn) {
        this.game = new Game(boardSize, movesPerTurn);
    }

    private Game getGame() {
        return this.game;
    }

    private void startNewPlayer() {
        handleServerInput(createCommandPlayer(createPlayer()));
    }

    private void startNewGame() {
        if (requestBooleanInput(consoleReader, "Do you want to start a game", null)) {
            handleServerInput(createCommandGo(requestBoardSize()));
        }
        else {
            shutDown();
        }
    }

    private Socket getSocket(InetAddress inetAddress, int port) {
        while (true) {
            try {
                return new Socket(inetAddress, port);
            }
            catch (IOException e) {
                printOutput("No Socket available, retrying...");
            }
        }
    }

    private InetAddress getInetAddress() {
        String inetAddressString = requestStringInput(consoleReader, "Enter the IP address", "localhost");
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(inetAddressString);
        }
        catch (UnknownHostException e) {
            printOutput("No Internet Address");
        }
        return inetAddress;
    }

    private BufferedWriter createSocketWriter(Socket socket) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
        return out;
    }

    protected void handleServerOutput(String string) {
        if (isWaitingCommand(string)) {
            commandWaiting(string);
        }
        else if (isReadyCommand(string)) {
            commandReady(string);
        }
        else if (isValidCommand(string)) {
            commandValid(string);
        }
        else if (isInvalidCommand(string)) {
            commandInvalid(string);
        }
        else if (isPassedCommand(string)) {
            commandPassed(string);
        }
        else if (isTableFlippedCommand(string)) {
            commandTableFlipped(string);
        }
        else if (isChatCommand(string)) {
            commandChat(string);
        }
        else if (isWarningCommand(string)) {
            commandWarning(string);
        }
        else if (isEndCommand(string)) {
            commandEnd(string);
        }
        else {
            noCommand(string);
        }
    }

    private void commandWaiting(String string) {
        printOutput(WAITING_FOR_OPPONENT.toString());
    }

    private void commandReady(String string) {
        String[] split = splitString(string);
        createGame(Integer.parseInt(split[0]), DEFAULT_MOVES_PER_TURN);
        for (int i = 1; i < split.length; i +=2) {
            Player player = new Player(split[i+1]);
            player.setStone(Stone.valueOf(split[i]));
            getGame().addPlayer(player);
        }
    }

    private void commandValid(String arguments) {
        String[] split = splitString(arguments);
        String response = getGame().checkMoveValidity(Stone.valueOf(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        if (response.equals(VALID.toString())) {
            getGame().move(Stone.valueOf(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            handleConsoleInput("Move by " + getGame().getPlayerByStone(Stone.valueOf(split[0])).getName() + ": " + split[1] + ", " + split[2]);
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandInvalid(String string) {
        String[] split = splitString(string);
        Player opponent = getGame().getPlayerByStone(Stone.valueOf(split[1]));
        printOutput("Invalid move by " + opponent.getName() + ": " + split[2]);
    }

    private void commandPassed(String string) {
        String[] split = splitString(string);
        if (getGame().isValidPass(Stone.valueOf(split[1]))) {
            getGame().pass();
            printOutput(getGame().getPlayerByStone(Stone.valueOf(split[1])).getName() + " passed");
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandTableFlipped(String string) {
        String[] split = splitString(string);
        if (getGame().isValidTableflip(Stone.valueOf(split[1]))) {
            getGame().tableflip();
            printOutput(getGame().getPlayerByStone(Stone.valueOf(split[1])).getName() + " tableflipped");
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandChat(String string) {
        String message = string.substring(CHAT.toString().length());
        printOutput("Chat message: " + message);
    }

    private void commandWarning(String string) {
        String message = string.substring(WARNING.toString().length());
        printOutput("Warning: " + message);
    }

    private void commandEnd(String string) {
        String[] split = splitString(string);
        Stone winner = null;
        if (Integer.parseInt(split[1]) > Integer.parseInt(split[1])) {
            winner = BLACK;
        }
        else if (Integer.parseInt(split[1]) > Integer.parseInt(split[1])) {
            winner = WHITE;
        }
        if (winner == null) {
            printOutput("The game ended with a draw");
        }
        else if (getPlayer().getStone() == winner) {
            printOutput("You won with " + split[1] + " to " + split[2] + "!");
        }
        else {
            printOutput("You lost with " + split[1] + " to " + split[2]);
        }
        startNewGame();
    }

    private void noCommand(String string) {
        System.out.println("Client warning");
        printOutput(WARNING.toString() + SPACE + UNKNOWN_KEYWORD);
    }

    public void handleConsoleInput(String string) {
        if (isMoveCommand(string)) {
            handleServerInput(string);
        }
        else if (isPassCommand(string)) {
            handleServerInput(string);
        }
        else if (isTableFlipCommand(string)) {
            handleServerInput(string);
        }
        else if (isChatCommand(string)) {
            handleServerInput(string);
        }
    }

    private void handleServerInput(String string) {
        try {
            this.serverInput.write(string);
            this.serverInput.newLine();
            this.serverInput.flush();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void shutDown() {

        try {
            this.serverInput.close();
            this.socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printOutput(String string) {
        System.out.println(string);
    }


//    private String getConsoleInput(String question) {
//        System.out.print(question);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        String consoleInput = "";
//        try {
//            consoleInput = bufferedReader.readLine();
//        }
//        catch (IOException e) {
//            printOutput(e.getMessage());
//        }
//        return consoleInput;
//    }

}
