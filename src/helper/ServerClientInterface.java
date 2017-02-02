package helper;

/**
 * Created by mark.banierink on 25-1-2017.
 */
public interface ServerClientInterface {

    int BOARD_SIZE_MIN = 5;
    int BOARD_SIZE_MAX = 131;
    int PORT_MIN = 0;
    int PORT_MAX = 65535;
    int DEFAULT_PLAYERS_PER_GAME = 2;
    int DEFAULT_MOVES_PER_TURN = 1;
    int DEFAULT_PORT = 2727;
    int PLAYERS_PER_GAME_MIN = 2;
    int MOVES_PER_TURN_MIN = 1;
    int MOVES_PER_TURN_MAX = 5;

    void handleConsoleInput(String line);
}
