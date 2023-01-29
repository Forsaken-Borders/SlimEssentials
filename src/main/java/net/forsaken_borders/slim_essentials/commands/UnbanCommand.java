package net.forsaken_borders.slim_essentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public class UnbanCommand implements Command<ServerCommandSource>{
	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		return context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "pardon " + context.getArgument("player", String.class));
	}
}
