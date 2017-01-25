package server;

import game.*;
import helper.Stone;

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
 * This class handles the server-side of the game.
 * No arguments are required.
 */
public class Server {

    private static final int MAX_CLIENTS = 10;              // Maximum number of clients that can connect to the server

    private HashMap<ClientHandler, Date> clientHandlers = new HashMap<>();
    private HashMap<Player, ClientHandler> playersList = new HashMap<>();
    private List<Game> gamesList = new ArrayList<>();
    private boolean serverLoop = true;

    private boolean matchBoardsize;
    private int playersPerGame = 0;

    public static void main(String[] args) {
        new Server();
    }

    /**
     * Constructor of Server. No input arguments are required.
     */
    public Server() {
        printOutput("Starting Server");
        ServerSocket serverSocket = createServerSocket();
        printOutput("ServerSocket made");
        setMatchBoardsize();
        setPlayersPerGame();
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

    private String getConsoleInput(String question) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(question);
        String consoleInput = "";
        try {
            consoleInput = bufferedReader.readLine();
        } catch (IOException e) {
            printOutput(e.getMessage());
        }
        return consoleInput;
    }

    private boolean getMatchBoardsize() {
        return this.matchBoardsize;
    }

    private int getPlayersPerGame() {
        return this.playersPerGame;
    }

    private void setMatchBoardsize() {
        boolean answered = false;
        while (!answered) {
            String matchString = getConsoleInput("Match players on board size (y/n): ");
            if (matchString.equals("y")) {
                this.matchBoardsize = true;
                answered = true;
            } else if (matchString.equals("n")) {
                this.matchBoardsize = false;
                answered = true;
            }
        }
    }

    private void setPlayersPerGame() {
        while (getPlayersPerGame() == 0) {
            String numberString = getConsoleInput("Number of players per game: ");
            if (isInteger(numberString)) {
                int number = Integer.parseInt(numberString);
                if (number > 1 && number <= maxPlayersPerGame()) {
                    this.playersPerGame = number;
                }
            }
        }
    }

    private int maxPlayersPerGame() {
        return Stone.values().length - 1;
    }

    private int getPortNumber() {
        while (true) {
            String portString = getConsoleInput("Enter port number: ");
            if (isInteger(portString)) {
                if (Integer.parseInt(portString) >= 0 && Integer.parseInt(portString) <= 65535) {
                    return Integer.parseInt(portString);
                }
            }
            printOutput(INVALID_PORT_NUMBER.toString());
        }
    }

    private ServerSocket createServerSocket() {
        int port = getPortNumber();
        while (true) {
            try {
                return new ServerSocket(port);
            } catch (IOException e) {
                printOutput(SERVER_SOCKET_NOT_POSSIBLE.toString());
                printOutput(e.getMessage());
            }
        }
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

    private boolean isPlayer(ClientHandler clientHandler) {
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

    private synchronized void removeListedGame(Game game) {
        getGamesList().remove(game);
        printOutput("Game " + getGamesList().indexOf(game) + " removed");
    }

    private void matchWithListedPlayer(Player player, int boardSize) {
        boolean match = false;
        for (Game game : getGamesList()) {
            if (game.getPlayers().size() == 1 && game.getPlayers().size() < game.maxPlayers()) {
                if (getMatchBoardsize() && game.getBoard().getBoardSize() == boardSize) {
                    match = true;
                } else if (!getMatchBoardsize()) {
                    match = true;
                }
            }
            if (match) {
                game.addPlayer(player);
                printOutput("Player added to Game " + gamesList.indexOf(game));
                game.startGame();
                broadcastReadyCommand(game);
                printOutput("Game " + gamesList.indexOf(game) + " started");
                return;
            }
        }
        Game newGame = createGame(boardSize);
        newGame.addPlayer(player);
        printOutput("New Game " + gamesList.indexOf(newGame) + " added to list");
    }

    private int numberOfPlayers() {
        return playersList.size();
    }

    private synchronized void listGame(Game game) {
        getGamesList().add(game);
        printOutput("Game " + gamesList.indexOf(game) + " listed");
    }

    private Game createGame(int boardsize) {
        Game game = new Game(boardsize);
        listGame(game);
        return game;
    }

    private void broadcastGame(Game game, String message) {
        for (Player player : game.getPlayers()) {
            handleClientOutput(getClientHandler(player), message);
        }
    }

    private void broadcastPlayers(String string) {
        for (ClientHandler clientHandler : playersList.values()) {
            handleClientOutput(clientHandler, string);
        }
    }

    private void broadcastClients(String string) {
        for (ClientHandler clientHandler : clientHandlers.keySet()) {
            handleClientOutput(clientHandler, string);
        }
    }

    private void broadcastReadyCommand(Game game) {
        for (Player player : game.getPlayers()) {
            List<Player> opponents = game.getOpponents(player);
            if (opponents.size() == 1) {
                String string = READY.toString() + SPACE + player.getStone().toString().toLowerCase() + SPACE + opponents.get(0).getName() + SPACE + game.getBoard().getBoardSize();
                handleClientOutput(getClientHandler(player), string);
            } else {
                printOutput("Multiple opponents not yet implemented!");
            }
        }
    }

    private Player createPlayer(ClientHandler clientHandler, String string) {
        String[] split = splitString(string);
        Player player = new Player(split[1]);
        listPlayer(player, clientHandler);
        handleClientOutput(clientHandler, WAITING.toString());
        printOutput("New player: " + split[1] + " on board(" + split[2] +")");
        return player;
    }

    private void newPlayer(ClientHandler clientHandler, String string) {
        Player player = createPlayer(clientHandler, string);
        matchWithListedPlayer(player, Integer.parseInt(splitString(string)[2]));
    }

    private Game getPlayerGame(Player player) {
        for (Game listedGame : getGamesList()) {
            for (Player listedPlayer : listedGame.getPlayers()) {
                if (player.equals(listedPlayer)) {
                    return listedGame;
                }
            }
        }
        return null;
    }

    protected void handleClientInput(ClientHandler clientHandler, String string) {
        String name = "[New Client]";
        if (getPlayer(clientHandler) != null) {
            name = getPlayer(clientHandler).getName();
        }
        printOutput(name + ": " + string);
        if (getPlayer(clientHandler) == null) {
            if (isValidCommand(GO, string)) {
                commandGo(clientHandler, string);
            } else {
                noCommand(clientHandler);
            }
        } else {
            if (isValidCommand(CANCEL, string)) {
                Game game = getPlayerGame(getPlayer(clientHandler));
                if (game != null && (game.getPlayers().size() == 1)) {
                    commandCancel(clientHandler);
                } else {
                    noCommand(clientHandler);
                }
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
    }

    private void commandGo(ClientHandler clientHandler, String string) {
        if (!isPlayer(clientHandler)) {
            newPlayer(clientHandler, string);
        } else {
            handleClientOutput(clientHandler, WARNING.toString() + SPACE + GAME_EXISTS);
        }
    }

    private void commandCancel(ClientHandler clientHandler) {
        deleteGame(getGame(getPlayer(clientHandler)));
        deletePlayer(getPlayer(clientHandler));
    }

    private Game getGame(Player player) {
        Game game = null;
        for (Game listedGame : getGamesList()) {
            for (Player listedPlayer : listedGame.getPlayers()) {
                if (listedPlayer.equals(player)) {
                    game = listedGame;
                }
            }
        }
        return game;
    }

    private void commandMove(ClientHandler clientHandler, String string) {
        Player player = getPlayer(clientHandler);
        Game game = getGame(player);
        int x = Integer.parseInt(splitString(string)[1]);
        int y = Integer.parseInt(splitString(string)[2]);
        String response = game.checkMoveValidity(player.getStone(), x, y);
        if (response.equals(VALID.toString())) {
            game.move(player.getStone(), x, y);
            String message = SPACE + player.getStone().toString() + SPACE + x + SPACE + y;
            broadcastGame(game, VALID + message.toLowerCase());
        } else {
            String reason = INVALID.toString() + SPACE + player.getStone().toString().toLowerCase() + response.toLowerCase();
            kickClient(clientHandler, reason);
        }
    }

    private void commandPass(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        Game game = getGame(player);
        if (game.isValidPass(player.getStone())) {
            String response = game.pass();
            if (response.equals("")) {
                String message = SPACE + player.getStone().toString();
                broadcastGame(game, PASSED + message.toLowerCase());
            } else {
                broadcastGame(game, response);
                deleteGame(game);
            }

        }
    }

    private void commandTableflip(ClientHandler clientHandler) {
        Player player = getPlayer(clientHandler);
        Game game = getGame(player);
        if (game.isValidTableflip(player.getStone())) {
            game.tableflip();
            String message = SPACE + player.getStone().toString();
            broadcastGame(game, TABLEFLIPPED + message.toLowerCase());
            //broadcastGame(game, END.toString() + SPACE + blackScore + SPACE + whiteScore);        End of game should be reported from Game
            deleteGame(game);
            deletePlayer(player);
        }
    }

    private void commandChat(ClientHandler clientHandler, String string) {
        Player player = getPlayer(clientHandler);
        String message = string.substring(splitString(string)[0].length());
        String sender = player.getName();
        String chatMessage = CHAT.toString() + SPACE + sender + ":" + SPACE + message;
        handleClientOutput(getClientHandler(getGame(player).getOpponents(player).get(0)), chatMessage);
    }

    private void noCommand(ClientHandler clientHandler) {
        handleClientOutput(clientHandler, WARNING.toString() + SPACE + UNKNOWN_KEYWORD);
    }

    private void deletePlayer(Player player) {
        removeListedPlayer(player);
        getGame(player).removePlayer(player);
    }

    private void deleteGame(Game game) {
        for (Player player : game.getPlayers()) {
            deletePlayer(player);
        }
        removeListedGame(game);
    }

    private void kickClient(ClientHandler clientHandler, String reason) {
        handleClientOutput(clientHandler, reason);
        handleClientOutput(clientHandler, CHAT.toString() + SPACE + SERVER + ":" + SPACE + KICKED);
        if (isPlayer(clientHandler)) {
            Player player = getPlayer(clientHandler);
            if (getGame(getPlayer(clientHandler)).getPlayers().size() == 0) {
                                                                                                    // keep game if another player is still in it?
                deleteGame(getGame(getPlayer(clientHandler)));
                                                                                                    // delete instance of game?
            } else {
                handleClientOutput(getClientHandler(getGame(player).getOpponents(player).get(0)), getPlayer(clientHandler).getName() + SPACE + IS_KICKED);
            }
            deletePlayer(getPlayer(clientHandler));
        }
        removeListedClientHandler(clientHandler);
        clientHandler.shutDown();
    }

    private void handleClientOutput(ClientHandler clientHandler, String string) {
        clientHandler.handleClientOutput(string);
    }

    protected void printOutput(String string) {
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


