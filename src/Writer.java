import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class Writer implements Runnable {

    private Socket socket = null;
    //protected BufferedReader in;
    protected BufferedWriter out;

    public Writer(Socket socket) {
        this.socket = socket;
        try {
            //this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        String line;
        try {
            while ((line = readString("")) != null) {
                this.out.write(line);
                this.out.newLine();
                this.out.flush();
            }
            //shutDown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String readString(String tekst) {
        System.out.print(tekst);
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
            //this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
