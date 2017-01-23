package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class Client {

    private BufferedWriter serverInput;

    Client(String serverAddress, int serverPort) {
        InetAddress inetAddress = getServerAddress(serverAddress);
        Socket socket = getSocket(inetAddress, serverPort);
        Thread socketReader = new Thread(new SocketReader(socket), "SocketReader");
        socketReader.start();
        this.serverInput = createSocketWriter(socket);
    }

    private InetAddress getServerAddress(String serverAddress) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            System.out.println("No Internet Address");
            System.out.println(e.getMessage());
        }
        return inetAddress;
    }

    private Socket getSocket(InetAddress inetAddress, int port) {
        Socket socket = null;
        try {
            socket = new Socket(inetAddress, port);
        } catch (IOException e) {
            System.out.println("No Socket");
            System.out.println(e.getMessage());
        }
        return socket;
    }

    public BufferedWriter createSocketWriter(Socket socket) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return out;
    }

    public void handleConsoleInput(String string) {
        handleServerOutput(string);
    }

    public void handleServerOutput(String string) {
        try {
            this.serverInput.write(string);
            this.serverInput.newLine();
            this.serverInput.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printOutput(String string) {
        System.out.println(string);
    }

}
