package helper;

import java.io.*;

/**
 * Created by mark.banierink on 18-1-2017.
 * @author Mark Banierink
 */
public class ConsoleReader implements Runnable {

    private ServerClientInterface serverClientInterface = null;
    private boolean paused;

    public ConsoleReader(ServerClientInterface serverClientInterface) {
        this.serverClientInterface = serverClientInterface;
        this.paused = false;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean pausedSetting) {
        this.paused = pausedSetting;
    }

    public void run() {
        String line;
        while ((line = readString("")) != null) {
            if (isPaused()) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
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
