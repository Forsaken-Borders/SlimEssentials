package net.forsaken_borders.models;

import java.util.Objects;
import java.util.UUID;

// This is called a Point instead of a home, because this is the exact same format for Warps. We can just reuse this record

public record Point(String id, UUID playerId, String worldId, double x, double y, double z, double pitch, double yaw) {

	public Point {
		Objects.requireNonNull(id);
		Objects.requireNonNull(playerId);
		Objects.requireNonNull(worldId);
	}
}
