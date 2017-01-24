package helper;

/**
 * Created by mark.banierink on 21-1-2017.
 * Provides standardised Strings for communication
 */
public enum Strings {

    SPACE (" "),
    UNKNOWN_KEYWORD ("Unknown keyword or non-allowed command"),
    GAME_EXISTS ("You already started a game"),
    KICKED ("You are being kicked"),
    IS_KICKED ("is being kicked"),
    SERVER_SHUTDOWN ("Server shuts down"),
    SERVER_SOCKET_NOT_POSSIBLE ("Socket could not be made at requested port. Please retry"),
    CLIENT_SOCKET_NOT_POSSIBLE ("Socket could not be found at requested address and port. Please retry"),
    NOT_TURN ("Not player's turn"),
    INVALID_PORT_NUMBER ("Invalid port number. Please retry"),
    NOT_FIELD ("No field"),
    NOT_FREE_FIELD ("No free field"),
    KO ("Ko: same as previous board"),
    WAITING_FOR_OPPONENT ("Waiting for opponent..."),
    SERVER_CLIENT_MISMATCH ("There is mismatch between Server and Client implementation"),
    SERVER ("Server");

    private final String string;

    Strings(String string) {
        this.string = string;
    }

    public String toString() {
        return this.string;
    }

}
