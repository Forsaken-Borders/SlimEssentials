package net.forsaken_borders;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.forsaken_borders.commands.UnbanCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {
	public static void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("unban")
				.executes(new UnbanCommand()::run).build();

		commandDispatcher.getRoot().addChild(unbanCommandNode);
	}
}
