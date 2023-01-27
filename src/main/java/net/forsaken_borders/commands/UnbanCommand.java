package net.forsaken_borders.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

public class UnbanCommand implements Command<ServerCommandSource>{
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		return context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "pardon " + context.getArgument("player", String.class));
	}
}
