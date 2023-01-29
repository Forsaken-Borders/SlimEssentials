package net.forsaken_borders.slim_essentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.forsaken_borders.slim_essentials.commands.home.HomeCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeDeleteCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeInviteCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeSetCommand;
import net.forsaken_borders.slim_essentials.commands.UnbanCommand;
import net.forsaken_borders.slim_essentials.commands.UnbanIpCommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;

public class CommandHandler {
	public static void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		RootCommandNode<ServerCommandSource> rootNode = commandDispatcher.getRoot();

		// Unban Command - Alies for /pardon
		{
			// TODO: Add Suggestions for banned players
			LiteralCommandNode<ServerCommandSource> unbancommandNode = CommandManager.literal("unban")
				.then(
					argument("player", StringArgumentType.greedyString()).executes(new UnbanCommand())
				).build();

			rootNode.addChild(unbancommandNode);
		}

		// Unban IP Command - Alias for /pardon-ip
		{
			// TODO: Add Suggestions for banned IPs
			LiteralCommandNode<ServerCommandSource> unbanIpCommandNode = CommandManager.literal("unban-ip")
				.then(
					argument("ip", StringArgumentType.greedyString()).executes(new UnbanIpCommand())
				).build();

			rootNode.addChild(unbanIpCommandNode);
		}


		// Home commands
		{

			// Home command - Visit home location
			LiteralCommandNode<ServerCommandSource> homeNode = CommandManager.literal("home")
				.then(argument("name", StringArgumentType.word()).executes(new HomeCommand()))
				.build();

			//TODO: Add a "visit" alias for /home so that it's possible to make points with names that match the subcommand names (i.e. set, delete, invite, etc.)


			// Set command - Set home location at player's position
			LiteralCommandNode<ServerCommandSource> homeSetNode = CommandManager.literal("set")
				.then(argument("name", StringArgumentType.word()).executes(new HomeSetCommand()))
				.build();

			// Delete command - Remove home point of specified name
			LiteralCommandNode<ServerCommandSource> homeDeleteNode = CommandManager.literal("delete")
				.then(argument("name", StringArgumentType.word()).executes(new HomeDeleteCommand()))
				.build();

			// Delete command alias - Remove
			LiteralCommandNode<ServerCommandSource> homeDeleteAlias_Remove = CommandManager.literal("remove")
				.redirect(homeDeleteNode)
				.build();


			// Invite command - Invite another player to your home point
			LiteralCommandNode<ServerCommandSource> homeInviteNode = CommandManager.literal("invite")
				.then(
					argument("player", EntityArgumentType.player())
						.then(argument("home name", StringArgumentType.word())
							.executes(new HomeInviteCommand())
						)
				)
				.build();


			// Register all
			homeNode.addChild(homeSetNode);
			homeNode.addChild(homeDeleteNode);
			homeNode.addChild(homeDeleteAlias_Remove);
			homeNode.addChild(homeInviteNode);

			//Register command node
			rootNode.addChild(homeNode);
		}
	}
}
