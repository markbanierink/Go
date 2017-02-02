package helper.enums;

/**
 * Provides standardised Resources for communication
 * @author Mark Banierink
 */
public enum Resources {

    SPACE(" "),
    COLON(": "),
    N("n"),
    Y("y"),
    LISTED("Listed"),
    REMOVED("Removed"),
    NEW_PLAYER("New player"),
    LOCAL_IP("Local IP address"),
    PORT_NUMBER("Port number"),
    MATCH_BOARDSIZE("Match players on board size"),
    MAXIMUM_CLIENTS("Maximum number of clients that can connect"),
    PLAYERS_PER_GAME("Number of players per game"),
    MOVES_PER_TURN("Number of moves per turn"),
    SERVER_START("Server starting"),
    SERVERSOCKET_MADE("ServerSocket made"),
    SOCKET_AVAILABLE("Socket available"),
    CLIENTHANDLER("ClientHandler"),
    CLIENTHANDLER_LISTED("ClientHandler listed"),
    CLIENTHANDLERTHREAD_LISTED("ClientHandler listed"),
    GAME_STARTED("Game started"),
    GAME_REMOVED("This game is removed"),
    CLIENTHANDLER_REMOVED("ClientHandler removed"),
    GAME_LISTED("Game listed"),
    PLAYER_REMOVED_GAME("Player removed from Game"),
    ANONYMOUS("Anonymous Client"),
    CONNECTION_LOST("Connection with Client was lost"),
    STOP_SOCKETREADER("Stopping SocketReader"),
    CONNECTING_SOCKET("Connecting to socket"),
    PLAYER_ADD_GAME("Player added to Game"),
    NEW_CLIENT("[New Client]"),
    CLIENTHANDLER_CLOSED("ClientHandler closed"),
    KICKED("You are being kicked"),
    IS_KICKED("is being kicked"),
    SERVER_SHUTDOWN("Server shuts down"),
    SERVER_SOCKET_NOT_POSSIBLE("Socket could not be made at requested port. Please retry"),
    NOT_TURN("Not player's turn"),
    NOT_FIELD("No field"),
    NOT_FREE_FIELD("No free field"),
    KO("Ko: same as previous board"),
    WAITING_FOR_OPPONENT("Waiting for opponent..."),
    SERVER_CLIENT_MISMATCH("There is mismatch between Server and Client implementation"),
    SERVER("Server"),
    YOUR_TURN("It is your turn"),
    NAME_TAKEN("Name is already taken, please retry");

    private final String string;

    Resources(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }

}
