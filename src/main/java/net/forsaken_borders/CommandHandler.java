package net.forsaken_borders;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.forsaken_borders.commands.UnbanCommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {
	public static void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("unban")
			    .then(RequiredArgumentBuilder.argument("player", EntityArgumentType.player()))
				.executes(new UnbanCommand()).build();

		commandDispatcher.getRoot().addChild(unbanCommandNode);
	}


}
