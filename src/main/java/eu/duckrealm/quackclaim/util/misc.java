package eu.duckrealm.quackclaim.util;

public class misc {
    public static boolean OnOffToBoolean(String onOff) {
        return switch (onOff.toLowerCase()) {
            case "on" -> true;
            case "yes" -> true;
            case "y" -> true;
            default -> false;
        };
    }
}
