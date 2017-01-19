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

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private List<Player> players = null;
    private List<Game> games = null;
    private boolean serverOpen = true;

    public static void main(String[] args) {
        new Server(PORT);
    }

    public Server(int port) {
        players = new ArrayList<>();
        games = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.getStackTrace();
        }
        try {
            while (numberOfPlayers() < MAX_CLIENTS && serverOpen) {
                System.out.println("Socket available");
                socket = serverSocket.accept();
                System.out.println("Socket accepted");
                ClientHandler clientHandler = new ClientHandler(this, socket);
                Thread clientThread = new Thread(clientHandler, "Client");
                clientThread.start();
                //listPlayer(clientHandler);
            }
            System.out.println("No sockets available");
            //socket.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void stopServerOpen() {
        this.serverOpen = false;
    }

    private void listPlayer(Player player) {
        players.add(player);
        System.out.println("Client listed");
    }

    private int numberOfPlayers() {
        return players.size();
    }

    private void matchWithListedPlayer(Player player) {
        for (Player listedPlayer : players) {
            if (listedPlayer.getGame() == null) {
                createGame(listedPlayer, player, listedPlayer.getBoardsize());
                player.setBoardsize(listedPlayer.getBoardsize());
                return;
            }
        }
    }

    private void createGame(Player player1, Player player2, int boardsize) {
        Game game = new Game(player1, player2, boardsize);
        games.add(game);
        player1.setGame(game);
        player2.setGame(game);
        System.out.println("Created board(" + player1.getBoardsize() + ") with " + player1.getName() + " vs " + player2.getName());
    }

    public void broadcast(String string) {
        for (Player player : players) {
            handlePlayerOutput(player, string);
        }
    }

    public void handleClientInput(ClientHandler clientHandler, String string) {
        System.out.println(string);
        String[] command = CommunicationToolbox.string2Command(string);
        if (command[0] != null && command[0].equals(Keyword.GO.toString())) {
            Player newPlayer = new Player(command[1], Integer.parseInt(command[2]), clientHandler);
            matchWithListedPlayer(newPlayer);
        }
    }

    public void handleClientInput(Player player, String string) {
        System.out.println(string);
        String[] command = CommunicationToolbox.string2Command(string);
        if (player.hasGame()) {
            player.getGame().handleClientInput(string);
        }
    }

    public void handlePlayerOutput(Player player, String string) {
        player.handleClientOutput(string);
    }

//    private void handleClientInput(String string) {
//        Keyword keyword = CommunicationToolbox.getKeyword(string);
//        if (keyword == Keyword.GO) {
//            // int numArguments = CommunicationToolbox.numArguments(string);
//        }
//    }

}

























