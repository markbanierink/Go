package helper;

import game.Game;
import game.Player;
import helper.enums.Keyword;
import helper.enums.Stone;

import static helper.ServerClientInterface.*;
import static helper.enums.Keyword.*;
import static helper.enums.Resources.*;

/**
 * This toolbox provides methods for checking protocol Keywords and Resources
 * @author Mark Banierink
 */
public class CommandToolbox {

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
     * @param boardSizeMin minimum board size
     * @param boardSizeMax maximum board size
     * @return true if board size between the range and an odd number, false if not
     */
    public static boolean isValidBoardSize(int boardSize, int boardSizeMin, int boardSizeMax) {
        if (boardSize >= boardSizeMin && boardSize <= boardSizeMax) {
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
     * Creates a valid CHAT command for the client to the server
     * @param message the string message to be sent
     * @return String that contains the commando
     */
    public static String createCommandChat(String message) {
        return CHAT.toString() + SPACE + message;
    }

    /**
     * Creates a valid READY command from the provided arguments
     * @param game this command concerns
     * @param player that it is sent to
     * @return String that can be used as a commando for the server
     */
    public static String createCommandReady(Game game, Player player) {
        int boardSize = game.getBoard().getBoardSize();
        Player nextPlayer = game.getPlayerByStone(player.getStone().nextStone(game.getPlayers().size()));
        String result = READY.toString() + SPACE + player.getStone().toString().toLowerCase() + SPACE + nextPlayer.getName() + SPACE + boardSize;
        for (int i = 2; i < game.getPlayers().size(); i++) {
            nextPlayer = game.getPlayerByStone(nextPlayer.getStone().nextStone(game.getPlayers().size()));
            result += SPACE + nextPlayer.getStone().toString().toLowerCase();
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
        return playerArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return String containing the Player's name, or else null
     */
    public static String[] playerArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], PLAYER)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid GO command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isGoCommand(String string) {
        return goArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return int containing the Player's requested board size, or else -1
     */
    public static String[] goArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], GO) && isInteger(split[1]) && isValidBoardSize(Integer.parseInt(split[1]), BOARD_SIZE_MIN, BOARD_SIZE_MAX)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid WAITING command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isWaitingCommand(String string) {
        return waitingArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is WAITING
     */
    public static String[] waitingArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 1 && (equalsKeyword(split[0], WAITING))) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid CANCEL command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isCancelCommand(String string) {
        return cancelArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is CANCEL
     */
    public static String[] cancelArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 1 && equalsKeyword(split[0], CANCEL)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid READY command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isReadyCommand(String string) {
        return readyArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the READY command
     */
    public static String[] readyArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 4 && !isOdd(split.length) && equalsKeyword(split[0], READY) && isStone(split[1].toUpperCase()) && isInteger(split[3])) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid MOVE command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isMoveCommand(String string) {
        return moveArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the MOVE command
     */
    public static String[] moveArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 3 && equalsKeyword(split[0], MOVE) && isInteger(split[1]) && isInteger(split[2])) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid VALID command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isValidCommand(String string) {
        return validArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the VALID command
     */
    public static String[] validArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 4 && equalsKeyword(split[0], VALID) && isStone(split[1]) && isInteger(split[2]) && isInteger(split[3])) {
            split[1] = split[1].toUpperCase();
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid INVALID command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isInvalidCommand(String string) {
        return invalidArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the INVALID command
     */
    public static String[] invalidArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], INVALID) && isStone(split[1])) {
            split[1] = split[1].toUpperCase();
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid PASS command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isPassCommand(String string) {
        return passArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is PASS
     */
    public static String[] passArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 1 && equalsKeyword(split[0], PASS)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid PASSED command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isPassedCommand(String string) {
        return passedArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the PASSED command
     */
    public static String[] passedArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], PASSED) && isStone(split[1])) {
            split[1] = split[1].toUpperCase();
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid TABLEFLIP command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isTableFlipCommand(String string) {
        return tableFlipArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is TABLEFLIP
     */
    public static String[] tableFlipArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 1 && equalsKeyword(split[0], TABLEFLIP)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid HINT command
     * @param string to be checked
     * @return true if valid
     */
    public static boolean isHintCommand(String string) {
        return hintArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return true if it is HINT
     */
    public static String[] hintArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 1 && equalsKeyword(split[0], HINT)) {
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid TABLEFLIPPED command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isTableFlippedCommand(String string) {
        return tableFlippedArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the PASSED command
     */
    public static String[] tableFlippedArguments(String string) {
        String[] split = splitString(string);
        if (split.length == 2 && equalsKeyword(split[0], TABLEFLIPPED) && isStone(split[1])) {
            split[1] = split[1].toUpperCase();
            return split;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid CHAT command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isChatCommand(String string) {
        return chatArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the CHAT command
     */
    public static String[] chatArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], CHAT)) {
            String[] newSplit = new String[2];
            newSplit[0] = CHAT.toString();
            newSplit[1] = string.substring(CHAT.toString().length() + 1);
            return newSplit;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid WARNING command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isWarningCommand(String string) {
        return warningArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the WARNING command
     */
    public static String[] warningArguments(String string) {
        String[] split = splitString(string);
        if (split.length >= 2 && equalsKeyword(split[0], WARNING)) {
            String[] newSplit = new String[2];
            newSplit[0] = WARNING.toString();
            newSplit[1] = string.substring(WARNING.toString().length());
            return newSplit;
        }
        return null;
    }

    /**
     * Checks if the string contains a valid END command
     * @param string to be checked
     * @return true if invalid
     */
    public static boolean isEndCommand(String string) {
        return endArguments(string) != null;
    }

    /**
     * Splits and checks the string for validity and arguments
     * @param string to be checked
     * @return arguments for the END command
     */
    public static String[] endArguments(String string) {
        String[] split = splitString(string);
        String[] result = null;
        if (split.length >= 3 && equalsKeyword(split[0], END)) {
            for (int i = 1; i < split.length; i++) {
                if (isInteger(split[i])) {
                    result = split;
                }
            }
        }
        return result;
    }
}
