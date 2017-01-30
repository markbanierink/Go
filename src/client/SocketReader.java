package client;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 *
 * @author Mark Banierink
 */
public class SocketReader implements Runnable {

    private Socket socket = null;
    private Client client = null;
    private BufferedReader serverOutput;

    public SocketReader(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            this.serverOutput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Client getClient() {
        return this.client;
    }

    private void handleServerOutput(String string) {
        getClient().handleServerOutput(string);
    }

    public void run() {
        String line;
        try {
            while ((line = serverOutput.readLine()) != null) {
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
            this.serverOutput.close();
            this.socket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
