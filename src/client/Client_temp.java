package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Client_temp {

    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 2727;

    private InetAddress inetAddress = null;
    private Socket socket = null;

    public static void main(String[] args) {
        new Client_temp(SERVER_ADDRESS, SERVER_PORT);
    }

    public Client_temp(String serverAddress, int serverPort) {
        this.inetAddress = getServerAddress(serverAddress);
        this.socket = getSocket(inetAddress, serverPort);
        Thread reader = new Thread(new Reader(this.socket), "client.Client reader");
        reader.start();
        Thread writer = new Thread(new Writer(this.socket), "client.Client writer");
        writer.start();
        System.out.println("client.Client started");
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

}
