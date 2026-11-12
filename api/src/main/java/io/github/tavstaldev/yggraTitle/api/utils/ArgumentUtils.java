package io.github.tavstaldev.yggraTitle.api.utils;

import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.Set;

/**
 * Utility methods for replacing named placeholders in message strings.
 *
 * <p>Placeholders use the {@code %key%} syntax. This class is shared by the version-specific
 * title implementations so that placeholder handling is identical on every server version.</p>
 */
public class ArgumentUtils {

    /**
     * Replaces every {@code %key%} placeholder in {@code message} with the corresponding value from
     * {@code parameters}.
     *
     * <p>Keys may be written with or without the surrounding percent signs, so a key of
     * {@code player} and a key of {@code %player%} both match {@code %player%} in the message.
     * Each value is converted to a string with {@link Object#toString()}. Placeholders without a
     * matching key are left untouched.</p>
     *
     * @param message    the message whose placeholders should be replaced
     * @param parameters the placeholder keys mapped to their replacement values
     * @return the message with all matching placeholders replaced
     */
    public static String apply(String message, Map<String, Object> parameters) {
        String msg = message;

        // Get the keys
        Set<String> keys = parameters.keySet();
        for (@RegExp String dirKey : keys) {
            @RegExp String finalKey;
            if (dirKey.startsWith("%"))
                finalKey = dirKey;
            else
                finalKey = "%" + dirKey + "%";
            msg = msg.replace(finalKey, parameters.get(dirKey).toString());
        }

        return msg;
    }
}
