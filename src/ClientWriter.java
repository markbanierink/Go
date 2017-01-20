import java.io.*;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class ClientWriter implements Runnable {

//    private ClientHandler clientHandler;
//    private Socket socket = null;
//    protected BufferedWriter out;
//
//    public ClientWriter(ClientHandler clientHandler, Socket socket) {
//        this.clientHandler = clientHandler;
//        this.socket = socket;
//        try {
//            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

    public void run() {
//        String line;
//        try {
//            while ((line = readString("")) != null) {
//                this.out.write("ClientWriter: " + line);
//                this.out.newLine();
//                this.out.flush();
//            }
//            //shutDown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
//
//    public void writeString(String string) {
//        try {
//            this.out.write(string);
//            this.out.newLine();
//            this.out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public String readString(String text) {
//        System.out.print(text);
//        String line = null;
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            line = in.readLine();
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//        return (line == null) ? "" : line;
//    }
//
//    public void shutDown() {
//        try {
//            System.out.println("Stopping client writer...");
//            this.out.close();
//            this.socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
