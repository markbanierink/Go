package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 17-1-2017.
 */
public class ClientHandler implements Runnable {

    private Server server;
    private Socket socket;
    private BufferedReader clientInput;
    private BufferedWriter clientOutput;

    ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.clientInput = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.clientOutput = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
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
            while ((line = clientInput.readLine()) != null) {
                handleClientInput(line);
            }
            shutDown();
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

    public void writeString(String string) {
        try {
            this.clientOutput.write(string);
            this.clientOutput.newLine();
            this.clientOutput.flush();
        } catch (IOException e) {
            getServer().printOutput(e.getMessage());
        }
    }

//    public String readString(String text) {
//        System.clientOutput.print(text);
//        String line = null;
//        try {
//            BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.clientInput));
//            line = clientInput.readLine();
//        } catch (IOException e) {
//            System.clientOutput.println(e.getMessage());
//        }
//        return (line == null) ? "" : line;
//    }

    public void shutDown() {
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