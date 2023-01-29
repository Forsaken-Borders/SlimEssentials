package net.forsaken_borders.fabrissentials_slim.commands.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.forsaken_borders.fabrissentials_slim.DatabaseHandler;
import net.forsaken_borders.fabrissentials_slim.DatabaseManager;
import net.forsaken_borders.fabrissentials_slim.FabrissentialsConfig;
import net.forsaken_borders.fabrissentials_slim.models.Point;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

public class HomeSetCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
		String homeName = context.getArgument("name", String.class);

		if (homes.size() >= FabrissentialsConfig.maxAmountOfHomes) {
			player.sendMessage(Text.translatable("command.home.set.max_homes"));
			return 0;
		} else if (homes.stream().anyMatch(home -> home.id().equals(homeName))) {
			player.sendMessage(Text.translatable("command.home.set.exists", homeName));
			return 0;
		}

		try {
			DatabaseHandler.createHomePointForPlayer(player, homeName);
			player.sendMessage(Text.translatable("command.home.set.success"));
			return 1;
		} catch (SQLException exception) {
			DatabaseManager.LOGGER.error("Creating a new Home failed.", exception);
			player.sendMessage(Text.translatable("command.home.set.error"));
			return -1;
		}
	}
}
