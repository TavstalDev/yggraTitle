package io.github.tavstaldev.yggraTitle.bootstrap;

import io.github.tavstaldev.yggraTitle.api.ITitle;
import io.github.tavstaldev.yggraTitle.bootstrap.utils.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class YggraTitle implements ITitle {
    private ITitle _title;

    public YggraTitle(JavaPlugin plugin)  {
        try {
            String implClassName = getImplClassName();
            _title = (ITitle) Class.forName(implClassName)
                    .getDeclaredConstructor().newInstance();
        }
        catch (Exception | LinkageError ex) {
            plugin.getLogger().warning("Failed to get the right title implementation for this server version.\n" + ex);
        }
    }

    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle);
    }

    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, arguments);
    }

    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull  net.kyori.adventure.title.Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, times);
    }

    @Override
    public void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments, net.kyori.adventure.title.Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, arguments, times);
    }

    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle);
    }

    @Override
    public void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle, @NotNull net.kyori.adventure.title.Title.Times times) {
        if (_title == null)
            return;
        _title.send(player, title, subTitle, times);
    }

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
