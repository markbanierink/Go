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
    private static final int MAX_CLIENTS_MIN = 2;
    private static final int MAX_CLIENTS_MAX = 20;


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
        printOutput(SERVER_START.toString());
        ServerSocket serverSocket = createServerSocket();
        printOutput(SERVERSOCKET_MADE.toString());
        setMaxClients();
        setMatchBoardSize();
        setPlayersPerGame();
        setMovesPerTurn();
        Socket socket = null;
        while (!stop) {
            if (isClientHandlerAvailable()) {
                printOutput(SOCKET_AVAILABLE.toString());
                socket = createSocket(serverSocket);
                ClientHandler clientHandler = new ClientHandler(this, socket);
                Thread clientHandlerThread = new Thread(clientHandler, CLIENTHANDLER.toString() + SPACE + createClientHandlerNumber());
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
        return requestIntegerInput(consoleReader, PORT_NUMBER.toString(), DEFAULT_PORT, PORT_MIN, PORT_MAX);
    }

    private void setMatchBoardSize() {
        matchBoardSize = requestBooleanInput(consoleReader, MATCH_BOARDSIZE.toString(), N.toString());
    }

    private void setMaxClients() {
        maxClients = requestIntegerInput(consoleReader, MAXIMUM_CLIENTS.toString(), DEFAULT_MAX_CLIENTS, MAX_CLIENTS_MIN, MAX_CLIENTS_MAX);
    }

    private void setPlayersPerGame() {
        playersPerGame = requestIntegerInput(consoleReader, PLAYERS_PER_GAME.toString(), DEFAULT_PLAYERS_PER_GAME, PLAYERS_PER_GAME_MIN, maxPlayersPerGame());
    }

    private void setMovesPerTurn() {
        movesPerTurn = requestIntegerInput(consoleReader, MOVES_PER_TURN.toString(), DEFAULT_MOVES_PER_TURN, MOVES_PER_TURN_MIN, MOVES_PER_TURN_MAX);
    }

    private int maxPlayersPerGame() {
        return Stone.values().length - 1;
    }

    private ServerSocket createServerSocket() {
        int port = getPortNumber();
        while (true) {
            try {
                printOutput(LOCAL_IP.toString() + COLON + InetAddress.getLocalHost().getHostAddress());
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
        printOutput(CLIENTHANDLER_LISTED.toString() + COLON + date.toString());
    }

    private synchronized void listClientHandlerThread(Thread clientHandlerThread) {
        clientHandlerThreads.add(clientHandlerThread);
        printOutput(CLIENTHANDLERTHREAD_LISTED.toString());
    }

    protected void removeClientHandler(ClientHandler clientHandler) {
        if (hasPlayer(clientHandler)) {
            if (hasGame(getPlayer(clientHandler))) {
                removeGamePlayer(getGame(getPlayer(clientHandler)), getPlayer(clientHandler));
            }
            removePlayer(getPlayer(clientHandler));
        }
        removeListedClientHandler(clientHandler);
    }

    private synchronized void removeListedClientHandler(ClientHandler clientHandler) {
        try {
            clientHandler.getSocket().close();
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
        clientHandlers.remove(clientHandler);
        printOutput(CLIENTHANDLER_REMOVED.toString());
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        playersList.put(player, clientHandler);
        printOutput(LISTED.toString() + COLON + player.getName());
    }

    private synchronized void removeListedPlayer(Player player) {
        playersList.remove(player);
        printOutput(REMOVED.toString() + COLON + player.getName());
    }

    private void addGamePlayer(Game game, Player player) {
        game.addPlayer(player);
        printOutput(PLAYER_ADD_GAME.toString() + COLON + getGameNumber(game));
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
        game.removePlayer(player);
        broadcastGame(game, game.opponentGone(), null);
        removeGame(game);
        printOutput(PLAYER_REMOVED_GAME.toString() + COLON + getGameNumber(game));
    }

    private synchronized void removeListedGame(Game game) {
        printOutput(GAME_REMOVED.toString() + COLON + getGameNumber(game));
        gamesList.remove(game);
    }

    private synchronized void listGame(Game game) {
        gamesList.add(game);
        printOutput(GAME_LISTED.toString() + COLON + getGameNumber(game));
    }

    private Game createGame(int boardSize) {
        Game game = new Game(boardSize, movesPerTurn, playersPerGame, false);
        listGame(game);
        return game;
    }

    private void removeGame(Game game) {
        broadcastGame(game, GAME_REMOVED.toString(), null);
        removeListedGame(game);
        game.closeGui();
    }

    private void checkGameToStart(Game game) {
        if (isFullGame(game)) {
            for (Player player : game.getPlayers()) {
                broadcastPlayer(player, readyMessage(game, player));
            }
            printOutput(GAME_STARTED.toString() + COLON + getGameNumber(game));
        }
    }

    private int getGameNumber(Game game) {
        return gamesList.indexOf(game) + 1;
    }

    private boolean isFullGame(Game game) {
        return game.getPlayers().size() == playersPerGame;
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

    private String readyMessage(Game game, Player player) {
         return createCommandReady(game, player);
    }

    private Player createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[0]);
        listPlayer(player, clientHandler);
        printOutput(NEW_PLAYER.toString() + SPACE + split[0]);
        return player;
    }

    protected void handleClientInput(ClientHandler clientHandler, String string) {
        String name = NEW_CLIENT.toString();
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
            printOutput(PLAYER_ADD_GAME.toString() + SPACE + getGameNumber(newGame));
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
            broadcastGame(game, game.tableFlip(), null);
            removeGame(game);
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
        String name = ANONYMOUS.toString();
        if (hasPlayer(clientHandler)) {
            name = getPlayer(clientHandler).getName();
        }
        broadcastClients(name + SPACE + IS_KICKED, clientHandler);
        removeClientHandler(clientHandler);
        clientHandler.shutDown();
    }

    private void handleClientOutput(ClientHandler clientHandler, String string) {
        printOutput(string);
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


