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
        send(player, title, subTitle, defaultTimes);
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

    private static Object createChatComponent(String text) throws Exception {
        Class<?> chatSerializerClass = getNMSClass("IChatBaseComponent$ChatSerializer");
        Method aMethod = chatSerializerClass.getMethod("a", String.class);
        return aMethod.invoke(null, "{\"text\":\"" + text + "\"}");
    }

    private static Object createTitlePacket(String actionName, Object chatComponent, int fadeIn, int stay, int fadeOut) throws Exception {
        Class<?> packetClass = getNMSClass("PacketPlayOutTitle");
        Class<?> actionClass = getNMSClass("PacketPlayOutTitle$EnumTitleAction");

        Object action = actionClass.getField(actionName).get(null);

        Constructor<?> constructor = packetClass.getConstructor(actionClass, getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
        return constructor.newInstance(action, chatComponent, fadeIn, stay, fadeOut);
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
