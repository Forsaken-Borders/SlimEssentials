package net.forsaken_borders.slim_essentials;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import net.forsaken_borders.slim_essentials.models.Point;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class DatabaseHandler {
	public static ArrayList<Point> getHomePointsForPlayer(ServerPlayerEntity player) {
		ArrayList<Point> homes = new ArrayList<>();

		try {
			assert SlimEssentials.databaseConnection != null;

			PreparedStatement statement = SlimEssentials.databaseConnection.prepareStatement("SELECT * FROM \"Homes\" WHERE \"PlayerID\" = ?;");
			statement.setBytes(1, bytesFromUuid(player.getUuid()));
			ResultSet result = statement.executeQuery();

			// Iterate over all results storing them in the homes ArrayList
			while (result.next()) {
				homes.add(new Point(
					result.getString("HomeID"),
					uuidFromBytes(result.getBytes("PlayerID")),
					result.getString("WorldID"),
					result.getDouble("X"),
					result.getDouble("Y"),
					result.getDouble("Z"),
					result.getDouble("Pitch"),
					result.getDouble("Yaw")
				));
			}

			return homes;
		} catch (SQLException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void createHomePointForPlayer(ServerPlayerEntity player, String homeName) throws SQLException {
		assert SlimEssentials.databaseConnection != null;

		Identifier worldIdentifier = player.getWorld().getRegistryKey().getValue();
		Vec3d position = player.getPos();

		PreparedStatement statement = SlimEssentials.databaseConnection.prepareStatement("INSERT INTO \"Homes\" VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		statement.setString(1, homeName);
		statement.setBytes(2, bytesFromUuid(player.getUuid()));
		statement.setString(3, worldIdentifier.getNamespace() + ":" + worldIdentifier.getPath());
		statement.setDouble(4, position.x);
		statement.setDouble(5, position.y);
		statement.setDouble(6, position.z);
		statement.setDouble(7, roundToNearestStep(player.getPitch(), 45));
		statement.setDouble(8, roundToNearestStep(player.getYaw(), 10));
		statement.execute();
	}

	private static UUID uuidFromBytes(byte[] inputBytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(inputBytes);
		long mostSigBits = byteBuffer.getLong();
		long leastSigBits = byteBuffer.getLong();

		return new UUID(mostSigBits, leastSigBits);
	}

	private static byte[] bytesFromUuid(UUID uuid) {
		byte[] byteArray = new byte[16];

		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());

		return byteArray;
	}

	private static int roundToNearestStep(double value, double step) {
		double remainder = value % step;
		double roundedNumber = value - remainder;

		if (remainder > step / 2d) {
			roundedNumber += step;
		}

		return (int) roundedNumber;
	}
}
