package helper;

import static helper.Keyword.*;
import static helper.Strings.*;

/**
 * Created by mark.banierink on 18-1-2017.
 * This toolbox provides methods for checking protocol Keywords and Strings
 */
public class ComToolbox {

    /**
     * Returns the Keyword of a string if it is in the first position
     * @param string String to be searched for a Keyword
     * @return Keyword Keyword that is found, null if none is found
     */
    public static Keyword getKeyword(String string) {
        try {
            return Keyword.valueOf(splitString(string)[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean isKeyword(String word, Keyword keyword) {
        return word.equals(keyword.toString());
    }

    /**
     * Splits a string at spaces
     * @param string Input string to be split
     * @return String array with the separate elements, one element if no split was found
     */
    public static String[] splitString(String string) {
        return string.split(" ");
    }

    private static int length(String[] split) {
        return split.length;
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
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String word) {
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isStone(String word) {
        for (Stone stone : Stone.values()) {
            if(isStone(word, stone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given board size is allowed
     * @param boardsize the requested board size
     * @return true if board size is allow, false if not
     */
    public static boolean isBoardsize(int boardsize) {                                      // make more dynamic!!!
        if (boardsize >= 5 && boardsize <= 131) {
            if (isOdd(boardsize)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOdd(int number) {
        return (number % 2 == 1);
    }

    private static boolean isStone(String word, Stone stone) {
        return word.equals(stone.toString());
    }

    /**
     * Creates a proper GO command from the provided arguments
     * @param name must be a string without spaces
     * @param boardsize the board size integer
     * @return String that can be used as a commando for the server
     */
    public static String createCommandGo(String name, int boardsize) {
        return GO.toString() + SPACE + name + SPACE + boardsize;
    }

    private static boolean isGo(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], GO)) {
                if ((length(splitString(string)) == 3) && isInteger(splitString(string)[2])) {
                    return isBoardsize(Integer.parseInt(splitString(string)[2]));
                }
            }
        }
        return false;
    }

    private static boolean isWaiting(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], WAITING)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    private static boolean isCancel(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CANCEL)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    private static boolean isReady(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], READY)) {
                return ((length(splitString(string)) == 4) &&
                        isStone(splitString(string)[1]) &&
                        isInteger(splitString(string)[3]));
            }
        }
        return false;
    }

    private static boolean isMove(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], MOVE)) {
                return ((length(splitString(string)) == 3) &&
                        isInteger(splitString(string)[1]) &&
                        isInteger(splitString(string)[2]));
            }
        }
        return false;
    }

    private static boolean isValid(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], VALID)) {
                return ((length(splitString(string)) == 4) &&
                        isStone(splitString(string)[1]) &&
                        isInteger(splitString(string)[2]) &&
                        isInteger(splitString(string)[3]));
            }
        }
        return false;
    }

    private static boolean isInvalid(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], INVALID)) {
                return (length(splitString(string)) == 3) &&
                        isStone(splitString(string)[1]);
            }
        }
        return false;
    }

    private static boolean isPass(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], PASS)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    private static boolean isPassed(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], PASSED)) {
                return ((length(splitString(string)) == 1) &&
                        isStone(splitString(string)[1]));
            }
        }
        return false;
    }

    private static boolean isTableflip(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], TABLEFLIP)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    private static boolean isTableflipped(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], TABLEFLIPPED)) {
                return ((length(splitString(string)) == 1) &&
                        isStone(splitString(string)[1]));
            }
        }
        return false;
    }

    private static boolean isChat(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CHAT)) {
                return (length(splitString(string)) > 1);
            }
        }
        return false;
    }

    private static boolean isWarning(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], WARNING)) {
                return (length(splitString(string)) > 1);
            }
        }
        return false;
    }

    private static boolean isEnd(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CHAT)) {
                return ((length(splitString(string)) == 1) &&
                        isDouble(splitString(string)[1]) &&
                        isDouble(splitString(string)[1]));
            }
        }
        return false;
    }

    /**
     * Checks if the input is a valid command, based on the keyword
     * @param keyword the Keyword with which the string is compared
     * @param string the String that is checked for the Keyword and correct composition
     * @return true if the string comply's with the required structure
     */
    public static boolean isValidCommand(Keyword keyword, String string) {
        boolean result = false;
        switch (keyword) {
            case GO:
                result = isGo(string);
                break;
            case CANCEL:
                result = isCancel(string);
                break;
            case MOVE:
                result = isMove(string);
                break;
            case PASS:
                result = isPass(string);
                break;
            case TABLEFLIP:
                result = isTableflip(string);
                break;
            case CHAT:
                result = isChat(string);
                break;
            case WAITING:
                result = isWaiting(string);
                break;
            case READY:
                result = isReady(string);
                break;
            case VALID:
                result = isValid(string);
                break;
            case INVALID:
                result = isInvalid(string);
                break;
            case PASSED:
                result = isPassed(string);
                break;
            case TABLEFLIPPED:
                result = isTableflipped(string);
                break;
            case WARNING:
                result = isWarning(string);
                break;
            case END:
                result = isEnd(string);
                break;
        }
        return result;
    }

//    public static boolean isServerCommand(String string) {
//        if (isGo(string)) {
//            return true;
//        }
//        if (isCancel(string)) {
//            return true;
//        }
//        if (isMove(string)) {
//            return true;
//        }
//        if (isPass(string)) {
//            return true;
//        }
//        if (isTableflip(string)) {
//            return true;
//        }
//        if (isChat(string)) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isClientCommand(String string) {
//        if (isWaiting(string)) {
//            return true;
//        }
//        if (isReady(string)) {
//            return true;
//        }
//        if (isValid(string)) {
//            return true;
//        }
//        if (isInvalid(string)) {
//            return true;
//        }
//        if (isPassed(string)) {
//            return true;
//        }
//        if (isTableflipped(string)) {
//            return true;
//        }
//        if (isChat(string)) {
//            return true;
//        }
//        if (isWarning(string)) {
//            return true;
//        }
//        if (isEnd(string)) {
//            return true;
//        }
//        return false;
//    }

}
