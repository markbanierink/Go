package server;

import java.io.*;
import java.net.Socket;

/**
 * The ClientHandler can run as a separate thread. It manages the incoming and outgoing buffer stream from Server to Client.
 *
 * @author Mark Banierink
 */
public class ClientHandler implements Runnable {

    private Server server;
    private Socket socket;
    private BufferedReader clientInput;
    private BufferedWriter clientOutput;
    private boolean stop;

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
        }
        catch (IOException e) {
            server.printOutput(e.getMessage());
        }
    }

    private void handleClientInput(String string) {
        System.out.println(string);
        server.handleClientInput(this, string);
    }

    protected void handleClientOutput(String string) {
        System.out.println(string);
        writeString(string);
    }

    public Socket getSocket() {
        return socket;
    }

    protected void setStop() {
        stop = true;
    }

    /**
     * Run method that is started as soon as ClientHandler is started in a separate thread. It reads the client input
     * and passes this through to the server
     */
    public void run() {
        String line;
        try {
            while ((line = clientInput.readLine()) != null && !stop) {
                handleClientInput(line);
            }
            shutDown();
        }
        catch (IOException e) {
            server.printOutput(e.getMessage() + ": Connection with Client was lost");
            server.removeClientHandler(this);
        }
    }

    private void writeString(String string) {
        try {
            this.clientOutput.write(string);
            this.clientOutput.newLine();
            this.clientOutput.flush();
        }
        catch (IOException e) {
            server.printOutput(e.getMessage());
        }
    }

    protected void shutDown() {
        try {
            server.printOutput("ClientWriter closed");
            this.clientInput.close();
            this.clientOutput.close();
            this.socket.close();
        }
        catch (IOException e) {
            server.printOutput(e.getMessage());
        }
    }
}