package net.forsaken_borders;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
	private static final int DATABASE_VERSION = 1;
	private static final Properties DATABASE_PROPERTIES = new Properties();

	public static final Logger LOGGER = LoggerFactory.getLogger("fabrissentials.database");

	public static void createDatabase() {
		// TODO: Make the Database File Path changeable via a Config
		boolean newDatabase = !(new File("database.db").exists());

		// The Commands that are run are from
		// https://cj.rs/blog/sqlite-pragma-cheatsheet-for-performance-and-consistency/
		// Archived Site:
		// https://web.archive.org/web/20230120235214/https://cj.rs/blog/sqlite-pragma-cheatsheet-for-performance-and-consistency/
		// These basically make the Database more performant and ensure integrity
		// I've tried running these as normal Statements, like you should/can in "normal
		// SQLite" but they throw various exceptions, so this will have to do.
		DATABASE_PROPERTIES.setProperty("journal_mode", "WAL");
		DATABASE_PROPERTIES.setProperty("synchronous", "normal");
		DATABASE_PROPERTIES.setProperty("foreign_keys", "on");

		try {
			Fabrissentials.databaseConnection = DriverManager.getConnection("jdbc:sqlite:database.db",
					DATABASE_PROPERTIES);
		} catch (Exception error) {
			LOGGER.error("Failed to create the database connection!", error);
			return;
		}

		if (newDatabase) {
			// If the database is new, we need to create the tables
			populateDatabase();
		} else {
			// If the database is not new, we need to transfer the data from the old
			// database into the new database format.
			migrate();
		}
	}

	public static void migrate() {
		try {
			assert Fabrissentials.databaseConnection != null;
			Statement statement = Fabrissentials.databaseConnection.createStatement();
			ResultSet versionResult = statement.executeQuery("PRAGMA schema.user_version;");
			int version = versionResult.getInt("user_version");

			switch (version) {
				case DATABASE_VERSION:
					// Currently we're on this version and no work needs to be done.
					break;
				default:
					// We're on an unknown version, likely a newer version than the current one.
					// Do NOT attempt to touch the database as it may cause data loss.
					LOGGER.error(
							"The database is on an unknown version! ({}), please ensure you're using the latest version of Fabrissentials!",
							version);
					break;
			}

			// Set the database version to the current version on a successful migration.
			statement.execute("PRAGMA schema.user_version = " + DATABASE_VERSION + ";");
			statement.close();
		} catch (SQLException error) {
			LOGGER.error("Failed to migrate the database!", error);
		}
	}

	public static void populateDatabase() {
		LOGGER.info("Creating the database...");

		try {
			assert Fabrissentials.databaseConnection != null;
			Statement statement = Fabrissentials.databaseConnection.createStatement();

			// Insert the current version of the database
			statement.execute("PRAGMA schema.user_version = " + DATABASE_VERSION + ";");

			// Create the homes table for the /home command
			statement.execute(
					"CREATE TABLE IF NOT EXISTS \"Homes\" (\"HomeID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");

			// Ensure that each player doesn't have multiple homes with the same name,
			// though there can be multiple homes with the same name, unique to each player
			statement.execute(
					"ALTER TABLE \"Homes\" ADD CONSTRAINT \"UniqueHomeIdPerPlayer\" UNIQUE (\"HomeID\", \"PlayerID\");");

			// Create the warps table for the /warp command
			statement.execute(
					"CREATE TABLE IF NOT EXISTS \"Warps\" (\"WarpID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");

			// Ensure that each warp doesn't have the same id.
			statement.execute("ALTER TABLE \"Warps\" ADD CONSTRAINT \"UniqueWarpId\" UNIQUE (\"WarpID\");");

			// Close the statement
			statement.close();
		} catch (SQLException error) {
			LOGGER.error("Failed to create the database!", error);
		}
	}

	public static void closeDatabase() {
		if (Fabrissentials.databaseConnection != null) {
			try {
				Statement statement = Fabrissentials.databaseConnection.createStatement();

				// The default value is 400, but we're going to set it to 1000 to allow for
				// longer and more efficient analysis.
				statement.execute("PRAGMA analysis_limit=1000;");

				// "Housekeeping" - Aaron
				statement.execute("PRAGMA optimize;");
				statement.close();
			} catch (SQLException exception) {
				LOGGER.warn(
						"An unexpected error had occured, please report this to Fabrissentials' GitHub page!",
						exception);
			}
		}

		if (Fabrissentials.databaseConnection != null) {
			try {
				Fabrissentials.databaseConnection.close();
			} catch (SQLException exception) {
				LOGGER.error(
						"An unexpected error occurred trying to close the database. It's possible some data loss occured, please create a backup of the database before attempting to run Fabrissentials again.",
						exception);
			}
		}
	}
}
