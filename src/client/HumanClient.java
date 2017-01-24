package client;

/**
 * Created by mark.banierink on 16-1-2017.
 */
public class HumanClient extends Client {

    public static void main(String[] args) {
        System.out.println("Starting HumanClient");
        new HumanClient();
    }

    public HumanClient() {
        Thread consoleReader = new Thread(new ConsoleReader(this), "ConsoleReader");
        consoleReader.start();
    }

}
