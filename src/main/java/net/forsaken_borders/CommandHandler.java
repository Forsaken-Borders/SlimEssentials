package net.forsaken_borders;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.forsaken_borders.models.Point;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;

public class CommandHandler {
	public static void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher) {

		RootCommandNode<ServerCommandSource> rootNode = commandDispatcher.getRoot();

		// Unban Command - Alies for /pardon
		// TODO: Add Suggestions for banned players
		{
			LiteralCommandNode<ServerCommandSource> commandNode = CommandManager.literal("unban")
				.then(RequiredArgumentBuilder.argument("player", StringArgumentType.greedyString()))
				.executes((context) -> context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "pardon " + context.getArgument("player", String.class))).build();

			rootNode.addChild(commandNode);
		}

		// Unban IP Command - Alias for /pardon-ip
		// TODO: Add Suggestions for banned IPs
		{
			LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("unban-ip")
				.then(RequiredArgumentBuilder.argument("ip", StringArgumentType.greedyString()))
				.executes((context) -> context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "pardon-ip " + context.getArgument("player", String.class))).build();

			rootNode.addChild(unbanCommandNode);
		}

		// Sethome Command - Set a Home Point
		{
			LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("sethome")
				.then(RequiredArgumentBuilder.argument("name", StringArgumentType.greedyString()))
				.executes((context) -> {
					// TODO: Translations!

					ServerPlayerEntity player = context.getSource().getPlayer();
					if (player == null) {
						return 0;
					}

					ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
					String homeName = context.getArgument("name", String.class);

					if (homes.size() >= FabrissentialsConfig.maxAmountOfHomes) {
						player.sendMessage(Text.literal("You already have the maximum amount of homes."));
						return 0;
					}

					if (homes.stream().anyMatch(home -> home.id().equals(homeName))) {
						player.sendMessage(Text.literal("You already have a Home called '" + homeName + "'."));
						return 0;
					}

					try {
						DatabaseHandler.createHomePointForPlayer(player, homeName);
						player.sendMessage(Text.literal("Your new Home was created successfully."));
						return 1;

					} catch (SQLException exception) {
						player.sendMessage(Text.literal("There was an internal Error."));
						return 0;
					}
				}).build();

			rootNode.addChild(unbanCommandNode);
		}

		// Home Command - Teleport to a Home Point
		{
			LiteralCommandNode<ServerCommandSource> unbanCommandNode = CommandManager.literal("home")
				.then(RequiredArgumentBuilder.argument("name", StringArgumentType.greedyString()))
				.executes((context) -> {
					// TODO: Translations (yes, here too)!

					ServerPlayerEntity player = context.getSource().getPlayer();
					if (player == null) {
						return 0;
					}

					ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
					String homeName = context.getArgument("name", String.class);

					if (homes.stream().noneMatch(home -> home.id().equals(homeName))) {
						player.sendMessage(Text.literal("You do not have a Home called '" + homeName + "'."));
						return 0;
					}

					// TODO: Have a cooldown check here. id do with with a static final dictionary and Bukkit.getScheduler in spigot, no idea how here

					// TODO: Have a check here about combat TP? Probably needs a mixin, unless fabric has something like https://jd.papermc.io/paper/1.19/org/bukkit/entity/Entity.html#getLastDamageCause()
					// Make it toggleable via the config ofc

					Point selectedHome = homes.stream().filter(home -> home.id().equals(homeName)).findFirst().get();
					// FIXME: I have no idea how to get the world back from our namespace:key format.
					// Maybe this: player.getWorld().getRegistryKey().getRegistry().withPath(string path) but what to we pass in as the path?
					// Comment the comments below out to use teleport(ServerWorld, double, double, double, float, float), this currently only teleports in the same world
					player.teleport(/*world here, */ selectedHome.x(), selectedHome.y(), selectedHome.z()/*, selectedHome.pitch(), selectedHome.yaw()*/);

					return 1;
				}).build();

			rootNode.addChild(unbanCommandNode);
		}
	}
}
