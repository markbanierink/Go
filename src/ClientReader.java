import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class ClientReader implements Runnable {

//    private ClientHandler clientHandler;
//    private Socket socket = null;
//    protected BufferedReader in;
//
//    public ClientReader(ClientHandler clientHandler, Socket socket) {
//        this.clientHandler = clientHandler;
//        this.socket = socket;
//        try {
//            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//    }

    public void run() {
//        String line;
//        try {
//            while ((line = in.readLine()) != null) {
//                clientHandler.handleClientInput(line);
//                //System.out.println(line);
//            }
//            //shutDown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
//
//    public void shutDown() {
//        try {
//            System.out.println("Stopping client reader...");
//            this.in.close();
//            this.socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
