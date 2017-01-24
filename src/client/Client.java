package client;

import game.*;
import helper.Stone;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static helper.ComToolbox.*;
import static helper.Keyword.*;
import static helper.Stone.*;
import static helper.Strings.*;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Client {

    private Socket socket;
    private BufferedWriter serverInput;
    private Player player;
    private Game game;

    public Client() {
        InetAddress inetAddress = getInetAddress();
        int port = getPortNumber();
        handleConsoleOutput("Connecting to socket");
        this.socket = getSocket(inetAddress, port);
        handleConsoleOutput("Connected to socket");
        Thread socketReader = new Thread(new SocketReader(this.socket, this), "SocketReader");
        socketReader.start();
        this.serverInput = createSocketWriter(this.socket);
        this.player = createPlayer();
        int boardsize = requestBoardsize();
        handleServerInput(createCommandGo(getPlayer().getName(), boardsize));
    }

    private int requestBoardsize() {
        while (true) {
            String boardsizeString = getConsoleInput("Preferred board size (5 - 131, odd): ");
            if (isInteger(boardsizeString)) {
                if (isBoardsize(Integer.parseInt(boardsizeString))) {
                    return Integer.parseInt(boardsizeString);
                }
            }
            handleConsoleOutput("No valid boardsize");
        }
    }

    private Player getPlayer() {
        return this.player;
    }

    private Game getGame() {
        return this.game;
    }

    private Player createPlayer() {
        while (true) {
            String name = getConsoleInput("Name: ");
            if (name.length() > 0) {
                return new Player(name);
            }
            handleConsoleOutput("No valid name");
        }
    }

    private void createGame(int boardsize) {
        this.game = new Game(boardsize);
    }

    private void startNewGame() {
        while (true) {
            String newGameAnswer = getConsoleInput("Do you want another game (y/n): ");
            if (newGameAnswer.equals("y")) {
                createGame(requestBoardsize());
                break;
            } else if (newGameAnswer.equals("n")) {
                shutDown();
                break;
            }
        }
    }

    private Socket getSocket(InetAddress inetAddress, int port) {
        while (true) {
            try {
                return new Socket(inetAddress, port);
            } catch (IOException e) {
                handleConsoleOutput("No Socket available, retrying...");
            }
        }
    }

    private InetAddress getInetAddress() {
        String inetAddressString = getConsoleInput("Enter the IP address: ");
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(inetAddressString);
        } catch (UnknownHostException e) {
            handleConsoleOutput("No Internet Address");
        }
        return inetAddress;
    }

    private int getPortNumber() {
        int port = -1;
        String portString = getConsoleInput("Enter port number: ");
        if (isInteger(portString)) {
            if (Integer.parseInt(portString) >= 0 && Integer.parseInt(portString) <= 65535) {
                port = Integer.parseInt(portString);
            }
        }
        return port;
    }

    private String getConsoleInput(String question) {
        System.out.print(question);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String consoleInput = "";
        try {
            consoleInput = bufferedReader.readLine();
        } catch (IOException e) {
            handleConsoleOutput(e.getMessage());
        }
        return consoleInput;
    }

    private BufferedWriter createSocketWriter(Socket socket) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            handleConsoleOutput(e.getMessage());
        }
        return out;
    }

    protected void handleServerOutput(String string) {
        if (isValidCommand(WAITING, string)) {
            commandWaiting(string);
        } else if (isValidCommand(READY, string)) {
            commandReady(string);
        } else if (isValidCommand(VALID, string)) {
            commandValid(string);
        } else if (isValidCommand(INVALID, string)) {
            commandInvalid(string);
        } else if (isValidCommand(PASSED, string)) {
            commandPassed(string);
        } else if (isValidCommand(TABLEFLIPPED, string)) {
            commandTableflipped(string);
        } else if (isValidCommand(CHAT, string)) {
            commandChat(string);
        } else if (isValidCommand(WARNING, string)) {
            commandWarning(string);
        } else if (isValidCommand(END, string)) {
            commandEnd(string);
        } else {
            noCommand(string);
        }
    }

    private void commandWaiting(String string) {
        handleConsoleOutput(WAITING_FOR_OPPONENT.toString());
    }

    private void commandReady(String string) {
        String[] split = splitString(string);
        getPlayer().setStone(Stone.valueOf(split[1]));
        Player opponent = new Player(split[2]);
        createGame(Integer.parseInt(split[3]));
        getGame().addPlayer(getPlayer());
        getGame().addPlayer(opponent);
    }

    private void commandValid(String string) {
        String[] split = splitString(string);
        String response = getGame().checkMoveValidity(Stone.valueOf(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        if (response.equals(VALID.toString())) {
            getGame().move(Stone.valueOf(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            handleConsoleInput("Move by " + getGame().getPlayerByStone(Stone.valueOf(split[1])).getName() + ": " + split[2] + ", " + split[3]);
        } else {
            handleConsoleOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandInvalid(String string) {
        String[] split = splitString(string);
        Player opponent = getGame().getPlayerByStone(Stone.valueOf(split[1]));
        handleConsoleOutput("Invalid move by " + opponent.getName() + ": " + split[2]);
    }

    private void commandPassed(String string) {
        String[] split = splitString(string);
        if (getGame().isValidPass(Stone.valueOf(split[1]))) {
            getGame().pass();
            handleConsoleOutput(getGame().getPlayerByStone(Stone.valueOf(split[1])).getName() + " passed");
        } else {
            handleConsoleOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandTableflipped(String string) {
        String[] split = splitString(string);
        if (getGame().isValidTableflip(Stone.valueOf(split[1]))) {
            getGame().tableflip();
            handleConsoleOutput(getGame().getPlayerByStone(Stone.valueOf(split[1])).getName() + " tableflipped");
        } else {
            handleConsoleOutput(SERVER_CLIENT_MISMATCH.toString());
        }
    }

    private void commandChat(String string) {
        String message = string.substring(getKeyword(string).toString().length());
        handleConsoleOutput("Chat message: " + message);
    }

    private void commandWarning(String string) {
        String message = string.substring(getKeyword(string).toString().length());
        handleConsoleOutput("Warning: " + message);
    }

    private void commandEnd(String string) {
        String[] split = splitString(string);
        Stone winner = null;
        if (Integer.parseInt(split[1]) > Integer.parseInt(split[1])) {
            winner = BLACK;
        } else if (Integer.parseInt(split[1]) > Integer.parseInt(split[1])) {
            winner = WHITE;
        }
        if (winner == null) {
            handleConsoleOutput("The game ended with a draw");
        } else if (getPlayer().getStone() == winner) {
            handleConsoleOutput("You won with " + split[1] + " to " + split[2] + "!");
        } else {
            handleConsoleOutput("You lost with " + split[1] + " to " + split[2]);
        }
        startNewGame();
    }

    private void noCommand(String string) {

    }

    protected void handleConsoleInput(String string) {
        //handleServerInput(string);
    }

    private void handleServerInput(String string) {
        try {
            this.serverInput.write(string);
            this.serverInput.newLine();
            this.serverInput.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void shutDown() {

        try {
            this.serverInput.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void handleConsoleOutput(String string) {
        System.out.println(string);
    }

}
