package helper;

import java.io.*;

import static java.lang.Thread.interrupted;

/**
 * Helper class to read the console input
 *
 * @author Mark Banierink
 */
public class ConsoleReader implements Runnable {

    private ServerClientInterface serverClientInterface = null;

    /**
     * Constructor for the console reader
     * @param serverClientInterface Interface containing the handling method for console input
     */
    public ConsoleReader(ServerClientInterface serverClientInterface) {
        this.serverClientInterface = serverClientInterface;
    }

    /**
     * Run method reading the console and handling the input
     */
    public void run() {
        String line;
        while ((line = readString("")) != null && !interrupted()) {
            serverClientInterface.handleConsoleInput(line);
        }
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
