package net.forsaken_borders;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import net.minecraft.text.Text;

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
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			// Saves the data, performs "optimization" and closes the database.
			DatabaseManager.closeDatabase();
		});

		LOGGER.info(Text.translatable("command.test.testing").getString());
	}
}