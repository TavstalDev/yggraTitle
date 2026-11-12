package io.github.tavstaldev.yggraTitle.test;

import io.github.tavstaldev.yggraTitle.bootstrap.YggraTitle;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Minimal example plugin used to exercise yggraTitle on a real server.
 *
 * <p>The constructor loads the required Adventure modules at runtime with Libby, and
 * {@link #onEnable()} creates the {@link YggraTitle} facade. Joining players receive a sample
 * title that mixes legacy color codes with MiniMessage tags.</p>
 */
@SuppressWarnings("unused")
public class TestPlugin extends JavaPlugin implements Listener {

    /** Library manager that downloads and loads the Adventure modules at runtime. */
    protected final BukkitLibraryManager libraryManager;

    /** Version-independent title facade used by this example plugin. */
    private YggraTitle _bar;

    /**
     * Creates the plugin and loads every Adventure module needed by yggraTitle.
     */
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

    /**
     * Creates the title facade and registers the join listener.
     */
    @Override
    public void onEnable() {
        _bar = new YggraTitle(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("The plugin has been loaded.");
    }

    /**
     * Logs plugin shutdown.
     */
    @Override
    public void onDisable() {
        getLogger().info("The plugin has been unloaded.");
    }

    /**
     * Sends a sample title to a player when they join.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        _bar.send(event.getPlayer(), "&4Welcome <blue>" + event.getPlayer().getDisplayName(), "§nto the server!");
    }
}
