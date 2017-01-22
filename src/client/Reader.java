package client;

import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class Reader implements Runnable {

    private Socket socket = null;
    protected BufferedReader in;
    //protected BufferedWriter out;

    public Reader(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            //this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            //shutDown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void shutDown() {
        try {
            System.out.println("Stopping reader...");
            this.in.close();
            //this.out.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
