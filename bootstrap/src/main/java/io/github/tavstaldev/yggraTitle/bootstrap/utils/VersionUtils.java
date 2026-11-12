package io.github.tavstaldev.yggraTitle.bootstrap.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Method;

/**
 * Utility methods for reading and comparing the running Minecraft server version.
 *
 * <p>The detected version is cached after the first lookup, so repeated calls to
 * {@link #getServerVersion()} are cheap and consistent. Version strings are compared
 * numerically, component by component (major, minor, patch).</p>
 */
@ApiStatus.Internal
public class VersionUtils {
    private static String _version = null;

    /**
     * Returns the running server version, detecting it on the first call and caching the result.
     *
     * <p>On servers that provide {@code Bukkit#getMinecraftVersion()}, that method is used.
     * Older servers fall back to parsing the package name of the server implementation.</p>
     *
     * @return the server version string, for example {@code "1.8.8"} or {@code "1.21.4"}
     */
    public static String getServerVersion() {
        if (_version != null) {
            return _version;
        }

        String version;
        try {
            Method method = Bukkit.class.getDeclaredMethod("getMinecraftVersion");
            version = (String) method.invoke(null);
        } catch (Exception e) {
            // Fallback for older versions
            version = Bukkit.getServer().getClass().getPackage().getName()
                    .split("\\.")[3]
                    .replace("_", ".")
                    .replaceAll("[A-Z]|[a-z]", "");
        }
        _version = version;
        return version;
    }

    /**
     * Checks whether the server version is at least the specified major and minor version.
     *
     * @param major the major version to compare against
     * @param minor the minor version to compare against
     * @return {@code true} if the server version is equal to or newer than the given version
     */
    public static boolean isAtLeast(int major, int minor) {
        String[] parts = getServerVersion().split("\\.");
        int maj = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        return (maj > major) || (maj == major && min >= minor);
    }

    /**
     * Checks whether the server version is at least the specified major, minor, and patch version.
     *
     * <p>If the server version does not contain a patch component, it is treated as {@code 0}.</p>
     *
     * @param major the major version to compare against
     * @param minor the minor version to compare against
     * @param patch the patch version to compare against
     * @return {@code true} if the server version is equal to or newer than the given version
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        String[] parts = getServerVersion().split("\\.");
        int maj = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        int pat;
        if (parts.length > 2) {
            pat = Integer.parseInt(parts[2]);
        } else {
            pat = 0; // Default to 0 if patch version is not specified
        }
        return (maj > major)
                || (maj == major && min > minor)
                || (maj == major && min == minor && pat >= patch);
    }
}
