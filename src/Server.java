import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
import static java.lang.Thread.sleep;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Server {

    private static final int PORT = 2727;                   // Port for communication
    private static final int MAX_CLIENTS = 10;              // Maximum number of clients that can connect to the server
    private static final long SERVER_SLEEP_TIME = 30000;    // Server waiting time in case max clients is reached

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
            System.out.println("Socket available");
            socket = createSocket(serverSocket);
            ClientHandler clientHandler = new ClientHandler(this, socket);
            (new Thread(clientHandler, "ClientHandler")).start();
            listClientHandler(clientHandler);
            checkServerStatus();
        }
        shutDown(socket, serverSocket, "Server shuts down");
    }

    private void checkServerStatus() {
        if (numberOfPlayers() == MAX_CLIENTS) {
            try {
                sleep(SERVER_SLEEP_TIME);
                checkServerStatus();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        System.out.println("ClientHandler listed " + date.toString());
    }

    private synchronized void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("ClientHandler removed");
    }

    private synchronized void listPlayer(Player player, ClientHandler clientHandler) {
        players.put(player, clientHandler);
        System.out.println(player.getName() + " listed");
    }

    private synchronized void removePlayer(Player player) {
        players.remove(player);
        System.out.println(player.getName() + " removed");
    }

    private synchronized void removeGame(Game game) {
        games.remove(game);
        System.out.println("Game " + game.getGameNumber() + " removed");
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
                    System.out.println("Player added to existing game");
                }
                game.startGame();
                broadcastReadyCommand(game);
                System.out.println("Game started");
                return;
            }
        }
        Game newGame = createGame(boardsize);
        newGame.addPlayer(player);
        System.out.println("New game added to list");
    }

    public void broadcastReadyCommand(Game game) {
        for (Player player : game.getPlayers()) {
            String string = READY + " " + player.getStone().toString() + " " + player.getOpponent().getName() + " " + game.getBoard().getSize();
            handleClientOutput(getClientHandler(player), string);
        }
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private int createGameNumber() {
        return games.size() + 1;
    }

    private synchronized void listGame(Game game) {
        games.add(game);
        System.out.println("Game " + game.getGameNumber() + " listed");
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

    public void createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[1]);
        listPlayer(player, clientHandler);
        handleClientOutput(clientHandler, WAITING.toString());
        System.out.println("New player: " + split[1] + " on board(" + split[2] +")");
        matchWithListedPlayer(player, Integer.parseInt(split[2]));
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
        if (isServerCommand(string)) {
            if (isGo(string)) {
                if (isPlayer(clientHandler)) {
                    handleClientOutput(clientHandler, WARNING + ": You already started a game..");
                } else {
                    createPlayer(clientHandler, string);
                }
            } else if (isCancel(string)) {
                deletePlayer(getPlayer(clientHandler));

            } else {
                getPlayer(clientHandler).getGame().handlePlayerInput(getPlayer(clientHandler), string);
            }
        } else {
            handleClientOutput(clientHandler, WARNING + ": Unknown keyword");
        }
    }

    private void deletePlayer(Player player) {
        removePlayer(player);
        player.getGame().removePlayer(player);
    }

    private void deleteGame(Game game) {

    }

    public void kickClient(ClientHandler clientHandler) {
        handleClientOutput(clientHandler, "You are being kicked");
        if (isPlayer(clientHandler)) {
            if (getPlayer(clientHandler).getGame().getPlayers().size() == 0) {
                // keep game if another player is still in it?
                deleteGame(getPlayer(clientHandler).getGame());
                // delete instance of game?
            } else {
                handleClientOutput(getClientHandler(getPlayer(clientHandler).getOpponent()), getPlayer(clientHandler).getName() + " is being kicked");
            }
            deletePlayer(getPlayer(clientHandler));
        }
        removeClientHandler(clientHandler);
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

























