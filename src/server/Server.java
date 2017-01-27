package server;

import game.*;
import helper.*;

import helper.enums.Stone;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static helper.ComToolbox.*;
import static helper.ConsoleToolbox.*;
import static helper.enums.Keyword.*;
import static helper.enums.Strings.*;

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

    private static final int[] MAX_CLIENTS_RANGE = {2, 20};
    private static final int[] PORT_RANGE = {0, 65535};
    private static final int MIN_PLAYERS_PER_GAME = 2;
    private static final int[] MOVES_PER_TURN_RANGE = {1, 5};

    private HashMap<ClientHandler, Date> clientHandlers = new HashMap<>();
    private HashMap<Player, ClientHandler> playersList = new HashMap<>();
    private List<Game> gamesList = new ArrayList<>();
    private boolean serverLoop = true;
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
        while (serverLoop) {
            if (isClientHandlerAvailable()) {
                printOutput("Socket available");
                socket = createSocket(serverSocket);
                ClientHandler clientHandler = new ClientHandler(this, socket);
                (new Thread(clientHandler, "ClientHandler " + createClientHandlerNumber())).start();
                listClientHandler(clientHandler);
            }
        }
        shutDown(socket, serverSocket, SERVER_SHUTDOWN.toString());
    }

    private void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    private ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    private int getPortNumber() {
        return requestIntegerInput(getConsoleReader(), "Port number", DEFAULT_PORT, PORT_RANGE);
    }

    private void setMatchBoardSize() {
        this.matchBoardSize = requestBooleanInput(getConsoleReader(), "Match players on board size", "n");
    }

    private void setMaxClients() {
        this.maxClients = requestIntegerInput(getConsoleReader(), "Maximum number of clients that can connect", DEFAULT_MAX_CLIENTS, MAX_CLIENTS_RANGE);
    }

    private void setPlayersPerGame() {
        int[] playersPerGameRange = {MIN_PLAYERS_PER_GAME, maxPlayersPerGame()};
        this.playersPerGame = requestIntegerInput(getConsoleReader(), "Number of players per game", DEFAULT_PLAYERS_PER_GAME, playersPerGameRange);
    }

    private void setMovesPerTurn() {
        this.movesPerTurn = requestIntegerInput(getConsoleReader(), "Number of moves per turn", DEFAULT_MOVES_PER_TURN, MOVES_PER_TURN_RANGE);
    }

    private int maxPlayersPerGame() {
        return Stone.values().length - 1;
    }

    private boolean getMatchBoardSize() {
        return this.matchBoardSize;
    }

    private int getPlayersPerGame() {
        return this.playersPerGame;
    }

    private int getMovesPerTurn() {
        return this.movesPerTurn;
    }

    private int getMaxClients() {
        return this.maxClients;
    }

    private ServerSocket createServerSocket() {
        int port = getPortNumber();
        while (true) {
            try {
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
        return getClientHandlers().size() + 1;
    }

    private boolean isClientHandlerAvailable() {
        return !(numberOfPlayers() == getMaxClients());
    }

    private ClientHandler getClientHandler(Player player) {
        return getPlayersList().get(player);
    }

    private Player getPlayer(ClientHandler clientHandler) {
        Player player = null;
        for (Player listedPlayer : getPlayersList().keySet()) {
            if (getPlayersList().get(listedPlayer).equals(clientHandler)) {
                player = listedPlayer;
            }
        }
        return player;
    }

    private boolean hasPlayer(ClientHandler clientHandler) {
        return getPlayer(clientHandler) != null;
    }

    private HashMap<ClientHandler, Date> getClientHandlers() {
        return this.clientHandlers;
    }

    private synchronized void listClientHandler(ClientHandler clientHandler) {
        Date date = new Date();
        getClientHandlers().put(clientHandler, date);
        printOutput("ClientHandler listed on " + date.toString());
    }

    protected void removeClientHandler(ClientHandler clientHandler) {
        if (hasPlayer(clientHandler)) {
            removePlayer(getPlayer(clientHandler));
        }
        removeListedClientHandler(clientHandler);
    }

    private synchronized void removeListedClientHandler(ClientHandler clientHandler) {
        getClientHandlers().remove(clientHandler);
        printOutput("ClientHandler removed");
    }

    private HashMap<Player, ClientHandler> getPlayersList() {
        return this.playersList;
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        getPlayersList().put(player, clientHandler);
        printOutput(player.getName() + " listed");
    }

    private synchronized void removeListedPlayer(Player player) {
        getPlayersList().remove(player);
        printOutput(player.getName() + " removed");
    }

    private List<Game> getGamesList() {
        return this.gamesList;
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
        game.removePlayer(player);
        printOutput("Player removed from Game " + getGameNumber(game));
        if (isEmptyGame(game)) {
            deleteGame(getGame(player));
        }
    }

    private synchronized void removeListedGame(Game game) {
        getGamesList().remove(game);
        printOutput("Game " + getGameNumber(game) + " removed");
    }

    private void checkGameToStart(Game game) {
        if (isFullGame(game)) {
            broadcastGame(game, readyMessage(game), null);
            printOutput("Game " + getGameNumber(game) + " started");
        }
    }

    private int getGameNumber(Game game) {
        return getGamesList().indexOf(game) + 1;
    }

    private boolean isFullGame(Game game) {
        return game.getPlayers().size() == getPlayersPerGame();
    }

    private boolean isEmptyGame(Game game) {
        return game.getPlayers().size() == 0;
    }

    private boolean isGameAvailable(int boardSize) {
        return availableGame(boardSize) != null;
    }

    private Game availableGame(int boardSize) {
        for (Game game : getGamesList()) {
            if (!isFullGame(game)) {
                if (!getMatchBoardSize() || game.getBoard().getBoardSize() == boardSize) {
                    return game;
                }
            }
        }
        return null;
    }

    private int numberOfPlayers() {
        return getPlayersList().size();
    }

    private synchronized void listGame(Game game) {
        getGamesList().add(game);
        printOutput("Game " + getGameNumber(game) + " listed");
    }

    private Game createGame(int boardSize) {
        Game game = new Game(boardSize, getMovesPerTurn());
        listGame(game);
        return game;
    }

    private void deletePlayer(Player player) {
        removeListedPlayer(player);
        getGame(player).removePlayer(player);
    }

    private void deleteGame(Game game) {
        broadcastGame(game, "This game is removed", null);
        for (Player player : game.getPlayers()) {
            removeGamePlayer(game, player);
        }
        removeListedGame(game);
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
                broadcastPlayer(player, message);
            }
        }
    }

    private void broadcastPlayers(String message, Player excludedPlayer) {
        for (Player player : getPlayersList().keySet()) {
            if (!player.equals(excludedPlayer)) {
                broadcastPlayer(player, message);
            }
        }
    }

    private void broadcastClients(String string, ClientHandler excludedClientHandler) {
        for (ClientHandler clientHandler : getClientHandlers().keySet()) {
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
                        noCommand(clientHandler);
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
                else {
                    noCommand(clientHandler);
                }
            }
            else {
                noCommand(clientHandler);
            }
        }
        else if (isChatCommand(string)) {
            commandChat(clientHandler, chatArguments(string));
        }
        else {
            noCommand(clientHandler);
        }
    }

    private boolean playerNameExists(String name) {
        for (Player player : getPlayersList().keySet()) {
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
        for (Game listedGame : getGamesList()) {
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
            if (response.equals("")) {
                broadcastGame(game, createCommandPassed(player.getStone()), null);
                game.pass();
            }
            else {
                kickClient(getClientHandler(player), createCommandInvalid(player.getStone(), response));
            }
        }
    }

    private void commandTableFlip(Game game, Player player) {
        if (game.isValidTableflip(player.getStone())) {
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

    private void noCommand(ClientHandler clientHandler) {
        handleClientOutput(clientHandler, WARNING.toString() + SPACE + UNKNOWN_KEYWORD);
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
        this.serverLoop = false;
    }

    private void shutDown(Socket socket, ServerSocket serverSocket, String broadcastMessage) {
        broadcastClients(broadcastMessage, null);
        try {
            socket.close();
            serverSocket.close();
        }
        catch (IOException e) {
            printOutput(e.getMessage());
        }
    }
}


