package io.github.tavstaldev.yggraTitle.impl.v1_8_8;

import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.api.utils.AdventureUtils;
import io.github.tavstaldev.yggraTitle.api.utils.ArgumentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ITitle} implementation for Minecraft 1.8 through 1.10.x servers.
 *
 * <p>Those server versions do not expose a native Adventure title API, so this implementation
 * sends the legacy NMS {@code PacketPlayOutTitle} packet through reflection. The packet class is
 * looked up in the versioned {@code net.minecraft.server.<version>} package at runtime, which
 * keeps the module free of compile-time NMS dependencies.</p>
 *
 * <p>Components are serialized to section-sign legacy text before they are sent. If a packet
 * cannot be constructed or delivered, the failure is logged to standard error and the call is
 * ignored so the host plugin keeps running.</p>
 */
@SuppressWarnings("CallToPrintStackTrace")
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

        sendNmsTitle(player, t, s, fadeIn, stay, fadeOut);
    }

    /**
     * Sends the title, subtitle, and timing packets to the given player.
     *
     * @param player   the receiving player
     * @param title    the section-sign encoded title text
     * @param subtitle the section-sign encoded subtitle text
     * @param fadeIn   fade-in duration in ticks
     * @param stay     stay duration in ticks
     * @param fadeOut  fade-out duration in ticks
     */
    private static void sendNmsTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            // Send Times packet if arguments are provided
            if (fadeIn >= 0 && stay >= 0 && fadeOut >= 0) {
                Object timesPacket = createTitlePacket("TIMES", null, fadeIn, stay, fadeOut);
                sendPacket(player, timesPacket);
            }

            // Send Subtitle packet
            if (subtitle != null) {
                Object subTitleComponent = createChatComponent(subtitle);
                Object subTitlePacket = createTitlePacket("SUBTITLE", subTitleComponent, -1, -1, -1);
                sendPacket(player, subTitlePacket);
            }

            // Send Title packet
            if (title != null) {
                Object titleComponent = createChatComponent(title);
                Object titlePacket = createTitlePacket("TITLE", titleComponent, -1, -1, -1);
                sendPacket(player, titlePacket);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates an NMS chat component containing the given legacy text.
     *
     * @param text the section-sign encoded text
     * @return the NMS {@code IChatBaseComponent} instance
     * @throws Exception if the NMS serializer cannot be resolved or invoked
     */
    private static Object createChatComponent(String text) throws Exception {
        Class<?> chatSerializerClass = getNMSClass("IChatBaseComponent$ChatSerializer");
        Method aMethod = chatSerializerClass.getMethod("a", String.class);
        return aMethod.invoke(null, "{\"text\":\"" + text + "\"}");
    }

    /**
     * Creates an NMS {@code PacketPlayOutTitle} for the given action.
     *
     * @param actionName    the {@code EnumTitleAction} field name, such as {@code TITLE},
     *                      {@code SUBTITLE}, or {@code TIMES}
     * @param chatComponent the component to display, or {@code null} for the {@code TIMES} action
     * @param fadeIn        fade-in duration in ticks, or {@code -1} to leave it unchanged
     * @param stay          stay duration in ticks, or {@code -1} to leave it unchanged
     * @param fadeOut       fade-out duration in ticks, or {@code -1} to leave it unchanged
     * @return the constructed packet
     * @throws Exception if the packet classes or constructor cannot be resolved
     */
    private static Object createTitlePacket(String actionName, Object chatComponent, int fadeIn, int stay, int fadeOut) throws Exception {
        Class<?> packetClass = getNMSClass("PacketPlayOutTitle");
        Class<?> actionClass = getNMSClass("PacketPlayOutTitle$EnumTitleAction");

        Object action = actionClass.getField(actionName).get(null);

        Constructor<?> constructor = packetClass.getConstructor(actionClass, getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        return constructor.newInstance(action, chatComponent, fadeIn, stay, fadeOut);
    }

    /**
     * Delivers an NMS packet to the given player through its player connection.
     *
     * @param player the receiving player
     * @param packet the packet to send
     */
    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolves an NMS class from the versioned {@code net.minecraft.server.<version>} package.
     *
     * @param name the simple class name, for example {@code PacketPlayOutTitle}
     * @return the resolved class, or {@code null} if it cannot be found
     */
    private static Class<?> getNMSClass(String name) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
