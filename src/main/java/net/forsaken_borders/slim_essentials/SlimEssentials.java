package net.forsaken_borders.slim_essentials;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class SlimEssentials implements DedicatedServerModInitializer {
	public static @Nullable Connection databaseConnection;
	public static final Logger LOGGER = LoggerFactory.getLogger("slim_essentials");

	@Override
	public void onInitializeServer() {
		MidnightConfig.init("slim_essentials", SlimEssentialsConfig.class);

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			// This method will create the database, migrate it if required or create the
			// tables if the database is new.
			DatabaseManager.openDatabase();

			// Register all commands after the database has been created.
			CommandHandler.registerCommands(server.getCommandManager().getDispatcher());
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			// Saves the data, performs "optimization" and closes the database.
			DatabaseManager.closeDatabase();
		});

		LOGGER.info("Hello Fabric world!");
	}
}