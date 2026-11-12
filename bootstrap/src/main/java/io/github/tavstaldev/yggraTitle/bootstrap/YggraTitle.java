package io.github.tavstaldev.yggraTitle.bootstrap;

import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.bootstrap.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Version-independent facade for sending Minecraft title and subtitle messages.
 *
 * <p>When an instance is created, the running server version is detected and the matching
 * {@link ITitle} implementation is loaded through reflection. Supported servers are Minecraft
 * 1.8 and newer:</p>
 *
 * <ul>
 *     <li>1.11+ uses {@code impl_v1_11}, which delegates to the native
 *     {@code Player#sendTitle(String, String, int, int, int)} API.</li>
 *     <li>1.8 &ndash; 1.10.x uses {@code impl_v1_8_8}, which sends NMS
 *     {@code PacketPlayOutTitle} packets.</li>
 * </ul>
 *
 * <p>If the server version is unsupported or the implementation cannot be loaded, a warning is
 * logged and every {@code send} call becomes a no-op. This keeps a missing implementation from
 * crashing the host plugin. Create one instance during {@code onEnable} and reuse it for the
 * lifetime of the plugin.</p>
 */
public class YggraTitle implements ITitle {
    private ITitle _title;

    /**
     * Creates a facade and selects the implementation that matches the running server version.
     *
     * @param plugin the owning plugin, used to log a warning when no implementation can be loaded
     */
    public YggraTitle(JavaPlugin plugin) {
        try {
            String implClassName = getImplClassName();
            _title = (ITitle) Class.forName(implClassName)
                    .getDeclaredConstructor().newInstance();
        }
        catch (Exception | LinkageError ex) {
            plugin.getLogger().warning("Failed to get the right title implementation for this server version.\n" + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments, @NotNull Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, arguments, times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle, @NotNull Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, times);
    }

    /**
     * Resolves the fully qualified class name of the implementation for the running server.
     *
     * @return the implementation class name
     * @throws RuntimeException if the server is older than Minecraft 1.8
     */
    private static @NotNull String getImplClassName() {
        String implClassName;

        if (VersionUtils.isAtLeast(1, 11))
            implClassName = "io.github.tavstaldev.yggraTitle.impl.v1_11.Title";
        else if (VersionUtils.isAtLeast(1, 8))
            implClassName = "io.github.tavstaldev.yggraTitle.impl.v1_8_8.Title";
        else
            throw new RuntimeException("Unsupported Minecraft version: " + Bukkit.getBukkitVersion());

        return implClassName;
    }
}
