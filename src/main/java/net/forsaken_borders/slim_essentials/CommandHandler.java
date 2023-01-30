package net.forsaken_borders.slim_essentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.forsaken_borders.slim_essentials.commands.*;
import net.forsaken_borders.slim_essentials.commands.home.HomeCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeDeleteCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeInviteCommand;
import net.forsaken_borders.slim_essentials.commands.home.HomeSetCommand;
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
			LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("unban")
				.then(
					argument("player", StringArgumentType.greedyString()).executes(new UnbanCommand())
				).build();

			rootNode.addChild(unbanCommandNode);
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

		//Repair command - repair item in the main/offhand or repair all items in the inventory
		{
			LiteralCommandNode<ServerCommandSource> repairItemCommandNode = CommandManager.literal("repair")
				.executes(new RepairCommand()::repairMain)
				.build();

			LiteralCommandNode<ServerCommandSource> repairMainHandItemCommandNode = CommandManager.literal("main")
				.executes(new RepairCommand()::repairMain)
				.build();

			LiteralCommandNode<ServerCommandSource> repairOffHandItemCommandNode = CommandManager.literal("offhand")
				.executes(new RepairCommand()::repairOffhand)
				.build();

			LiteralCommandNode<ServerCommandSource> repairAllItemsCommandNode = CommandManager.literal("all")
				.executes(new RepairCommand()::repairAll)
				.build();


			//Register all child command nodes
			repairItemCommandNode.addChild(repairMainHandItemCommandNode);
			repairItemCommandNode.addChild(repairOffHandItemCommandNode);
			repairItemCommandNode.addChild(repairAllItemsCommandNode);

			//Register command node
			rootNode.addChild(repairItemCommandNode);
		}

		// Extinguish command - extinguish self/another player
		{
			LiteralCommandNode<ServerCommandSource> extinguishCommandNode = CommandManager.literal("extinguish")
				.then(argument("player", EntityArgumentType.player()).executes(new ExtinguishCommand()::runOnOther))
				.executes(new ExtinguishCommand())
				.build();

			// For some reason when redirecting to the node above, causes the command to fail because no player was specified. :shrug:
			// We'll just copy it and forget about it
			LiteralCommandNode<ServerCommandSource> extinguishCommandAliasNode = CommandManager.literal("ext")
				.then(argument("player", EntityArgumentType.player()).executes(new ExtinguishCommand()::runOnOther))
				.executes(new ExtinguishCommand())
				.build();

			rootNode.addChild(extinguishCommandNode);
			rootNode.addChild(extinguishCommandAliasNode);
		}

		// Heal command - restore health of self/another player
		{
			LiteralCommandNode<ServerCommandSource> healCommandNode = CommandManager.literal("heal")
				.then(argument("player", EntityArgumentType.player()).executes(new HealCommand()::runOnOther))
				.executes(new HealCommand())
				.build();

			rootNode.addChild(healCommandNode);
		}

		// Feed command - restore food of self/another player
		{
			LiteralCommandNode<ServerCommandSource> feedCommandNode = CommandManager.literal("feed")
				.then(argument("player", EntityArgumentType.player()).executes(new FeedCommand()::runOnOther))
				.executes(new FeedCommand())
				.build();

			rootNode.addChild(feedCommandNode);
		}

		// Broadcast - broadcast a message to all players
		{
			LiteralCommandNode<ServerCommandSource> broadcastCommandNode = CommandManager.literal("broadcast")
				.then(argument("message", StringArgumentType.greedyString()).executes(new BroadcastCommand()))
				.build();

			rootNode.addChild(broadcastCommandNode);
		}

		// Hat - put an item you're holding on your head
		{
			LiteralCommandNode<ServerCommandSource> hatCommandNode = CommandManager.literal("hat")
				.executes(new HatCommand())
				.build();

			rootNode.addChild(hatCommandNode);
		}

		// Home commands
		{
			// Home command - Visit home location
			LiteralCommandNode<ServerCommandSource> homeNode = CommandManager.literal("home")
				.then(argument("name", StringArgumentType.word()).executes(new HomeCommand()))
				.build();

			// Home command alias - Visit
			// Since we can't just redirect to home command (will cause recursion, not going to break but looks wonky), we're just going to copy over the whole command
			LiteralCommandNode<ServerCommandSource> homeVisitNode = CommandManager.literal("visit")
				.then(argument("name", StringArgumentType.word()).executes(new HomeCommand()))
				.build();

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

			// Register all child command nodes
			homeNode.addChild(homeVisitNode);
			homeNode.addChild(homeSetNode);
			homeNode.addChild(homeDeleteNode);
			homeNode.addChild(homeDeleteAlias_Remove);
			homeNode.addChild(homeInviteNode);

			//Register command node
			rootNode.addChild(homeNode);
		}
	}
}
