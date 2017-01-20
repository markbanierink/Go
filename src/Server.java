import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Server {

    private static final int PORT = 2727;               // Port for communication
    private static final int MAX_CLIENTS = 10;          // Maximum number of clients that can connect to the server

    private List<ClientHandler> clientHandlers = new ArrayList<>();;
    private List<Player> players = new ArrayList<>();
    private List<Game> games = new ArrayList<>();
    private boolean serverOpen = true;
    private static final boolean MATCH_BOARDSIZE = false;

    public static void main(String[] args) {
        new Server(PORT);
    }

    public Server(int port) {
        ServerSocket serverSocket = createServerSocket(port);
        Socket socket = null;
        while (numberOfPlayers() < MAX_CLIENTS && serverOpen) {
            System.out.println("Socket available");
            socket = createSocket(serverSocket);
            ClientHandler clientHandler = new ClientHandler(this, socket);
            (new Thread(clientHandler, "ClientHandler")).start();
            listClientHandler(clientHandler);
        }
        shutDown(socket, "Server shuts down");
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

    private void stopServerOpen() {
        this.serverOpen = false;
    }

    private synchronized void listClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
        System.out.println("ClientHandler listed");
    }

    private synchronized void listPlayer(Player player) {
        players.add(player);
        System.out.println("Player listed");
    }

    private synchronized void listGame(Game game) {
        games.add(game);
        System.out.println("Game listed");
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private synchronized void matchWithListedPlayer(Player player) {
        for (Player listedPlayer : players) {
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

    private void createGame(Player player1, Player player2, int boardsize) {
        Game game = new Game(player1, player2, boardsize);
        listGame(game);
        player1.setGame(game);
        player2.setGame(game);
        System.out.println("Created game on board(" + player1.getBoardsize() + ") with " + player1.getName() + " vs " + player2.getName());
    }

    private void broadcastClients(String string) {
        for (ClientHandler clientHandler : clientHandlers) {
            handleClientOutput(clientHandler, string);
        }
    }

    public void broadcastPlayers(String string) {
        for (Player player : players) {
            handleClientOutput(player.getClientHandler(), string);
        }
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
        String[] command = CommunicationToolbox.string2Command(string);
        if (CommunicationToolbox.commandGoIsValid(string)) {
            Player player = new Player(command[1], Integer.parseInt(command[2]), clientHandler);
            System.out.println("New player: " + command[1] + " on board(" + command[2] +")");
            listPlayer(player);
            handleClientOutput(player.getClientHandler(), Keyword.WAITING.toString());
            matchWithListedPlayer(player);
        }
    }

    public void handleClientOutput(ClientHandler clientHandler, String string) {
        clientHandler.handleClientOutput(string);
    }

    private void shutDown(Socket socket, String broadcastMessage) {
        broadcastClients(broadcastMessage);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

























