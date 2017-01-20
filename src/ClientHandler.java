import helper.Stone;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 17-1-2017.
 */
public class ClientHandler implements Runnable {

    private Server server;
    private Socket socket;
    private Game game = null;
    private Player player = null;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Server getServer() {
        return this.server;
    }

    public void handleClientInput(String string) {
        getServer().handleClientInput(this, string);
    }

    public void handleClientOutput(String string) {
        writeString(string);
    }

    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                handleClientInput(line);
            }
            shutDown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeString(String string) {
        try {
            this.out.write(string);
            this.out.newLine();
            this.out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String readString(String text) {
        System.out.print(text);
        String line = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            line = in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return (line == null) ? "" : line;
    }

    public void shutDown() {
        try {
            System.out.println("Stopping client writer...");
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}