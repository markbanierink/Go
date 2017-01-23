package client;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class HumanClient extends Client {

    private final static String SERVER_ADDRESS = "localhost";
    private final static int SERVER_PORT = 2727;

    public static void main(String[] args) {
        new HumanClient(SERVER_ADDRESS, SERVER_PORT);
    }

    public HumanClient(String serverAddress, int serverPort) {
        super(serverAddress, serverPort);
        Thread consoleReader = new Thread(new ConsoleReader(this), "ConsoleReader");
        consoleReader.start();
        printOutput("HumanClient started");
    }

}

