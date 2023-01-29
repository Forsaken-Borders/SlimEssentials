package net.forsaken_borders.commands.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.forsaken_borders.DatabaseHandler;
import net.forsaken_borders.models.Point;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class HomeDeleteCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
		String homeName = context.getArgument("name", String.class);

		Optional<Point> homePoint = homes.stream().filter(home -> home.id().equals(homeName)).findFirst();

		if (homePoint.isEmpty()) {
			player.sendMessage(Text.translatable("command.home.delete.not_found", homeName));
			return 0;
		}

		//TODO: Add a database handler call to delete the home point

		player.sendMessage(Text.translatable("command.home.delete.success", homeName));
		return 1;
	}
}
