package client;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class ConsoleReader implements Runnable {

    private Client client = null;

    public ConsoleReader(Client client) {
        this.client = client;
    }

    public void run() {
        String line;
        while ((line = readString("")) != null) {
            getClient().handleConsoleInput(line);
        }
        System.out.println("ConsoleReader finished");
    }

    private Client getClient() {
        return this.client;
    }

    public String readString(String tekst) {
        System.out.print(tekst);
        String line = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            line = in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return (line == null) ? "" : line;
    }

}
