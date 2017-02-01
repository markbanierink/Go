package helper;

import java.io.*;

/**
 * Helper class to read the console input
 *
 * @author Mark Banierink
 */
public class ConsoleReader implements Runnable {

    private ServerClientInterface serverClientInterface = null;
    private boolean stop;

    /**
     * Constructor for the console reader
     * @param serverClientInterface Interface containing the handling method for console input
     */
    public ConsoleReader(ServerClientInterface serverClientInterface) {
        this.serverClientInterface = serverClientInterface;
    }

    /**
     * Set stop to exit the loop of the console reader
     */
    public void setStop() {
        stop = true;
    }

    /**
     * Run method reading the console and handling the input
     */
    public void run() {
        String line;
        while ((line = readString("")) != null && !stop) {
            serverClientInterface.handleConsoleInput(line);
        }
        System.out.println("ConsoleReader finished");
    }

    protected String readString(String string) {
        String line = null;
        try {
            System.out.print(string);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            line = in.readLine();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        if (line == null) {
            return "";
        }
        else {
            return line;
        }
    }
}
