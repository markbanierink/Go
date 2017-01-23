package client;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class ComputerClient extends Client {

    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 2727;

    public static void main(String[] args) {
        new ComputerClient(SERVER_ADDRESS, SERVER_PORT);
    }

    public ComputerClient(String serverAddress, int serverPort) {
        super(serverAddress, serverPort);
        System.out.println("ComputerClient started");
    }

}
