/**
 * Created by mark.banierink on 18-1-2017.
 */
public class CommunicationToolbox {

    public static String[] splitString(String string) {
        return string.split(" ");
    }

    public static boolean isKeyword(String word) {
        for (Keyword keyword : Keyword.values()) {
            if (word.equals(keyword.toString())) {
                return true;
            }
        }
        return false;
    }

    public static Keyword getKeyword(String[] split) {
        Keyword result = null;
        if (numArguments(split) > 0) {
            if (isKeyword(split[0])) {
                result = Keyword.valueOf(split[0]);
            }
        }
        return result;
    }

    private static int numArguments(String[] split) {
        return split.length;
    }

    private static boolean stringIsInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean commandGoIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) == 3) && (split[1] != null) && (stringIsInteger(split[2])));
        }
        return valid;
    }

    public static boolean commandCancelIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) == 1));
        }
        return valid;
    }

    public static boolean commandMoveIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) == 3) && (stringIsInteger(split[1])) && (stringIsInteger(split[2])));
        }
        return valid;
    }

    public static boolean commandPassIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) == 1));
        }
        return valid;
    }

    public static boolean commandTableflipIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) == 1));
        }
        return valid;
    }

    public static boolean commandChatIsValid(String string) {
        boolean valid = false;
        String[] split = splitString(string);
        if (getKeyword(split)!= null) {
            valid = ((numArguments(split) > 1));
        }
        return valid;
    }

//    private static boolean commandGoIsValid(String[] split) {
//        return ((numArguments(split) == 3) && (split[1] != null) && (stringIsInteger(split[2])));
//    }
//
//    private static boolean commandCancelIsValid(String[] split) {
//        return ((numArguments(split) == 1));
//    }
//
//    private static boolean commandMoveIsValid(String[] split) {
//        return ((numArguments(split) == 3) && (stringIsInteger(split[1])) && (stringIsInteger(split[2])));
//    }
//
//    private static boolean commandPassIsValid(String[] split) {
//        return ((numArguments(split) == 1));
//    }
//
//    private static boolean commandTableflipIsValid(String[] split) {
//        return ((numArguments(split) == 1));
//    }
//
//    private static boolean commandChatIsValid(String[] split) {
//        return ((numArguments(split) > 1));
//    }
//
    public static String[] string2Command(String string) {
        String[] split = splitString(string);
        Keyword keyword = getKeyword(split);
        String[] command = null;
        if (keyword != null) {
            switch (keyword) {
                case GO:
                    if (commandGoIsValid(string)) {
                        command = split;
                    }
                    break;
                case CANCEL:
                    if (commandCancelIsValid(string)) {
                        command = split;
                    }
                    break;
                case MOVE:
                    if (commandMoveIsValid(string)) {
                        command = split;
                    }
                    break;
                case PASS:
                    if (commandPassIsValid(string)) {
                        command = split;
                    }
                    break;
                case TABLEFLIP:
                    if (commandTableflipIsValid(string)) {
                        command = split;
                    }
                    break;
                case CHAT:
                    if (commandChatIsValid(string)) {
                        command = split;
                    }
                    break;
            }
        }
        return command;
    }




//    public static void handleClientCommand(String string) {
//        Keyword keyword = CommunicationToolbox.getKeyword(string);
//        if (keyword != null) {
//            switch (keyword) {
//                case GO:
//                    if (commandIsValid(keyword, string)) {
//                        //commandGo();
//                    }
//                    break;
//                case CANCEL:
//                    break;
//                case MOVE:
//                    //commandMove(player, x, y);
//                    break;
//                case PASS:
//                    break;
//                case TABLEFLIP:
//                    break;
//                case CHAT:
//                    break;
//            }
//        }
//    }

}
