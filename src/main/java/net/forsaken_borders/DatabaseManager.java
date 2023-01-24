package net.forsaken_borders;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseManager {
	/***
	 * The current version of the database. This is used for database migrations and
	 * future proofing. Modify this when tables have changed in any form.
	 */
	private static final int DATABASE_VERSION = 1;

	/***
	 * The properties that are used when creating the database connection. This is
	 * used to set the database properties, like the journal mode, foreign keys and
	 * more.
	 */
	private static final Properties DATABASE_PROPERTIES = new Properties();

	/***
	 * The logger that is used to log errors and other messages.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger("fabrissentials.database");

	/***
	 * Creates the database connection and creates the tables if the database is
	 * new.
	 * If the database is not new, it will migrate the data from the old database
	 * format into the new one.
	 */
	public static void openDatabase() {
		// TODO: Make the Database File Path changeable via a Config
		boolean newDatabase = !new File("database.db").exists();

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
			Fabrissentials.databaseConnection = DriverManager.getConnection("jdbc:sqlite:database.db", DATABASE_PROPERTIES);
		} catch (Exception error) {
			LOGGER.error("Failed to create the database connection!", error);
			return;
		}

		if (newDatabase) {
			// If the database is new, we need to create the tables
			setupDatabase();
		} else {
			// If the database is not new, we need to transfer the data from the old
			// database into the new database format.
			migrateDatabase();
		}
	}

	/***
	 * Performs a database migration. This will migrate the data from the old
	 * database format into the new one. This will also set the database version to
	 * the current version. This method will not do anything if the database is on
	 * the current version. Under no circumstances should data loss occur.
	 */
	public static void migrateDatabase() {
		assert Fabrissentials.databaseConnection != null;

		try {
			Statement statement = Fabrissentials.databaseConnection.createStatement();
			ResultSet versionResult = statement.executeQuery("PRAGMA user_version;");
			int version = versionResult.getInt("user_version");

			switch (version) {
				case DATABASE_VERSION:
					// Currently we're on this version and no work needs to be done.
					break;
				default:
					// We're on an unknown version, likely a newer version than the current one.
					// Do NOT attempt to touch the database as it may cause data loss.
					LOGGER.error("The database is on an unknown version! ({}), please ensure you're using the latest version of Fabrissentials!", version);
					return;
			}

			// Set the database version to the current version on a successful migration.
			statement.execute("PRAGMA schema.user_version = " + DATABASE_VERSION + ";");
			statement.close();
		} catch (SQLException error) {
			LOGGER.error("Failed to migrate the database!", error);
		}
	}

	/***
	 * Creates the tables for the database. This will also set the database version
	 * to the current version.
	 */
	public static void setupDatabase() {
		assert Fabrissentials.databaseConnection != null;
		LOGGER.info("Creating the database...");

		try {
			Statement statement = Fabrissentials.databaseConnection.createStatement();

			// Insert the current version of the database
			statement.execute("PRAGMA user_version = " + DATABASE_VERSION + ";");

			// Create the homes table for the /home command, and use a unique key to make sure a player cannot create two homes with the same name
			statement.execute("CREATE TABLE IF NOT EXISTS \"Homes\" (\"HomeID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");
			statement.execute("ALTER TABLE \"Homes\" ADD CONSTRAINT \"UniqueHomeIdPerPlayer\" UNIQUE (\"HomeID\", \"PlayerID\");");

			// Create the warps table for the /warp command, and use a unique key to make sure each warp has a unique id
			statement.execute("CREATE TABLE IF NOT EXISTS \"Warps\" (\"WarpID\" TEXT NOT NULL, \"PlayerID\" BLOB NOT NULL, \"WorldID\" BLOB NOT NULL, \"X\" REAL NOT NULL, \"Y\" REAL NOT NULL, \"Z\" REAL NOT NULL, \"Pitch\" REAL NOT NULL, \"Yaw\" REAL NOT NULL);");
			statement.execute("ALTER TABLE \"Warps\" ADD CONSTRAINT \"UniqueWarpId\" UNIQUE (\"WarpID\");");

			// Close the statement
			statement.close();
		} catch (SQLException error) {
			LOGGER.error("Failed to create the database!", error);
		}
	}

	/***
	 * Closes the database connection and performs some "housekeeping" on the
	 * database.
	 */
	public static void closeDatabase() {
		if (Fabrissentials.databaseConnection != null) {
			try {
				Statement statement = Fabrissentials.databaseConnection.createStatement();

				// The default value is 400, but we're going to set it to 1000 to allow for
				// longer and more efficient analysis.
				statement.execute("PRAGMA analysis_limit=1000;");

				// "Housekeeping" - Aaron
				// "Housekeeping" - Me
				statement.execute("PRAGMA optimize;");
				statement.close();
			} catch (SQLException exception) {
				LOGGER.warn("An unexpected error has occurred, please report this to Fabrissentials' GitHub page!", exception);
			}
		}

		if (Fabrissentials.databaseConnection != null) {
			try {
				Fabrissentials.databaseConnection.close();
			} catch (SQLException exception) {
				LOGGER.error("An unexpected error occurred trying to close the database. It's possible some data was lost. Please create a backup of the database before attempting to run Fabrissentials again.", exception);
			}
		}
	}
}
