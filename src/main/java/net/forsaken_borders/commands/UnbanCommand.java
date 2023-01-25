package net.forsaken_borders.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.forsaken_borders.annotations.EssentialArgument;
import net.forsaken_borders.annotations.EssentialArgumentType;
import net.forsaken_borders.annotations.EssentialCommand;
import net.minecraft.server.command.ServerCommandSource;

public class UnbanCommand implements Command<ServerCommandSource> {
	@Override
	@EssentialCommand(name = "unban", arguments = {
		@EssentialArgument(name = "player", type = EssentialArgumentType.Player)
	})
	public int run(CommandContext<ServerCommandSource> context) {
		return context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(),
				"pardon " + context.getArgument("player", String.class));
	}
}
