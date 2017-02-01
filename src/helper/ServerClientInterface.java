package helper;

/**
 * Created by mark.banierink on 25-1-2017.
 */
public interface ServerClientInterface {

    int BOARD_SIZE_MIN = 5;
    int BOARD_SIZE_MAX = 131;
    int PORT_MIN = 0;
    int PORT_MAX = 65535;

    void handleConsoleInput(String line);
}
