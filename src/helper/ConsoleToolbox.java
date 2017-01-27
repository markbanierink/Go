package helper;

import static helper.ComToolbox.*;

/**
 * Created by mark.banierink on 25-1-2017.
 */
public class ConsoleToolbox {

    public static int requestIntegerInput(ConsoleReader consoleReader, String request, int defaultValue, int[] range) {
        int result = defaultValue;
        String answer = consoleReader.readString(request + " (" + range[0] + "-" + range[1] + ", default " + defaultValue + "): ");
        if (!answer.isEmpty()) {
            if (isInteger(answer)) {
                int number = Integer.parseInt(answer);
                if (number >= range[0] && number <= range[1]) {
                    result = number;
                }
            }
        }
        return result;
    }

    public static boolean requestBooleanInput(ConsoleReader consoleReader, String request, String defaultValue) {
        String defaultString = "";
        if (defaultValue != null) {
            defaultString = ", default " + defaultValue;
        }
        while (true) {
            String answer = consoleReader.readString(request + " (y/n" + defaultString + "): ");
            if (answer.equals("y")) {
                return true;
            }
            else if (answer.equals("n")) {
                return false;
            }
            else if (defaultValue != null) {
                if (defaultValue.equals("y")) {
                    return true;
                }
                else if (defaultValue.equals("n")) {
                    return false;
                }
            }
        }
    }

    public static String requestStringInput(ConsoleReader consoleReader, String request, String defaultValue) {
        String defaultString = "";
        if (defaultValue != null) {
            defaultString = " (default \'" + defaultValue + "\')";
        }
        while (true) {
            String answer = consoleReader.readString(request + defaultString + ": ");
            if (!answer.isEmpty()) {
                return answer;
            }
            else if (defaultValue != null) {
                return defaultValue;
            }
        }
    }
}
