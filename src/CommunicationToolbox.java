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

    private static boolean commandGoIsValid(String[] split) {
        return ((numArguments(split) == 3) && (split[1] != null) && (stringIsInteger(split[2])));
    }

    private static boolean commandCancelIsValid(String[] split) {
        return ((numArguments(split) == 1));
    }

    private static boolean commandMoveIsValid(String[] split) {
        return ((numArguments(split) == 3) && (stringIsInteger(split[1])) && (stringIsInteger(split[2])));
    }

    private static boolean commandPassIsValid(String[] split) {
        return ((numArguments(split) == 1));
    }

    private static boolean commandTableflipIsValid(String[] split) {
        return ((numArguments(split) == 1));
    }

    private static boolean commandChatIsValid(String[] split) {
        return ((numArguments(split) > 1));
    }

    public static String[] string2Command(String string) {
        String[] split = splitString(string);
        Keyword keyword = getKeyword(split);
        String[] result = null;
        if (keyword != null) {
            switch (keyword) {
                case GO:
                    if (commandGoIsValid(split)) {
                        result = split;
                    }
                    break;
                case CANCEL:
                    if (commandCancelIsValid(split)) {
                        result = split;
                    }
                    break;
                case MOVE:
                    if (commandMoveIsValid(split)) {
                        result = split;
                    }
                    break;
                case PASS:
                    if (commandPassIsValid(split)) {
                        result = split;
                    }
                    break;
                case TABLEFLIP:
                    if (commandTableflipIsValid(split)) {
                        result = split;
                    }
                    break;
                case CHAT:
                    if (commandChatIsValid(split)) {
                        result = split;
                    }
                    break;
            }
        }
        return result;
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
