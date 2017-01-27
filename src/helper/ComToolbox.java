package helper;

import game.Player;
import helper.enums.Keyword;
import helper.enums.Stone;

import static helper.enums.Keyword.*;
import static helper.enums.Strings.*;

/**
 * This toolbox provides methods for checking protocol Keywords and Strings
 * @author Mark Banierink
 */
public class ComToolbox {

    private static boolean equalsKeyword(String word, Keyword keyword) {
        return word.equals(keyword.toString());
    }

    /**
     * Splits a string at spaces
     * @param string Input string to be split
     * @return String array with the separate elements, one element if no split was found
     */
    public static String[] splitString(String string) {
        return string.split(SPACE.toString());
    }

    /**
     * Checks if the provided String is a integer
     * @param word is the string to test
     * @return true if the word is integer, false if it isn't
     */
    public static boolean isInteger(String word) {
        try {
            Integer.parseInt(word);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isStone(String word) {
        for (Stone stone : Stone.values()) {
            if (isSameStone(word.toUpperCase(), stone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given board size is allowed
     * @param boardSize the requested board size
     * @param boardSizeRange array with two elements that defines the minimum and maximum (inclusive)
     * @return true if board size between the range and an odd number, false if not
     */
    public static boolean isBoardSize(int boardSize, int[] boardSizeRange) {
        if (boardSize >= boardSizeRange[0] && boardSize <= boardSizeRange[1]) {
            if (isOdd(boardSize)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOdd(int number) {
        return (number % 2 == 1);
    }

    private static boolean isSameStone(String word, Stone stone) {
        return word.equals(stone.toString());
    }

    /**
     * Creates a valid PLAYER command from the provided arguments
     * @param player must be the player that is processed
     * @return String that can be used as a commando for the server
     */
    public static String createCommandPlayer(Player player) {
        return PLAYER.toString() + SPACE + player.getName();
    }

    /**
     * Creates a valid GO command from the provided arguments
     * @param boardSize is the requested board size
     * @return String that can be used as a commando for the server
     */
    public static String createCommandGo(int boardSize) {
        return GO.toString() + SPACE + boardSize;
    }

    /**
     * Creates a valid CANCEL command from the provided arguments
     * @return String that can be used as a commando for the server
     */
    public static String createCommandCancel() {
        return CANCEL.toString();
    }

    /**
     * Creates a valid WAITING command from the provided arguments
     * @return String that can be used as a commando for the server
     */
    public static String createCommandWaiting() {
        return WAITING.toString();
    }

    /**
     * Creates a valid MOVE command from the provided arguments
     * @param x the coordinate of the move
     * @param y the coordinate of the move
     * @return String that can be used as a commando for the server
     */
    public static String createCommandMove(int x, int y) {
        return MOVE.toString() + SPACE + x + SPACE + y;
    }

    /**
     * Creates a valid PASS command from the provided arguments
     * @return String that can be used as a commando for the server
     */
    public static String createCommandPass() {
        return PASS.toString();
    }

    /**
     * Creates a valid TABLEFLIP command from the provided arguments
     * @return String that can be used as a commando for the server
     */
    public static String createCommandTableFlip() {
        return TABLEFLIP.toString();
    }

    /**
     * Creates a valid CHAT command from the provided arguments
     * @param sender is the name of the sender (client or server)
     * @param message is the message string that is sent
     * @return String that can be used as a commando for the server
     */
    public static String createCommandChat(String sender, String message) {
        return CHAT.toString() + SPACE + sender + ":" + SPACE + message;
    }

    /**
     * Creates a valid READY command from the provided arguments
     * @param boardSize is the board size on which is going to be played
     * @param stones contains the stones of the players
     * @param players contains the names of the players
     * @return String that can be used as a commando for the server
     */
    public static String createCommandReady(int boardSize, Stone[] stones, Player[] players) {
        String result = READY.toString() + SPACE + boardSize;
        for (int i = 0; i < stones.length; i++) {
            result += SPACE.toString() + stones[i].toString().toLowerCase() + SPACE + players[i].getName();
        }
        return result;
    }

    /**
     * Creates a valid VALID command from the provided arguments
     * @param stone is the stone of the player
     * @param x the coordinate of the move
     * @param y the coordinate of the move
     * @return String that can be used as a commando for the server
     */
    public static String createCommandValid(Stone stone, int x, int y) {
        return VALID.toString() + SPACE + stone.toString().toLowerCase() + SPACE + x + SPACE + y;
    }

    /**
     * Creates a valid INVALID command from the provided arguments
     * @param stone is the stone of the player
     * @param reason is the reason the move is invalid
     * @return String that can be used as a commando for the server
     */
    public static String createCommandInvalid(Stone stone, String reason) {
        return INVALID.toString() + SPACE + stone.toString().toLowerCase() + SPACE + reason;
    }

    /**
     * Creates a valid PASSED command from the provided arguments
     * @param stone is the stone of the player
     * @return String that can be used as a commando for the server
     */
    public static String createCommandPassed(Stone stone) {
        return PASSED.toString() + SPACE + stone.toString().toLowerCase();
    }

    /**
     * Creates a valid TABLEFLIPPED command from the provided arguments
     * @param stone is the stone of the player
     * @return String that can be used as a commando for the server
     */
    public static String createCommandTableFlipped(Stone stone) {
        return TABLEFLIPPED.toString() + SPACE + stone.toString().toLowerCase();
    }

    /**
     * Creates a valid WARNING command from the provided arguments
     * @param message is the warning message
     * @return String that can be used as a commando for the server
     */
    public static String createCommandWarning(String message) {
        return WARNING.toString() + SPACE + message;
    }

    /**
     * Creates a valid END command from the provided arguments
     * @param scores is are the scores of the players in color order
     * @return String that can be used as a commando for the server
     */
    public static String createCommandEnd(int[] scores) {
        String result = END.toString();
        for (int i = 0; i < scores.length; i++) {
            result += SPACE.toString() + scores[i];
        }
        return result;
    }

    /**
     * Checks if the string contains a valid PLAYER command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isPlayerCommand(String string) {
        return playerCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return String containing the Player's name, or else null
     */
    public static String playerCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], PLAYER)) {
            return splitString(string)[1];
        }
        return null;
    }

    /**
     * Checks if the string contains a valid GO command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isGoCommand(String string) {
        return goCommandArguments(string) != -1;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return int containing the Player's requested board size, or else -1
     */
    public static int goCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], GO) && isInteger(split[1])) {
            return Integer.parseInt(splitString(string)[1]);
        }
        return -1;
    }

    /**
     * Checks if the string contains a valid WAITING command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isWaitingCommand(String string) {
        return waitingCommandArguments(string);
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is WAITING
     */
    public static boolean waitingCommandArguments(String string) {
        String[] split = splitString(string);
        return split.length == 1 && (equalsKeyword(split[0], WAITING));
    }

    /**
     * Checks if the string contains a valid CANCEL command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isCancelCommand(String string) {
        return cancelCommandArguments(string);
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is CANCEL
     */
    public static boolean cancelCommandArguments(String string) {
        String[] split = splitString(string);
        return split.length == 1 && equalsKeyword(split[0], CANCEL);
    }

    /**
     * Checks if the string contains a valid READY command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isReadyCommand(String string) {
        return readyCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the READY command
     */
    public static String readyCommandArguments(String string) {
        String result = null;
        String[] split = splitString(string);
        if (split.length >= 6 && !isOdd(split.length) && equalsKeyword(split[0], READY) && isInteger(split[1])) {
            for (int i = 2; i < split.length; i += 2) {
                if (isStone(split[i])) {
                    result = string.substring(READY.toString().length());
                }
                else {
                    result = null;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks if the string contains a valid MOVE command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isMoveCommand(String string) {
        return moveCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the MOVE command
     */
    public static String moveCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 3 && equalsKeyword(split[0], MOVE) && isInteger(split[1]) && isInteger(split[2])) {
            return string.substring(MOVE.toString().length() + 1);
        }
        return null;
    }

    /**
     * Checks if the string contains a valid VALID command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isValidCommand(String string) {
        return validCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the VALID command
     */
    public static String validCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 4 && equalsKeyword(split[0], VALID) && isStone(split[1]) && isInteger(split[2]) && isInteger(split[3])) {
            return string.substring(VALID.toString().length());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid INVALID command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isInvalidCommand(String string) {
        return invalidCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the INVALID command
     */
    public static String invalidCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], INVALID) && isStone(split[1])) {
            return string.substring(INVALID.toString().length());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid PASS command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isPassCommand(String string) {
        return passCommandArguments(string);
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is PASS
     */
    public static boolean passCommandArguments(String string) {
        String[] split = splitString(string);
        return split.length == 1 && equalsKeyword(split[0], PASS);
    }

    /**
     * Checks if the string contains a valid PASSED command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isPassedCommand(String string) {
        return passedCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the PASSED command
     */
    public static Stone passedCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], PASSED) && isStone(split[1])) {
            return Stone.valueOf(split[1].toUpperCase());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid TABLEFLIP command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isTableFlipCommand(String string) {
        return tableFlipCommandArguments(string);
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is TABLEFLIP
     */
    public static boolean tableFlipCommandArguments(String string) {
        String[] split = splitString(string);
        return split.length == 1 && equalsKeyword(split[0], TABLEFLIP);
    }

    /**
     * Checks if the string contains a valid TABLEFLIPPED command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isTableFlippedCommand(String string) {
        return tableFlippedCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the PASSED command
     */
    public static Stone tableFlippedCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], TABLEFLIPPED) && isStone(split[1])) {
            return Stone.valueOf(split[1].toUpperCase());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid CHAT command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isChatCommand(String string) {
        return chatCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the CHAT command
     */
    public static String chatCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], CHAT)) {
            return string.substring(CHAT.toString().length());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid WARNING command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isWarningCommand(String string) {
        return warningCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the WARNING command
     */
    public static String warningCommandArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], WARNING)) {
            return string.substring(WARNING.toString().length());
        }
        return null;
    }

    /**
     * Checks if the string contains a valid END command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isEndCommand(String string) {
        return endCommandArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the END command
     */
    public static String endCommandArguments(String string) {
        String[] split = splitString(string);
        String result = null;
        if (split.length >= 3 && equalsKeyword(split[0], END)) {
            for (int i = 1; i < split.length; i++) {
                if (!isInteger(split[i])) {
                    result = null;
                    break;
                }
                result = string.substring(WARNING.toString().length());
            }
        }
        return result;
    }
}
