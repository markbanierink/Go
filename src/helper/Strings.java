package helper;

/**
 * Created by mark.banierink on 21-1-2017.
 */
public enum Strings {

    SPACE (" "),
    UNKNOWN_KEYWORD ("Unknown keyword"),
    GAME_EXISTS ("You already started a game"),
    KICKED ("You are being kicked"),
    IS_KICKED ("is being kicked"),
    SERVER_SHUTDOWN ("Server shuts down"),
    NO_SOCKET_AT_PORT ("Socket could not be made at requeste port. Please retry");

    private final String string;

    Strings(String string) {
        this.string = string;
    }

    public String toString() {
        return this.string;
    }

}
