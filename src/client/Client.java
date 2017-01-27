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
import static helper.enums.Strings.*;

/**
 * The Client Class handles the client side of the game
 * It is extended by a ComputerClient or HumanClient
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
        if (requestBooleanInput(consoleReader, "Do you want to start a new game", "y")) {
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
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(requestStringInput(consoleReader, "Enter the IP address", "localhost"));
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
            commandReady(readyArguments(string));
        }
        else if (isValidCommand(string)) {
            commandValid(validArguments(string));
        }
        else if (isInvalidCommand(string)) {
            commandInvalid(invalidArguments(string));
        }
        else if (isPassedCommand(string)) {
            commandPassed(passedArguments(string));
        }
        else if (isTableFlippedCommand(string)) {
            commandTableFlipped(tableFlippedArguments(string));
        }
        else if (isChatCommand(string)) {
            commandChat(chatArguments(string));
        }
        else if (isWarningCommand(string)) {
            commandWarning(warningArguments(string));
        }
        else if (isEndCommand(string)) {
            commandEnd(endArguments(string));
        }
        else {
            noCommand(string);
        }
    }

    private void commandWaiting(String string) {
        printOutput(WAITING_FOR_OPPONENT.toString());
    }

    private void commandReady(String[] arguments) {
        createGame(Integer.parseInt(arguments[1]), DEFAULT_MOVES_PER_TURN);
        for (int i = 2; i < arguments.length; i += 2) {
            Player player = new Player(arguments[i + 1]);
            player.setStone(Stone.valueOf(arguments[i].toUpperCase()));
            getGame().addPlayer(player);
        }
    }

    private void commandValid(String[] arguments) {
        String response = getGame().checkMoveValidity(Stone.valueOf(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]));
        if (response.equals(VALID.toString())) {
            getGame().move(Stone.valueOf(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]));
            printOutput("Move by " + getGame().getPlayerByStone(Stone.valueOf(arguments[1])).getName() + ": " + arguments[2] + ", " + arguments[3]);
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandInvalid(String[] arguments) {
        Player opponent = getGame().getPlayerByStone(Stone.valueOf(arguments[1]));
        printOutput("Invalid move by " + opponent.getName() + ": " + arguments[2]);
    }

    private void commandPassed(String[] arguments) {
        if (getGame().isValidPass(Stone.valueOf(arguments[1]))) {
            getGame().pass();
            printOutput(getGame().getPlayerByStone(Stone.valueOf(arguments[1])).getName() + " passed");
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandTableFlipped(String[] arguments) {
        if (getGame().isValidTableflip(Stone.valueOf(arguments[1]))) {
            getGame().tableFlip();
            printOutput(getGame().getPlayerByStone(Stone.valueOf(arguments[1])).getName() + " tableflipped");
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandChat(String[] arguments) {
        printOutput("Chat message: " + arguments[1]);
    }

    private void commandWarning(String[] arguments) {
        printOutput("Warning: " + arguments[1]);
    }

    private void commandEnd(String[] arguments) {
        int max = 1;
        String result;
        for (int i = 2; i < arguments.length; i++) {
            if (Integer.parseInt(arguments[i]) >= Integer.parseInt(arguments[max])) {
                max = Integer.parseInt(arguments[i]);
            }
        }
        if (Stone.values()[max].equals(getPlayer().getStone())) {
            result = "the winner";
        }
        else {
            result = "a loser";
        }
        String outputMessage = "You are " + result + " with " + arguments[1];
        for (int j = 2; j < arguments.length; j++) {
            outputMessage += " to " + arguments[j];
        }
        printOutput(outputMessage);
        startNewGame();
    }

    private void noCommand(String string) {
        System.out.println("Client warning");
        printOutput(WARNING.toString() + SPACE + UNKNOWN_KEYWORD + SPACE + ": " + string);
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
