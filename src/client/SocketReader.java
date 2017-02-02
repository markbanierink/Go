package client;

import java.io.*;
import java.net.Socket;

/**
 * SocketReader reads contains the BufferedReader for the socket.
 *
 * @author Mark Banierink
 */
public class SocketReader implements Runnable {

    private Socket socket = null;
    private Client client = null;
    private BufferedReader serverOutput;
    private boolean stop;

    public SocketReader(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleServerOutput(String string) {
        client.handleServerOutput(string);
    }

    protected void setStop() {
        stop = true;
    }

    public void run() {
        String line;
        try {
            while ((line = serverOutput.readLine()) != null && !stop) {
                handleServerOutput(line);
            }
            shutDown();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void shutDown() {
        try {
            System.out.println("Stopping SocketReader");
            serverOutput.close();
            socket.close();
            client.connectionLost();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
