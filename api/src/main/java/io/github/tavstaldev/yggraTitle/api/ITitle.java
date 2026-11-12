package io.github.tavstaldev.yggraTitle.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Public API for displaying Minecraft title and subtitle messages.
 *
 * <p>The interface exposes two families of methods: string-based overloads that accept legacy
 * color codes (for example {@code &6} or {@code §6}) and MiniMessage tags, and component-based
 * overloads that take pre-built Adventure {@link Component}s. String-based overloads also support
 * {@code %key%} placeholder replacement through an optional argument map.</p>
 *
 * <p>Implementations target specific server versions and are selected at runtime by the
 * {@code io.github.tavstaldev.yggraTitle.bootstrap.YggraTitle} facade. Application code should
 * depend on this interface rather than on a concrete implementation.</p>
 *
 * @see io.github.tavstaldev.yggraTitle.api.utils.AdventureUtils
 */
@SuppressWarnings("unused")
public interface ITitle {

    /**
     * Displays a title and subtitle for the given player using the implementation's default timing.
     *
     * <p>The default timing is a 500&nbsp;ms fade-in, a 2&nbsp;second stay, and a 1&nbsp;second
     * fade-out.</p>
     *
     * @param player   the player who should see the title
     * @param title    the main title text; legacy color codes and MiniMessage tags are supported
     * @param subTitle the subtitle text; legacy color codes and MiniMessage tags are supported
     */
    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle);

    /**
     * Displays a title and subtitle after replacing {@code %key%} placeholders in both texts.
     *
     * <p>Keys in {@code arguments} may be supplied with or without the surrounding percent signs,
     * so {@code player} and {@code %player%} are equivalent. Values are converted to strings with
     * {@link Object#toString()} before replacement.</p>
     *
     * @param player    the player who should see the title
     * @param title     the main title text, possibly containing placeholders
     * @param subTitle  the subtitle text, possibly containing placeholders
     * @param arguments placeholder keys mapped to their replacement values
     */
    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments);

    /**
     * Displays a title and subtitle for the given player using custom timing.
     *
     * @param player   the player who should see the title
     * @param title    the main title text; legacy color codes and MiniMessage tags are supported
     * @param subTitle the subtitle text; legacy color codes and MiniMessage tags are supported
     * @param times    the fade-in, stay, and fade-out durations
     */
    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Title.Times times);

    /**
     * Displays a title and subtitle with both placeholder replacement and custom timing.
     *
     * @param player    the player who should see the title
     * @param title     the main title text, possibly containing placeholders
     * @param subTitle  the subtitle text, possibly containing placeholders
     * @param arguments placeholder keys mapped to their replacement values
     * @param times     the fade-in, stay, and fade-out durations
     */
    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments, @NotNull Title.Times times);

    /**
     * Displays pre-built Adventure components as a title and subtitle using the default timing.
     *
     * <p>The components are sent as-is; no legacy code parsing or placeholder replacement is
     * performed.</p>
     *
     * @param player   the player who should see the title
     * @param title    the main title component
     * @param subTitle the subtitle component
     */
    void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle);

    /**
     * Displays pre-built Adventure components as a title and subtitle using custom timing.
     *
     * <p>The components are sent as-is; no legacy code parsing or placeholder replacement is
     * performed.</p>
     *
     * @param player   the player who should see the title
     * @param title    the main title component
     * @param subTitle the subtitle component
     * @param times    the fade-in, stay, and fade-out durations
     */
    void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle, @NotNull Title.Times times);
}
