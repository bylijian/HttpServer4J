package util;

public class Log {

    public static void debug(String tag, String text) {
        System.out.println(String.format("TAG: %s, Log: %s", tag, text));
    }

}
