package server;

import game.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
import static helper.Strings.*;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Server {

    private static final int MAX_CLIENTS = 10;              // Maximum number of clients that can connect to the server

    private HashMap<ClientHandler, Date> clientHandlers = new HashMap<>();
    private HashMap<Player, ClientHandler> players = new HashMap<>();
    private List<Game> games = new ArrayList<>();
    private boolean serverLoop = true;
    private static final boolean MATCH_BOARDSIZE = false;   // FALSE: conform second player, TRUE: players matched on boardsize

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        ServerSocket serverSocket = createServerSocket();
        while (serverSocket == null) {
            printOutput(NO_SOCKET_AT_PORT.toString());
            serverSocket = createServerSocket();
        }
        printOutput("Socket made");
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

    private int getPortNumber() {
        int port = -1;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter port number: ");
        String portString = "";
        try {
            portString = bufferedReader.readLine();
        } catch (IOException e) {
            printOutput(e.getMessage());
        }
        if (isInteger(portString)) {
            port = Integer.parseInt(portString);
        }
        return port;
    }

    private ServerSocket createServerSocket()  {
        int port = getPortNumber();
        ServerSocket serverSocket = null;
        if (port >= 0 && port <= 65535) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                printOutput(e.getMessage());
            }
        }
        return serverSocket;
    }

    private Socket createSocket(ServerSocket serverSocket) {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            printOutput(e.getMessage());
        }
        return socket;
    }

    private int createClientHandlerNumber() {
        return clientHandlers.size() + 1;
    }

    private boolean isClientHandlerAvailable() {
        return !(numberOfPlayers() == MAX_CLIENTS);
    }

    private ClientHandler getClientHandler(Player player) {
        return players.get(player);
    }

    private Player getPlayer(ClientHandler clientHandler) {
        Player player = null;
        for (Player listedPlayer : players.keySet()) {
            if (players.get(listedPlayer).equals(clientHandler)) {
                player = listedPlayer;
            }
        }
        return player;
    }

    private boolean isPlayer(ClientHandler clientHandler) {
        return getPlayer(clientHandler) != null;
    }

    private synchronized void listClientHandler(ClientHandler clientHandler) {
        Date date = new Date();
        clientHandlers.put(clientHandler, date);
        printOutput("ClientHandler listed " + date.toString());
    }

    private synchronized void removeListedClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        printOutput("ClientHandler removed");
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        players.put(player, clientHandler);
        printOutput(player.getName() + " listed");
    }

    private synchronized void removeListedPlayer(Player player) {
        players.remove(player);
        printOutput(player.getName() + " removed");
    }

    private synchronized void removeListedGame(Game game) {
        games.remove(game);
        printOutput("Game " + game.getGameNumber() + " removed");
    }

    private synchronized void matchWithListedPlayer(Player player, int boardsize) {
        for (Game game : games) {
            if (game.getPlayers().size() == 1) {
                if (MATCH_BOARDSIZE) {
                    if (game.getBoard().getSize() == boardsize) {
                        game.addPlayer(player);
                    }
                } else {
                    game.addPlayer(player);
                    printOutput("Player added to existing game");
                }
                game.startGame();
                broadcastReadyCommand(game);
                printOutput("Game started");
                return;
            }
        }
        Game newGame = createGame(boardsize);
        newGame.addPlayer(player);
        printOutput("New game added to list");
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private int createGameNumber() {
        return games.size() + 1;
    }

    private synchronized void listGame(Game game) {
        games.add(game);
        printOutput("Game " + game.getGameNumber() + " listed");
    }

    private Game createGame(int boardsize) {
        Game game = new Game(boardsize, createGameNumber());
        listGame(game);
        return game;
    }

    public void broadcastGame(Game game, String message) {
        for (Player player : game.getPlayers()) {
            handleClientOutput(getClientHandler(player), message);
        }
    }

    public void broadcastPlayers(String string) {
        for (ClientHandler clientHandler : players.values()) {
            handleClientOutput(clientHandler, string);
        }
    }

    private void broadcastClients(String string) {
        for (ClientHandler clientHandler : clientHandlers.keySet()) {
            handleClientOutput(clientHandler, string);
        }
    }

    public void broadcastReadyCommand(Game game) {
        for (Player player : game.getPlayers()) {
            String string = READY.toString() + SPACE + player.getStone().toString() + SPACE + player.getOpponent().getName() + SPACE + game.getBoard().getSize();
            handleClientOutput(getClientHandler(player), string);
        }
    }

    public Player createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[1]);
        listPlayer(player, clientHandler);
        handleClientOutput(clientHandler, WAITING.toString());
        printOutput("New player: " + split[1] + " on board(" + split[2] +")");
        return player;
    }

    public void newPlayer(ClientHandler clientHandler, String string) {
        Player player = createPlayer(clientHandler, string);
        matchWithListedPlayer(player, Integer.parseInt(splitString(string)[2]));
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
        printOutput(getPlayer(clientHandler).getName() + ": " + string);
        if (isValidCommand(GO, string)) {
            commandGo(clientHandler, string);
        } else if (isValidCommand(CANCEL, string)) {
            commandCancel(clientHandler);
        } else if (isValidCommand(MOVE, string)) {
            commandMove(clientHandler, string);
        } else if (isValidCommand(PASS, string)) {
            commandPass(clientHandler);
        } else if (isValidCommand(TABLEFLIP, string)) {
            commandTableflip(clientHandler);
        } else if (isValidCommand(CHAT, string)) {
            commandChat(clientHandler, string);
        } else {
            noCommand(clientHandler);
        }
    }

    private void commandGo(ClientHandler clientHandler, String string) {
        if (!isPlayer(clientHandler)) {
            newPlayer(clientHandler, string);
        } else {
            handleClientOutput(clientHandler, WARNING.toString() + SPACE + GAME_EXISTS);
        }
    }

    private void commandCancel(ClientHandler clientHandler) {
        deleteGame(getPlayer(clientHandler).getGame());
        deletePlayer(getPlayer(clientHandler));
    }

    private void commandMove(ClientHandler clientHandler, String string) {
        Player player = getPlayer(clientHandler);
        Game game = player.getGame();
        int x = Integer.parseInt(splitString(string)[1]);
        int y = Integer.parseInt(splitString(string)[2]);
        if (game.isValidMove(player, x, y)) {
            game.move(player, x, y);
            String message = VALID.toString() + SPACE + player.getStone() + SPACE + x + SPACE + y;
            broadcastGame(game, message);
        } else {
            kickClient(clientHandler);
        }
    }

    private void commandPass(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        Game game = player.getGame();
        if (game.isValidPass(player)) {
            broadcastGame(game, PASSED.toString() + SPACE + player.getStone());
        }
    }

    private void commandTableflip(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        Game game = player.getGame();
        if (game.isValidTableflip(player)) {
            broadcastGame(game, TABLEFLIPPED.toString() + SPACE + player.getStone());
//            broadcastGame(game, END.toString() + SPACE + blackScore + SPACE + whiteScore);
            deleteGame(game);
            deletePlayer(player);
        }
    }

    private void commandChat(ClientHandler clientHandler, String string) {
        String message = string.substring(splitString(string)[0].length());
        String sender = getPlayer(clientHandler).getName();
        String chatMessage = CHAT.toString() + SPACE + sender + ":" + SPACE + message;
        handleClientOutput(getClientHandler(getPlayer(clientHandler).getOpponent()), chatMessage);
    }

    private void noCommand(ClientHandler clientHandler) {
        handleClientOutput(clientHandler, WARNING.toString() + SPACE + UNKNOWN_KEYWORD);
    }

    private void deletePlayer(Player player) {
        removeListedPlayer(player);
        player.getGame().removePlayer(player);
    }

    private void deleteGame(Game game) {
        for (Player player : game.getPlayers()) {
            deletePlayer(player);
        }
        removeListedGame(game);
    }

    public void kickClient(ClientHandler clientHandler) {
        handleClientOutput(clientHandler, KICKED.toString());
        if (isPlayer(clientHandler)) {
            if (getPlayer(clientHandler).getGame().getPlayers().size() == 0) {
                                                                                                    // keep game if another player is still serverOutput it?
                deleteGame(getPlayer(clientHandler).getGame());
                                                                                                    // delete instance of game?
            } else {
                handleClientOutput(getClientHandler(getPlayer(clientHandler).getOpponent()), getPlayer(clientHandler).getName() + SPACE + IS_KICKED);
            }
            deletePlayer(getPlayer(clientHandler));
        }
        removeListedClientHandler(clientHandler);
        clientHandler.shutDown();
    }

    public void handleClientOutput(ClientHandler clientHandler, String string) {
        clientHandler.handleClientOutput(string);
    }

    public void printOutput(String string) {
        System.out.println(string);
    }

    private void stopServer() {
        this.serverLoop = false;
    }

    private void shutDown(Socket socket, ServerSocket serverSocket, String broadcastMessage) {
        broadcastClients(broadcastMessage);
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            printOutput(e.getMessage());
        }

    }

}

























