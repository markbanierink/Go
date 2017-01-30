package helper;

import java.io.*;

/**
 * Created by mark.banierink on 18-1-2017.
 * @author Mark Banierink
 */
public class ConsoleReader implements Runnable {

    private ServerClientInterface serverClientInterface = null;

    public ConsoleReader(ServerClientInterface serverClientInterface) {
        this.serverClientInterface = serverClientInterface;
    }

    public void run() {
        String line;
        while ((line = readString("")) != null) {
            getServerClient().handleConsoleInput(line);
        }
        System.out.println("ConsoleReader finished");
    }

    private ServerClientInterface getServerClient() {
        return this.serverClientInterface;
    }

    public String readString(String string) {
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
