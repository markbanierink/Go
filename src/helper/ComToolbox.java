package helper;

import static helper.Keyword.*;

/**
 * Created by mark.banierink on 18-1-2017.
 */
public class ComToolbox {

    public static Keyword getKeyword(String string) {
        return Keyword.valueOf(splitString(string)[0]);
    }

    public static boolean hasKeyword(String string) {
        if (length(splitString(string)) > 0) {
            if (isKeyword(splitString(string)[0])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKeyword(String word) {
        for (Keyword keyword : Keyword.values()) {
            if (isKeyword(word, keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKeyword(String word, Keyword keyword) {
        return word.equals(keyword.toString());
    }

    public static String[] splitString(String string) {
        return string.split(" ");
    }

    public static int length(String[] split) {
        return split.length;
    }

    public static boolean isInteger(String word) {
        try {
            Integer.parseInt(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String word) {
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isStone(String word) {
        for (Stone stone : Stone.values()) {
            if(isStone(word, stone)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStone(String word, Stone stone) {
        return word.equals(stone.toString());
    }

    public static boolean isGo(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], GO)) {
                return ((length(splitString(string)) == 3) &&
                        isInteger(splitString(string)[2]));
            }
        }
        return false;
    }

    public static boolean isWaiting(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], WAITING)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    public static boolean isCancel(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CANCEL)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    public static boolean isReady(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], READY)) {
                return ((length(splitString(string)) == 4) &&
                        isStone(splitString(string)[1]) &&
                        isInteger(splitString(string)[3]));
            }
        }
        return false;
    }

    public static boolean isMove(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], MOVE)) {
                return ((length(splitString(string)) == 3) &&
                        isInteger(splitString(string)[1]) &&
                        isInteger(splitString(string)[2]));
            }
        }
        return false;
    }

    public static boolean isValid(String string) {
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

    public static boolean isInvalid(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], INVALID)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    public static boolean isPass(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], PASS)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    public static boolean isPassed(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], PASSED)) {
                return ((length(splitString(string)) == 1) &&
                        isStone(splitString(string)[1]));
            }
        }
        return false;
    }

    public static boolean isTableflip(String string) {
        String[] split = splitString(string);
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], TABLEFLIP)) {
                return (length(splitString(string)) == 1);
            }
        }
        return false;
    }

    public static boolean isTableflipped(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], TABLEFLIPPED)) {
                return ((length(splitString(string)) == 1) &&
                        isStone(splitString(string)[1]));
            }
        }
        return false;
    }

    public static boolean isChat(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CHAT)) {
                return (length(splitString(string)) > 1);
            }
        }
        return false;
    }

    public static boolean isWarning(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], WARNING)) {
                return (length(splitString(string)) > 1);
            }
        }
        return false;
    }

    public static boolean isEnd(String string) {
        if (string.length() > 0) {
            if (isKeyword(splitString(string)[0], CHAT)) {
                return ((length(splitString(string)) == 1) &&
                        isDouble(splitString(string)[1]) &&
                        isDouble(splitString(string)[1]));
            }
        }
        return false;
    }

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

    public static boolean isServerCommand(String string) {
        if (isGo(string)) {
            return true;
        }
        if (isCancel(string)) {
            return true;
        }
        if (isMove(string)) {
            return true;
        }
        if (isPass(string)) {
            return true;
        }
        if (isTableflip(string)) {
            return true;
        }
        if (isChat(string)) {
            return true;
        }
        return false;
    }

    public static boolean isClientCommand(String string) {
        if (isWaiting(string)) {
            return true;
        }
        if (isReady(string)) {
            return true;
        }
        if (isValid(string)) {
            return true;
        }
        if (isInvalid(string)) {
            return true;
        }
        if (isPassed(string)) {
            return true;
        }
        if (isTableflipped(string)) {
            return true;
        }
        if (isChat(string)) {
            return true;
        }
        if (isWarning(string)) {
            return true;
        }
        if (isEnd(string)) {
            return true;
        }
        return false;
    }

}
