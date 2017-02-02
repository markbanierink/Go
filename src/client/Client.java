package client;

import game.*;
import helper.*;

import helper.enums.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import strategy.*;
import strategy.strategies.*;

import static helper.CommandToolbox.*;
import static helper.ConsoleToolbox.*;
import static helper.enums.Keyword.*;
import static helper.enums.PlayerType.*;
import static helper.enums.Resources.*;
import static helper.enums.Stone.*;
import static helper.enums.Strategies.*;

/**
 * The Client Class handles the client side of the game
 * It is extended by a ComputerClient or HumanClient
 * @author Mark Banierink
 */
public class Client implements ServerClientInterface {

    private static final int DEFAULT_PORT = 2727;
    private static final int DEFAULT_MOVES_PER_TURN = 1;
    private static final int DEFAULT_BOARD_SIZE = 5;
    private static final int MAX_CALCULATION_TIME = 1000;

    private InetAddress inetAddress;
    private int port;
    private ConsoleReader consoleReader;
    private SocketReader socketReader;
    private Socket socket;
    private BufferedWriter serverInput;
    private Player player;
    private Game game;
    private Thread socketReaderThread;
    private Thread consoleReaderThread;
    private PlayerType playerType = HUMAN;
    private Strategies strategyType;
    private Strategy strategy;

    public static void main(String[] args) {
        System.out.println("Starting Client");
        new Client();
    }

    /**
     * The constructor can be called from the subclasses. No parameters or environmental variables are required
     */
    public Client() {
        consoleReader = new ConsoleReader(this);
        inetAddress = getInetAddress();
        port = getPortNumber();
        startNewConnection();
    }

    protected void connectionLost() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            }
            catch (IOException e) {
                printOutput(e.getMessage());
            }
        }
        consoleReaderThread.interrupt();

        startNewConnection();
    }

    private void startNewConnection() {
        printOutput(CONNECTING_SOCKET);
        socket = getSocket(inetAddress, port);
        printOutput("Connected to socket");
        socketReader = new SocketReader(socket, this);
        socketReaderThread = new Thread(socketReader, "SocketReader");
        socketReaderThread.start();
        serverInput = createSocketWriter(socket);
        startNewPlayer();
        startNewGame();
    }

    private void startNewConsoleReaderThread() {
        consoleReaderThread = new Thread(consoleReader, "ConsoleReader");
        consoleReaderThread.start();
    }

    private int getPortNumber() {
        return requestIntegerInput(consoleReader, "Port number", DEFAULT_PORT, PORT_MIN, PORT_MAX);
    }

    private int requestBoardSize() {
        int boardSize = requestIntegerInput(consoleReader, "Preferred board size (odd)", DEFAULT_BOARD_SIZE, BOARD_SIZE_MIN, BOARD_SIZE_MAX);
        if (isValidBoardSize(boardSize, BOARD_SIZE_MIN, BOARD_SIZE_MAX)) {
            return boardSize;
        }
        else {
            return DEFAULT_BOARD_SIZE;
        }
    }

    private Player createPlayer() {
        Player player = new Player(requestStringInput(consoleReader, "What is your name", null));
        if (requestBooleanInput(consoleReader, "Do you want a computer player", "y")) {
            playerType = COMPUTER;
        }
        if (playerType == COMPUTER) {
            if (requestBooleanInput(consoleReader, "Do you want a smart strategy", "n")) {
                strategyType = SMART;
            }
            else {
                strategyType = RANDOM;
            }
        }
        setPlayer(player);
        return player;
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    protected Game getGame() {
        return game;
    }

    protected Player getPlayer() {
        return player;
    }

    private void createGame(int boardSize, int movesPerTurn, int playersPerGame) {
        game = new Game(boardSize, movesPerTurn, playersPerGame, false);
    }

    private void startNewPlayer() {
        handleServerInput(createCommandPlayer(createPlayer()));
    }

    private void startNewGame() {
        game = null;
        if (requestBooleanInput(consoleReader, "Do you want to start a new game", "y")) {
            handleServerInput(createCommandGo(requestBoardSize()));
            startNewConsoleReaderThread();
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
            commandWaiting();
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

    private void checkForMove() {
        if (player.getStone().equals(game.getTurn()) && !game.isFinished()) {
            if (playerType == COMPUTER) {
                if (strategyType == SMART) {
                    strategy = new SmartStrategy(game, getPlayer().getStone(), MAX_CALCULATION_TIME, this);
                }
                else {
                    strategy = new RandomStrategy(game, getPlayer().getStone(), MAX_CALCULATION_TIME, this);
                }
                Thread strategyThread = new Thread(strategy);
                strategyThread.start();
            }
            else {
                printOutput(YOUR_TURN);
            }
        }
    }

    private void commandWaiting() {
        printOutput(WAITING_FOR_OPPONENT);
    }

    private void commandReady(String[] arguments) {
        printOutput("Opponent found");
        int numPlayers = arguments.length - 2;
        createGame(Integer.parseInt(arguments[3]), DEFAULT_MOVES_PER_TURN, numPlayers);
        player.setStone(Stone.valueOf(arguments[1].toUpperCase()));
        game.addPlayer(player);
        Player opponent = new Player(arguments[2]);
        if (Stone.valueOf(arguments[1].toUpperCase()).equals(EMPTY.nextStone(numPlayers))) {
            opponent.setStone(EMPTY.nextStone(numPlayers).nextStone(numPlayers));
        }
        else {
            opponent.setStone(EMPTY.nextStone(numPlayers));
        }
        game.addPlayer(opponent);
        for (int i = 4; i < numPlayers + 2; i++) {
            Player newPlayer = new Player(arguments[i]);
            opponent.setStone(Stone.values()[(i-2)]);
            game.addPlayer(newPlayer);
        }
        printOutput("Starting game");
        checkForMove();
    }

    private void commandValid(String[] arguments) {
        String response = game.checkMoveValidity(Stone.valueOf(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]));
        if (response.equals(VALID.toString())) {
            game.move(Stone.valueOf(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]));
            checkForMove();
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH);
        }
    }

    private void commandInvalid(String[] arguments) {
        Player opponent = game.getPlayerByStone(Stone.valueOf(arguments[1]));
        printOutput("Invalid move by " + opponent.getName() + ": " + arguments[2]);
    }

    private void commandPassed(String[] arguments) {
        if (game.isValidPass(Stone.valueOf(arguments[1]))) {
            game.pass();
            printOutput(game.getPlayerByStone(Stone.valueOf(arguments[1])).getName() + " passed");
            checkForMove();
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH);
        }
    }

    private void commandTableFlipped(String[] arguments) {
        if (game.isValidTableFlip(Stone.valueOf(arguments[1].toUpperCase()))) {
            game.tableFlip();
            printOutput(game.getPlayerByStone(Stone.valueOf(arguments[1])).getName() + " tableflipped");
        }
        else {
            printOutput(SERVER_CLIENT_MISMATCH);
        }
    }

    private void commandChat(String[] arguments) {
        printOutput(arguments[1]);
    }

    private void commandWarning(String[] arguments) {
        printOutput("Warning: " + arguments[1]);
    }

    private void commandEnd(String[] arguments) {
        String result = determineWinner(arguments);
        String outputMessage = result + " with " + arguments[1];
        for (int j = 2; j < arguments.length; j++) {
            outputMessage += " to " + arguments[j];
        }
        printOutput(outputMessage);
        consoleReaderThread.interrupt();
        printOutput("Press [Enter] to proceed");
        while (consoleReaderThread.isAlive()) {
            // Waiting to press [Enter] and kill the consoleReaderThread
        }
        startNewGame();
    }

    private String determineWinner(String[] arguments) {
        int max = 1;
        boolean draw = true;
        String result;
        for (int i = 1; i < arguments.length; i++) {
            if (Integer.parseInt(arguments[i]) > Integer.parseInt(arguments[max])) {
                draw = false;
                max = i;
            }
            else if (Integer.parseInt(arguments[i]) < Integer.parseInt(arguments[max])) {
                draw = false;
            }
        }
        if (draw) {
            if (Integer.parseInt(arguments[1]) == -1) {
                result = "Winner due to premature leaving of your opponent";
            }
            else {
                result = "Draw..";
            }
        }
        else if (Stone.values()[max].equals(player.getStone())) {
            result = "WINNER!!!";
        }
        else {
            result = "Loser..";
        }
        return result;
    }

    private void noCommand(String string) {
        printOutput(createCommandWarning(string));
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
        else if (isHelpCommand(string)) {
            strategy = new RandomStrategy(game, getPlayer().getStone(), MAX_CALCULATION_TIME, this);
            printOutput("Maybe: " + strategy.determineMove().substring(MOVE.toString().length() + 1));
        }
        else {
            handleServerInput(createCommandChat(string));
        }
    }

    public void handleServerInput(String string) {
        try {
            serverInput.write(string);
            serverInput.newLine();
            serverInput.flush();
        }
        catch (IOException e) {
            printOutput("Socket lost");
            printOutput(e.getMessage());
        }
    }

    private void shutDown() {

        socketReader.setStop();
        try {
            socketReaderThread.join();
            consoleReaderThread.join();
        }
        catch (InterruptedException e) {
            printOutput(e.getMessage());
        }
        try {
            serverInput.close();
            socket.close();
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
    }

    private void printOutput(Resources string) {
        printOutput(string.toString());
    }

    private void printOutput(String string) {
        System.out.println(string);
    }
}
