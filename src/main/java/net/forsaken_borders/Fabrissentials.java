package net.forsaken_borders;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Fabrissentials implements DedicatedServerModInitializer {

	private static final Properties DATABASE_PROPERTIES = new Properties();

	public static @Nullable Connection databaseConnection;
	public static final Logger LOGGER = LoggerFactory.getLogger("fabrissentials");

	@Override
	public void onInitializeServer() {

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			//TODO: Make the Database File Path changeable via a Config
			try {
				boolean newDatabase = new File("Database.db").exists();

				// The Commands that are run are from https://cj.rs/blog/sqlite-pragma-cheatsheet-for-performance-and-consistency/
				// Archived Site: https://web.archive.org/web/20230120235214/https://cj.rs/blog/sqlite-pragma-cheatsheet-for-performance-and-consistency/
				// These basically make the Database more performant and ensure integrity
				// I've tried running these as normal Statements, like you should/can in "normal SQLite" but they throw various exceptions, so this will have to do.
				DATABASE_PROPERTIES.setProperty("journal_mode", "WAL");
				DATABASE_PROPERTIES.setProperty("synchronous", "normal");
				DATABASE_PROPERTIES.setProperty("foreign_keys", "on");

				databaseConnection = DriverManager.getConnection("jdbc:sqlite:Database.db", DATABASE_PROPERTIES);
				if (databaseConnection == null) {
					LOGGER.error("Database Connection is NULL!");
					return;
				}

				if (newDatabase) {
					LOGGER.info("Seems like this is the first time you are running Fabrissentials.");
					LOGGER.info("Creating a Database, this might take a while...");

					Statement statement = databaseConnection.createStatement();

					statement.execute("CREATE TABLE IF NOT EXISTS \"Homes\" (\"HomeID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");
					statement.execute("ALTER TABLE \"Homes\" ADD CONSTRAINT \"UniqueHomeIdPerPlayer\" UNIQUE (\"HomeID\", \"PlayerID\");");

					statement.execute("CREATE TABLE IF NOT EXISTS \"Warps\" (\"WarpID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");
					statement.execute("ALTER TABLE \"Warps\" ADD CONSTRAINT \"UniqueWarpId\" UNIQUE (\"WarpID\");");

					statement.close();
				}
			} catch (SQLException exception) {
				LOGGER.error("An Error occurred trying to load the Database!", exception);
				return;
			}

			LOGGER.info("Database is ready.");
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			if (databaseConnection != null) {
				try {
					Statement statement = databaseConnection.createStatement();
					statement.execute("PRAGMA analysis_limit=1000;");
					statement.execute("PRAGMA optimize;");
					statement.close();
				} catch (SQLException exception) {
					LOGGER.warn("An Error occurred trying to prepare the Database for closing. This is usually not a big problem!", exception);
				}
			}

			if (databaseConnection != null) {
				try {
					databaseConnection.close();
				} catch (SQLException exception) {
					LOGGER.error("An Error occurred trying to close the Database, some Data may be lost!", exception);
				}
			}

			LOGGER.info("Database closed. Goodbye!");
		});

		LOGGER.info("Hello Fabric world!");
	}
}