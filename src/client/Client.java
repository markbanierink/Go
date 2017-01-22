package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Client implements Runnable {

    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 2727;
    private InetAddress inetAddress = null;
    private Socket socket = null;
    private BufferedReader in;
    private BufferedWriter out;

    public static void main(String[] args) {
        new Client(SERVER_ADDRESS, SERVER_PORT).run();
    }

    public Client(String serverAddress, int serverPort) {
        this.inetAddress = getServerAddress(serverAddress);
        this.socket = getSocket(inetAddress, serverPort);
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("client.Client started");
    }

    public void handleServerInput(String string) {
        handleServerOutput(string);
    }

    public void handleServerOutput(String string) {
        writeString(string);
    }

    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null) {
                handleServerInput(line);
            }
            shutDown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private InetAddress getServerAddress(String serverAddress) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            System.out.println("No Internet Address");
            e.printStackTrace();
        }
        return inetAddress;
    }

    private Socket getSocket(InetAddress inetAddress, int port) {
        Socket socket = null;
        try {
            socket = new Socket(inetAddress, port);
        } catch (IOException e) {
            System.out.println("No Socket");
            e.printStackTrace();
        }
        return socket;
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
