package net.forsaken_borders;

import net.fabricmc.api.DedicatedServerModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.Properties;

public class Fabrissentials implements DedicatedServerModInitializer {

	private static final Properties DATABASE_PROPERTIES = new Properties();
	private static final int DATABASE_VERSION = 1;

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
					//TODO: Create the needed Tables here. If we use statements with

					LOGGER.info("new db");

					// This just lets us check if the Database is from an old version of the mod. If so, it will be migrated below
					// We can't use a PreparedStatement here. Java is dumb and throws org.sqlite.SQLiteException: SQL error or missing database (near "?": syntax error)
					Statement statement = databaseConnection.createStatement();
					statement.execute("PRAGMA user_version = " + DATABASE_VERSION + ";");
					statement.close();

				} else {
					// Check if the Database was created with an older version of SQLite. If yes, we should migrate it.

					Statement statement = databaseConnection.createStatement();
					ResultSet result = statement.executeQuery("PRAGMA user_version;");

					if (!result.next()) {
						//TODO: No Database version was set. What do we do?
						//LOGGER.warn("how??");
					}

					if (result.getInt("user_version") != DATABASE_VERSION) {
						//TODO: If we land here, we need to migrate!
						//LOGGER.warn("The Database needs to be updated. This might take a while!");
					}
				}
			} catch (SQLException exception) {
				LOGGER.error("An Error occurred trying to load the Database!", exception);
				return;
			}

			LOGGER.info("db open");
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

			LOGGER.info("db closed");
		});

		LOGGER.info("Hello Fabric world!");
	}
}