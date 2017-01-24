package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 17-1-2017.
 * The ClientHandler can run as a separate thread. It manages the incoming and outgoing buffer stream from Server to Client.
 */
public class ClientHandler implements Runnable {

    private Server server;
    private Socket socket;
    private BufferedReader clientInput;
    private BufferedWriter clientOutput;

    /**
     * Constructor of the ClientHandler
     * @param server Server object
     * @param socket Socket for the buffer stream communication
     */
    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.clientInput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.clientOutput = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

    private Server getServer() {
        return this.server;
    }

    private void handleClientInput(String string) {
        getServer().handleClientInput(this, string);
    }

    protected void handleClientOutput(String string) {
        writeString(string);
    }

    /**
     * Run method that is started as soon as ClientHandler is started in a separate thread. It reads the client input
     * and passes this through to the server
     */
    public void run() {
        String line;
        try {
            while ((line = clientInput.readLine()) != null) {
                handleClientInput(line);
            }
            shutDown();
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

    private void writeString(String string) {
        try {
            this.clientOutput.write(string);
            this.clientOutput.newLine();
            this.clientOutput.flush();
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

    protected void shutDown() {
        try {
            getServer().printOutput("ClientWriter closed");
            this.clientInput.close();
            this.clientOutput.close();
            this.socket.close();
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

}