package helper;

import static helper.CommandToolbox.*;

/**
 * Created by mark.banierink on 25-1-2017.
 *
 * @author Mark Banierink
 */
public class ConsoleToolbox {

    public static int requestIntegerInput(ConsoleReader consoleReader, String request, int defaultValue, int minValue, int maxValue) {
        int result = defaultValue;
        String answer = consoleReader.readString(request + " (" + minValue + "-" + maxValue + ", default " + defaultValue + "): ");
        if (!answer.isEmpty()) {
            if (isInteger(answer)) {
                int number = Integer.parseInt(answer);
                if (number >= minValue && number <= maxValue) {
                    result = number;
                }
            }
        }
        return result;
    }

    public static boolean requestBooleanInput(ConsoleReader consoleReader, String request, String defaultValue) {
        boolean result = false;
        boolean answered = false;
        String defaultString = "";
        if (defaultValue != null) {
            defaultString = ", default " + defaultValue;
        }
        while (!answered) {
            String answer = consoleReader.readString(request + " (y/n" + defaultString + "): ");
            if (answer.equals("y")) {
                result = true;
                answered = true;
            }
            else if (answer.equals("n")) {
                result = false;
                answered = true;
            }
            else if (defaultValue != null) {
                if (defaultValue.equals("y")) {
                    result = true;
                }
                else if (defaultValue.equals("n")) {
                    result = false;
                }
                answered = true;
            }
        }
        return result;
    }

    public static String requestStringInput(ConsoleReader consoleReader, String request, String defaultValue) {
        String result;
        String defaultString = "";
        if (defaultValue != null) {
            defaultString = " (default \'" + defaultValue + "\')";
        }
        while (true) {
            String answer = consoleReader.readString(request + defaultString + ": ");
            if (!answer.isEmpty()) {
                result = answer;
                break;
            }
            else if (defaultValue != null) {
                result = defaultValue;
                break;
            }
        }
        return result;
    }
}
