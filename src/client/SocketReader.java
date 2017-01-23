package client;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class SocketReader implements Runnable {

    private Socket socket = null;
    protected BufferedReader serverOutput;

    public SocketReader(Socket socket) {
        this.socket = socket;
        try {
            this.serverOutput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        String line;
        try {
            while ((line = serverOutput.readLine()) != null) {
                System.out.println(line);
            }
            shutDown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void shutDown() {
        try {
            System.out.println("Stopping SocketReader");
            this.serverOutput.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
