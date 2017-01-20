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
        for (Map.Entry<Player, ClientHandler> listedPlayer : players.entrySet()) {
            if (player.equals(listedPlayer)) {
                clientHandler = listedPlayer.getValue();
            }
        }
        return clientHandler;
    }

    private Player getPlayer(ClientHandler clientHandler) {
        Player player = null;
        for (Map.Entry<Player, ClientHandler> listedClientHandler : players.entrySet()) {
            if (clientHandler.equals(listedClientHandler)) {
                player = listedClientHandler.getKey();
            }
        }
        return player;
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

    private synchronized void listGame(Game game) {
        games.add(game);
        System.out.println("Game " + game.getGameNumber() + " listed");
    }

    private synchronized void removeGame(Game game) {
        games.remove(game);
        System.out.println("Game " + game.getGameNumber() + " removed");
    }

    private synchronized void matchWithListedPlayer(Player player) {
        for (Player listedPlayer : players.keySet()) {
            if (listedPlayer.getGame() == null && !listedPlayer.equals(player)) {
                if (!MATCH_BOARDSIZE) {
                    player.setBoardsize(listedPlayer.getBoardsize());   // set boardsize of second player to match the first
                }
                if (player.getBoardsize() == listedPlayer.getBoardsize()) {
                    createGame(listedPlayer, player, listedPlayer.getBoardsize());
                    return;
                }
            } else {
                System.out.println("No match found");
            }
        }
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private int createGameNumber() {
        return games.size() + 1;
    }

    private void createGame(Player player1, Player player2, int boardsize) {
        Game game = new Game(player1, player2, boardsize, createGameNumber());
        listGame(game);
        player1.setGame(game);
        player2.setGame(game);
        System.out.println("Created game on board(" + player1.getBoardsize() + ") with " + player1.getName() + " vs " + player2.getName());
    }

    private void broadcastClients(String string) {
        for (ClientHandler clientHandler : clientHandlers.keySet()) {
            handleClientOutput(clientHandler, string);
        }
    }

    public void broadcastPlayers(String string) {
        for (ClientHandler clientHandler : players.values()) {
            handleClientOutput(clientHandler, string);
        }
    }

    public void createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[1], Integer.parseInt(split[2]));
        listPlayer(player, clientHandler);
        handleClientOutput(clientHandler, WAITING.toString());
        matchWithListedPlayer(player);
        System.out.println("New player: " + split[1] + " on board(" + split[2] +")");
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
        if (isServerCommand(string)) {
            if (isGo(string)) {
                createPlayer(clientHandler, string);
            } else {
                getPlayer(clientHandler).getGame().handlePlayerInput(getPlayer(clientHandler), string);
            }
        } else {
            handleClientOutput(clientHandler, WARNING + " unknown keyword");
        }
    }

    public void kickClient(ClientHandler clientHandler) {
        removeClientHandler(clientHandler);
        handleClientOutput(clientHandler, "You are being kicked");
        if (clientHandler.hasGame()) {
            removeGame(clientHandler.getPlayer().getGame());
            removePlayer(clientHandler.getPlayer());
            if (clientHandler.getGame().getPlayers().size() > 1) {
                handleClientOutput(getClientHandler(clientHandler.getPlayer().getOpponent()), clientHandler.getPlayer().getName() + " is being kicked");
            }
        }
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

























