package io.github.tavstaldev.yggraTitle.test;

import io.github.tavstaldev.yggraTitle.bootstrap.YggraTitle;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin implements Listener {

    protected final BukkitLibraryManager libraryManager;
    private YggraTitle _bar;

    public TestPlugin() {
        libraryManager = new BukkitLibraryManager(this);
        libraryManager.addMavenCentral();
        libraryManager.addSonatype();
        libraryManager.addRepository("https://jitpack.io");

        //#region Adventure-API

        // net.kyori:examination-api
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-api")
                .version("1.3.0")
                .build());

        // net.kyori:examination-string
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("examination-string")
                .version("1.3.0")
                .build());

        // net.kyori:adventure-key
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-key")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-api - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-api")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-legacy - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-legacy")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-minimessage - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-plain - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-plain")
                .version("4.26.1")
                .build());

        // net.kyori:adventure-text-serializer-gson - Required for 1.8.8 support
        libraryManager.loadLibrary(Library.builder()
                .groupId("net{}kyori")
                .artifactId("adventure-text-serializer-gson")
                .version("4.26.1")
                .build());

        //#endregion
    }

    @Override
    public void onEnable() {
        _bar = new YggraTitle(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has been loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has been unloaded.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        _bar.send(event.getPlayer(), "&4Welcome <blue>" + event.getPlayer().getDisplayName(), "§nto the server!");
    }
}
