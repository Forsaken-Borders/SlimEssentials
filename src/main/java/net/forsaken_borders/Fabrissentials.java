package net.forsaken_borders;

import java.sql.Connection;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.forsaken_borders.annotations.EssentialCommandProcessor;

public class Fabrissentials implements DedicatedServerModInitializer {

	public static @Nullable Connection databaseConnection;
	public static final Logger LOGGER = LoggerFactory.getLogger("fabrissentials");

	@Override
	public void onInitializeServer() {
		MidnightConfig.init("fabrissentials", FabrissentialsConfig.class);

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			// This method will create the database, migrate it if required or create the
			// tables if the database is new.
			DatabaseManager.openDatabase();

			// Pass the MinecraftServer to our Annotation Processor,
			// so it knows what Server to register our Commands to
			EssentialCommandProcessor.server = server;

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