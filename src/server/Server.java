package server;

import game.*;
import helper.*;

import helper.enums.Stone;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static helper.CommandToolbox.*;
import static helper.ConsoleToolbox.*;
import static helper.enums.Keyword.*;
import static helper.enums.Resources.*;

/**
 * This class handles the server-side of the game
 * No arguments are required.
 * @author Mark Banierink
 */
public class Server implements ServerClientInterface {

    private static final int DEFAULT_MAX_CLIENTS = 10;
    private static final int DEFAULT_PORT = 2727;
    private static final int DEFAULT_PLAYERS_PER_GAME = 2;
    private static final int DEFAULT_MOVES_PER_TURN = 1;
    private static final int MAX_CLIENTS_MIN = 2;
    private static final int MAX_CLIENTS_MAX = 20;
    private static final int PLAYERS_PER_GAME_MIN = 2;
    private static final int MOVES_PER_TURN_MIN = 1;
    private static final int MOVES_PER_TURN_MAX = 5;

    private Map<ClientHandler, Date> clientHandlers = new HashMap<>();
    private Set<Thread> clientHandlerThreads = new HashSet<>();
    private Map<Player, ClientHandler> playersList = new HashMap<>();
    private List<Game> gamesList = new ArrayList<>();
    private boolean stop = false;
    private ConsoleReader consoleReader;
    private boolean matchBoardSize;
    private int maxClients = -1;
    private int playersPerGame = -1;
    private int movesPerTurn = -1;

    public static void main(String[] args) {
        new Server();
    }

    /**
     * Constructor of Server. No input arguments are required.
     */
    public Server() {
        setConsoleReader(new ConsoleReader(this));
        printOutput("Starting Server");
        ServerSocket serverSocket = createServerSocket();
        printOutput("ServerSocket made");
        setMaxClients();
        setMatchBoardSize();
        setPlayersPerGame();
        setMovesPerTurn();
        Socket socket = null;
        while (!stop) {
            if (isClientHandlerAvailable()) {
                printOutput("Socket available");
                socket = createSocket(serverSocket);
                ClientHandler clientHandler = new ClientHandler(this, socket);
                Thread clientHandlerThread = new Thread(clientHandler, "ClientHandler " + createClientHandlerNumber());
                clientHandlerThread.start();
                listClientHandlerThread(clientHandlerThread);
                listClientHandler(clientHandler);
            }
        }
        shutDown(socket, serverSocket, SERVER_SHUTDOWN.toString());
    }

    private void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    private int getPortNumber() {
        return requestIntegerInput(consoleReader, "Port number", DEFAULT_PORT, PORT_MIN, PORT_MAX);
    }

    private void setMatchBoardSize() {
        matchBoardSize = requestBooleanInput(consoleReader, "Match players on board size", "n");
    }

    private void setMaxClients() {
        maxClients = requestIntegerInput(consoleReader, "Maximum number of clients that can connect", DEFAULT_MAX_CLIENTS, MAX_CLIENTS_MIN, MAX_CLIENTS_MAX);
    }

    private void setPlayersPerGame() {
        playersPerGame = requestIntegerInput(consoleReader, "Number of players per game", DEFAULT_PLAYERS_PER_GAME, PLAYERS_PER_GAME_MIN, maxPlayersPerGame());
    }

    private void setMovesPerTurn() {
        movesPerTurn = requestIntegerInput(consoleReader, "Number of moves per turn", DEFAULT_MOVES_PER_TURN, MOVES_PER_TURN_MIN, MOVES_PER_TURN_MAX);
    }

    private int maxPlayersPerGame() {
        return Stone.values().length - 1;
    }

    private ServerSocket createServerSocket() {
        int port = getPortNumber();
        while (true) {
            try {
                printOutput("Local IP address: " + InetAddress.getLocalHost().getHostAddress());
                return new ServerSocket(port);
            }
            catch (IOException e) {
                printOutput(SERVER_SOCKET_NOT_POSSIBLE.toString());
                printOutput(e.getMessage());
            }
        }
    }

    private Socket createSocket(ServerSocket serverSocket) {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
        return socket;
    }

    private int createClientHandlerNumber() {
        return clientHandlers.size() + 1;
    }

    private boolean isClientHandlerAvailable() {
        return !(numberOfPlayers() == maxClients);
    }

    private ClientHandler getClientHandler(Player player) {
        return playersList.get(player);
    }

    private Player getPlayer(ClientHandler clientHandler) {
        Player player = null;
        for (Player listedPlayer : playersList.keySet()) {
            if (playersList.get(listedPlayer).equals(clientHandler)) {
                player = listedPlayer;
            }
        }
        return player;
    }

    private boolean hasPlayer(ClientHandler clientHandler) {
        return getPlayer(clientHandler) != null;
    }

    private boolean hasClientHandler(Player player) {
        return getClientHandler(player) != null;
    }

    private synchronized void listClientHandler(ClientHandler clientHandler) {
        Date date = new Date();
        clientHandlers.put(clientHandler, date);
        printOutput("ClientHandler listed on " + date.toString());
    }

    private synchronized void listClientHandlerThread(Thread clientHandlerThread) {
        clientHandlerThreads.add(clientHandlerThread);
        printOutput("ClientHandlerThread listed");
    }

    protected void removeClientHandler(ClientHandler clientHandler) {
        if (hasPlayer(clientHandler)) {
            removePlayer(getPlayer(clientHandler));
        }
        removeListedClientHandler(clientHandler);
    }

    private synchronized void removeListedClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        printOutput("ClientHandler removed");
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        playersList.put(player, clientHandler);
        printOutput(player.getName() + " listed");
    }

    private synchronized void removeListedPlayer(Player player) {
        playersList.remove(player);
        printOutput(player.getName() + " removed");
    }

    private void addGamePlayer(Game game, Player player) {
        game.addPlayer(player);
        printOutput("Player added to Game " + getGameNumber(game));
        broadcastPlayer(player, WAITING.toString());
        checkGameToStart(game);
    }

    private void removePlayer(Player player) {
        if (hasGame(player)) {
            removeGamePlayer(getGame(player), player);
        }
        removeListedPlayer(player);
    }

    private void removeGamePlayer(Game game, Player player) {
        if (hasOnePlayer(game)) {
            removeGame(getGame(player));
        }
        else {
            game.removePlayer(player);
            printOutput("Player removed from Game " + getGameNumber(game));
        }
    }

    private synchronized void removeListedGame(Game game) {
        printOutput("Game " + getGameNumber(game) + " removed");
        gamesList.remove(game);
    }

    private synchronized void listGame(Game game) {
        gamesList.add(game);
        printOutput("Game " + getGameNumber(game) + " listed");
    }

    private Game createGame(int boardSize) {
        Game game = new Game(boardSize, movesPerTurn, playersPerGame);
        listGame(game);
        return game;
    }

    private void removeGame(Game game) {
        broadcastGame(game, "This game is removed", null);
        removeListedGame(game);
        //        for (Player player : game.getPlayers()) {
        //            removeGamePlayer(game, player);
        //        }
    }

    private void checkGameToStart(Game game) {
        if (isFullGame(game)) {
            broadcastGame(game, readyMessage(game), null);
            printOutput("Game " + getGameNumber(game) + " started");
        }
    }

    private int getGameNumber(Game game) {
        return gamesList.indexOf(game) + 1;
    }

    private boolean isFullGame(Game game) {
        return game.getPlayers().size() == playersPerGame;
    }

    private boolean hasOnePlayer(Game game) {
        return game.getPlayers().size() == 1;
    }

    private boolean isGameAvailable(int boardSize) {
        return availableGame(boardSize) != null;
    }

    private Game availableGame(int boardSize) {
        for (Game game : gamesList) {
            if (!isFullGame(game)) {
                if (!matchBoardSize || game.getBoard().getBoardSize() == boardSize) {
                    return game;
                }
            }
        }
        return null;
    }

    private int numberOfPlayers() {
        return playersList.size();
    }

    private void broadcastClient(ClientHandler clientHandler, String message) {
        handleClientOutput(clientHandler, message);
    }

    private void broadcastPlayer(Player player, String message) {
        handleClientOutput(getClientHandler(player), message);
    }

    private void broadcastGame(Game game, String message, Player excludedPlayer) {
        for (Player player : game.getPlayers()) {
            if (!player.equals(excludedPlayer)) {
                if (hasClientHandler(player)) {
                    broadcastPlayer(player, message);
                }
            }
        }
    }

    private void broadcastPlayers(String message, Player excludedPlayer) {
        for (Player player : playersList.keySet()) {
            if (!player.equals(excludedPlayer)) {
                broadcastPlayer(player, message);
            }
        }
    }

    private void broadcastClients(String string, ClientHandler excludedClientHandler) {
        for (ClientHandler clientHandler : clientHandlers.keySet()) {
            if (!clientHandler.equals(excludedClientHandler)) {
                broadcastClient(clientHandler, string);
            }
        }
    }

    private String readyMessage(Game game) {
        int i = 0;
        Stone[] stones = new Stone[game.getPlayers().size()];
        for (Player player : game.getPlayers()) {
            stones[i] = player.getStone();
            i++;
        }
        return createCommandReady(game.getBoard().getBoardSize(), stones, game.getPlayers());
    }

    private Player createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[0]);
        listPlayer(player, clientHandler);
        printOutput("New player: " + split[0]);
        return player;
    }

    protected void handleClientInput(ClientHandler clientHandler, String string) {
        String name = "[New Client]";
        if (hasPlayer(clientHandler)) {
            name = getPlayer(clientHandler).getName();
        }
        printOutput(name + ": " + string);
        if (isPlayerCommand(string) && !hasPlayer(clientHandler)) {
            if (!playerNameExists(playerArguments(string)[1])) {
                commandPlayer(clientHandler, playerArguments(string));
            }
            else {
                broadcastClient(clientHandler, createCommandWarning(NAME_TAKEN.toString()));
            }
        }
        else if (hasPlayer(clientHandler)) {
            Player player = getPlayer(clientHandler);
            if (isGoCommand(string) && !hasGame(player)) {
                commandGo(player, goArguments(string));
            }
            else if (hasGame(player)) {
                Game game = getGame(player);
                if (isCancelCommand(string)) {
                    if (!isFullGame(game)) {
                        commandCancel(game, player);
                    }
                    else {
                        noCommand(clientHandler, string);
                    }
                }
                else if (isMoveCommand(string)) {
                    commandMove(game, player, moveArguments(string));
                }
                else if (isPassCommand(string)) {
                    commandPass(game, player);
                }
                else if (isTableFlipCommand(string)) {
                    commandTableFlip(game, player);
                }
                else if (isChatCommand(string)) {
                    commandChat(clientHandler, chatArguments(string));
                }
                else {
                    System.out.println("No command 1");
                    noCommand(clientHandler, string);
                }
            }
            else {
                noCommand(clientHandler, string);
            }
        }
        else if (isChatCommand(string)) {
            commandChat(clientHandler, chatArguments(string));
        }
        else {
            noCommand(clientHandler, string);
        }
    }

    private boolean playerNameExists(String name) {
        for (Player player : playersList.keySet()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void commandPlayer(ClientHandler clientHandler, String[] arguments) {
        createPlayer(clientHandler, arguments[1]);
    }

    private void commandGo(Player player, String[] arguments) {
        int boardSize = Integer.parseInt(arguments[1]);
        if (isGameAvailable(boardSize)) {
            Game game = availableGame(boardSize);
            addGamePlayer(game, player);
        }
        else {
            Game newGame = createGame(boardSize);
            addGamePlayer(newGame, player);
            printOutput("Player added to Game " + getGameNumber(newGame));
        }
    }

    private void commandCancel(Game game, Player player) {
        removeGamePlayer(game, player);
    }

    private boolean hasGame(Player player) {
        return getGame(player) != null;
    }

    private Game getGame(Player player) {
        for (Game listedGame : gamesList) {
            for (Player listedPlayer : listedGame.getPlayers()) {
                if (listedPlayer.equals(player)) {
                    return listedGame;
                }
            }
        }
        return null;
    }

    private void commandMove(Game game, Player player, String[] arguments) {
        int x = Integer.parseInt(arguments[1]);
        int y = Integer.parseInt(arguments[2]);
        String response = game.checkMoveValidity(player.getStone(), x, y);
        if (game.isValidMove(player.getStone(), x, y)) {
            game.move(player.getStone(), x, y);
            broadcastGame(game, createCommandValid(player.getStone(), x, y), null);
        }
        else {
            kickClient(getClientHandler(player), createCommandInvalid(player.getStone(), response));
        }
    }

    private void commandPass(Game game, Player player) {
        if (game.isValidPass(player.getStone())) {
            String response = game.pass();
            broadcastGame(game, createCommandPassed(player.getStone()), null);
            if (isEndCommand(response)) {
                broadcastGame(game, response, null);
                removeGame(game);
            }
        }
        else {
            kickClient(getClientHandler(player), createCommandInvalid(player.getStone(), NOT_TURN.toString()));
        }
    }

    private void commandTableFlip(Game game, Player player) {
        if (game.isValidTableFlip(player.getStone())) {
            broadcastGame(game, createCommandTableFlipped(player.getStone()), null);
            game.tableFlip();
        }
    }

    private void commandChat(ClientHandler clientHandler, String[] arguments) {
        Player player = getPlayer(clientHandler);
        String sender = player.getName();
        if (hasGame(getPlayer(clientHandler))) {
            Game game = getGame(getPlayer(clientHandler));
            broadcastGame(game, createCommandChat(sender, arguments[1]), player);
        }
        else {
            broadcastPlayers(createCommandChat(sender, arguments[1]), player);
        }
    }

    private void noCommand(ClientHandler clientHandler, String string) {
        handleClientOutput(clientHandler, createCommandWarning(string));
    }

    private void kickClient(ClientHandler clientHandler, String reason) {
        handleClientOutput(clientHandler, reason);
        handleClientOutput(clientHandler, CHAT.toString() + SPACE + SERVER + ":" + SPACE + KICKED);
        String name = "Anonymous Client";
        if (hasPlayer(clientHandler)) {
            name = getPlayer(clientHandler).getName();
        }
        broadcastClients(name + SPACE + IS_KICKED, clientHandler);
        removeClientHandler(clientHandler);
        clientHandler.shutDown();
    }

    private void handleClientOutput(ClientHandler clientHandler, String string) {
        clientHandler.handleClientOutput(string);
    }

    protected void printOutput(String string) {
        System.out.println(string);
    }

    public void handleConsoleInput(String line) {

    }

    private void stopServer() {
        stop = true;
    }

    private void shutDown(Socket socket, ServerSocket serverSocket, String broadcastMessage) {
        broadcastClients(broadcastMessage, null);
        for (ClientHandler clientHandler : clientHandlers.keySet()) {
            clientHandler.setStop();
        }
        for (Thread clientHandlerThread : clientHandlerThreads) {
            try {
                clientHandlerThread.join();
            }
            catch (InterruptedException e) {
                printOutput(e.getMessage());
            }
        }
        try {
            socket.close();
            serverSocket.close();
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
    }
}


