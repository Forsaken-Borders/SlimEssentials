package net.forsaken_borders.slim_essentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class BroadcastCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		//Not checking if the player is valid, so it's possible to use this command from console

		String message = context.getArgument("message", String.class);
		//TODO: There's gotta be a better way to do this. Any ideas?
		List<ServerPlayerEntity> players = context.getSource().getServer().getPlayerManager().getPlayerList();
		players.forEach(x -> x.sendMessage(Text.literal(message)));
		return 1;
	}
}
