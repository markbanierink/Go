package helper.enums;

/**
 * Provides standardised Resources for communication
 * @author Mark Banierink
 */
public enum Resources {

    SPACE(" "),
    UNKNOWN_KEYWORD("Unknown keyword or non-allowed command"),
    GAME_EXISTS("You already started a game"),
    KICKED("You are being kicked"),
    IS_KICKED("is being kicked"),
    SERVER_SHUTDOWN("Server shuts down"),
    SERVER_SOCKET_NOT_POSSIBLE("Socket could not be made at requested port. Please retry"),
    CLIENT_SOCKET_NOT_POSSIBLE("Socket could not be found at requested address and port. Please retry"),
    NOT_TURN("Not player's turn"),
    NOT_FIELD("No field"),
    NOT_FREE_FIELD("No free field"),
    KO("Ko: same as previous board"),
    WAITING_FOR_OPPONENT("Waiting for opponent..."),
    SERVER_CLIENT_MISMATCH("There is mismatch between Server and Client implementation"),
    SERVER("Server"),
    PERFORMED_TABLEFLIP("flipped the table"),
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
