package tech.codealpha.tasks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Minimal JSON helpers for the flat task payloads used by this API. */
public final class JsonUtil {
    private JsonUtil() {
    }

    public static String toJson(Task task) {
        return "{\"id\":" + task.id() + ",\"title\":\"" + escape(task.title()) + "\",\"done\":" + task.done() + "}";
    }

    public static String toJson(List<Task> tasks) {
        return tasks.stream().map(JsonUtil::toJson).collect(Collectors.joining(",", "[", "]"));
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    public static String escape(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /** Returns the string value of a top-level key, or null if absent. */
    public static String extractString(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(json);
        return m.find() ? unescape(m.group(1)) : null;
    }

    /** Returns the boolean value of a top-level key, or null if absent. */
    public static Boolean extractBoolean(String json, String key) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(true|false)").matcher(json);
        return m.find() ? Boolean.valueOf(m.group(1)) : null;
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i == s.length() - 1) {
                sb.append(c);
                continue;
            }
            char n = s.charAt(++i);
            switch (n) {
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'u' -> {
                    if (i + 4 < s.length()) {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    } else {
                        sb.append("\\u");
                    }
                }
                default -> sb.append(n);
            }
        }
        return sb.toString();
    }
}
