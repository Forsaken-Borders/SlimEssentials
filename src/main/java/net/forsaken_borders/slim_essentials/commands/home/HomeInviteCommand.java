package net.forsaken_borders.slim_essentials.commands.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.forsaken_borders.slim_essentials.DatabaseHandler;
import net.forsaken_borders.slim_essentials.models.Point;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class HomeInviteCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		// The player we're trying to invite
		ServerPlayerEntity otherPlayer = EntityArgumentType.getPlayer(context, "player");
		if(otherPlayer == player)
		{
			player.sendMessage(Text.translatable("command.home.invite.self_invite"));
			return 0;
		}

		// The point we're trying to invite to
		String homeName = context.getArgument("home name", String.class);

		ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
		Optional<Point> homePoint = homes.stream().filter(home -> home.id().equals(homeName)).findFirst();
		if (homePoint.isEmpty()) {
			player.sendMessage(Text.translatable("command.home.invite.not_found", player.getDisplayName(), homeName));
			return 0;
		}


		//TODO: Add database call for inviting another player to a home point

		player.sendMessage(Text.translatable("command.home.invite.success", player.getDisplayName(), homeName));


		return 0;
	}
}
