package io.github.tavstaldev.yggraTitle.api.utils;

import org.intellij.lang.annotations.RegExp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ArgumentUtils {

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
