package server;

import game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
import static helper.Strings.*;

import static java.lang.Thread.sleep;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Server {

    private static final int PORT = 2727;                   // Port for communication
    private static final int MAX_CLIENTS = 10;              // Maximum number of clients that can connect to the server

    private HashMap<ClientHandler, Date> clientHandlers = new HashMap<>();
    private HashMap<Player, ClientHandler> players = new HashMap<>();
    private List<Game> games = new ArrayList<>();
    private boolean serverLoop = true;
    private static final boolean MATCH_BOARDSIZE = false;   // FALSE: conform second player, TRUE: players matched on boardsize

    public static void main(String[] args) {
        new Server(PORT);
    }

    public Server(int port) {
        ServerSocket serverSocket = createServerSocket(port);
        Socket socket = null;
        while (serverLoop) {
            if (isClientHandlerAvailable()) {
                System.out.println("Socket available");
                socket = createSocket(serverSocket);
                ClientHandler clientHandler = new ClientHandler(this, socket);
                (new Thread(clientHandler, "ClientHandler")).start();
                listClientHandler(clientHandler);
            }
        }
        shutDown(socket, serverSocket, SERVER_SHUTDOWN.toString());
    }

    private boolean isClientHandlerAvailable() {
        return !(numberOfPlayers() == MAX_CLIENTS);
    }

    private ClientHandler getClientHandler(Player player) {
        ClientHandler clientHandler = null;
        for (Player listedPlayer : players.keySet()) {
            if (listedPlayer.equals(player)) {
                clientHandler = players.get(listedPlayer);
            }
        }
        return clientHandler;
    }

    private Player getPlayer(ClientHandler clientHandler) {                                 // Deze nakijken, loop door HashMap goed?
        Player player = null;
        for (Map.Entry<Player, ClientHandler> listedClientHandler : players.entrySet()) {
            if (clientHandler.equals(listedClientHandler)) {
                player = listedClientHandler.getKey();
            }
        }
        return player;
    }

    private boolean isPlayer(ClientHandler clientHandler) {
        return getPlayer(clientHandler) != null;
    }

    private ServerSocket createServerSocket(int port)  {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return serverSocket;
    }

    private Socket createSocket(ServerSocket serverSocket) {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return socket;
    }

    private synchronized void listClientHandler(ClientHandler clientHandler) {
        Date date = new Date();
        clientHandlers.put(clientHandler, date);
        System.out.println("server.ClientHandler listed " + date.toString());
    }

    private synchronized void removeListedClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("server.ClientHandler removed");
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        players.put(player, clientHandler);
        System.out.println(player.getName() + " listed");
    }

    private synchronized void removeListedPlayer(Player player) {
        players.remove(player);
        System.out.println(player.getName() + " removed");
    }

    private synchronized void removeListedGame(Game game) {
        games.remove(game);
        System.out.println("game.Game " + game.getGameNumber() + " removed");
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
                    System.out.println("game.Player added to existing game");
                }
                game.startGame();
                broadcastReadyCommand(game);
                System.out.println("game.Game started");
                return;
            }
        }
        Game newGame = createGame(boardsize);
        newGame.addPlayer(player);
        System.out.println("New game added to list");
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private int createGameNumber() {
        return games.size() + 1;
    }

    private synchronized void listGame(Game game) {
        games.add(game);
        System.out.println("game.Game " + game.getGameNumber() + " listed");
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
        System.out.println("New player: " + split[1] + " on board(" + split[2] +")");
        return player;
    }

    public void newPlayer(ClientHandler clientHandler, String string) {
        Player player = createPlayer(clientHandler, string);
        matchWithListedPlayer(player, Integer.parseInt(splitString(string)[2]));
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
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
        }
    }

    private void commandPass(ClientHandler clientHandler) {
        broadcastGame(getPlayer(clientHandler).getGame(), PASSED.toString() + SPACE + getPlayer(clientHandler).getStone());
    }

    private void commandTableflip(ClientHandler clientHandler) {
        broadcastGame(getPlayer(clientHandler).getGame(), TABLEFLIPPED.toString() + SPACE + getPlayer(clientHandler).getStone());
    }

    private void commandChat(ClientHandler clientHandler, String string) {
        String message = string.substring(splitString(string)[0].length());
        handleClientOutput(getClientHandler(getPlayer(clientHandler).getOpponent()), message);
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
                                                                                                    // keep game if another player is still in it?
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

    private void stopServer() {
        this.serverLoop = false;
    }

    private void shutDown(Socket socket, ServerSocket serverSocket, String broadcastMessage) {
        broadcastClients(broadcastMessage);
        try {
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}

























