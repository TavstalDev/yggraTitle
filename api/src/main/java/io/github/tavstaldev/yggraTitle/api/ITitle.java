package io.github.tavstaldev.yggraTitle.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("unused")
public interface ITitle {

    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle);

    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments);

    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Title.Times times);

    void send(@NotNull Player player, @NotNull String title, @NotNull String subTitle, @NotNull Map<String, Object> arguments, @NotNull Title.Times times);

    void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle);

    void send(@NotNull Player player, @NotNull Component title, @NotNull Component subTitle, @NotNull Title.Times times);
}
