package net.forsaken_borders.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.forsaken_borders.DatabaseHandler;
import net.forsaken_borders.DatabaseManager;
import net.forsaken_borders.FabrissentialsConfig;
import net.forsaken_borders.models.Point;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

public class SetHomeCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		// TODO: Translations!

		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
		String homeName = context.getArgument("name", String.class);

		if (homes.size() >= FabrissentialsConfig.maxAmountOfHomes) {
			player.sendMessage(Text.literal("You already have the maximum amount of homes."));
			return 0;
		} else if (homes.stream().anyMatch(home -> home.id().equals(homeName))) {
			player.sendMessage(Text.literal("You already have a Home called '" + homeName + "'."));
			return 0;
		}

		try {
			DatabaseHandler.createHomePointForPlayer(player, homeName);
			player.sendMessage(Text.literal("Your new Home was created successfully."));
			return 1;
		} catch (SQLException exception) {
			DatabaseManager.LOGGER.error("Creating a new Home failed.", exception);
			player.sendMessage(Text.literal("There was an internal Error."));
			return -1;
		}
	}
}
