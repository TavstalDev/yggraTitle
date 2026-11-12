package io.github.tavstaldev.yggraTitle.impl.v1_11;

import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.api.utils.AdventureUtils;
import io.github.tavstaldev.yggraTitle.api.utils.ArgumentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ITitle} implementation for Minecraft 1.11 and newer servers.
 *
 * <p>Those versions expose the native {@link Player#sendTitle(String, String, int, int, int)}
 * API, so no NMS access is required. Adventure components are converted to section-sign legacy
 * text before they are passed to Bukkit.</p>
 *
 * <p>Durations are converted from milliseconds to ticks (50&nbsp;ms per tick) and rounded to the
 * nearest tick.</p>
 */
public class Title implements ITitle {
    private static final net.kyori.adventure.title.Title.Times defaultTimes = net.kyori.adventure.title.Title.Times.times(
            Duration.ofMillis(500),   // Fade In: 0.5 seconds
            Duration.ofSeconds(2),    // Stay: 2 seconds
            Duration.ofSeconds(1)     // Fade Out: 1 second
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        send(player, title, subTitle, new HashMap<>(), defaultTimes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments) {
        send(player, title, subTitle, arguments, defaultTimes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull net.kyori.adventure.title.Title.Times times) {
        send(player, title, subTitle, new HashMap<>(), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments, @NotNull net.kyori.adventure.title.Title.Times times) {
        String t = ArgumentUtils.apply(title, arguments);
        String s = ArgumentUtils.apply(subTitle, arguments);
        send(player, AdventureUtils.parse(t), AdventureUtils.parse(s), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle) {
        send(player, title, subTitle, defaultTimes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle, @NotNull net.kyori.adventure.title.Title.Times times) {
        int fadeIn = Math.round((float) times.fadeIn().toMillis() / 50);
        int stay = Math.round((float) times.stay().toMillis() / 50);
        int fadeOut = Math.round((float) times.fadeOut().toMillis() / 50);
        String t = LegacyComponentSerializer.legacySection().serialize(title);
        String s = LegacyComponentSerializer.legacySection().serialize(subTitle);
        player.sendTitle(t, s, fadeIn, stay, fadeOut);
    }
}
