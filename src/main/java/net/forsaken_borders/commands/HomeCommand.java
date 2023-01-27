package net.forsaken_borders.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.forsaken_borders.DatabaseHandler;
import net.forsaken_borders.models.Point;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class HomeCommand implements Command<ServerCommandSource> {

	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		// TODO: Translations (yes, here too)!
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ArrayList<Point> homes = DatabaseHandler.getHomePointsForPlayer(player);
		String homeName = context.getArgument("name", String.class);
		if (homes.stream().noneMatch(home -> home.id().equals(homeName))) {
			player.sendMessage(Text.translatable("command.home.nohome", homeName));
			return 0;
		}

		// TODO: Have a cooldown check here. id do with with a static final dictionary
		// and Bukkit.getScheduler in spigot, no idea how here

		// TODO: Have a check here about combat TP? Probably needs a mixin, unless
		// fabric has something like
		// https://jd.papermc.io/paper/1.19/org/bukkit/entity/Entity.html#getLastDamageCause()
		// Make it toggleable via the config ofc

		// .orElseThrow() will never throw here. We check if a home with the given name exists in the list
		// and return if it does not.
		Point selectedHome = homes.stream().filter(home -> home.id().equals(homeName)).findFirst().orElseThrow();

		// FIXME: I have no idea how to get the world back from our namespace:key format
		// Maybe this:
		// player.getWorld().getRegistryKey().getRegistry().withPath(string path)
		// but what to we pass in as the path?
		// This would be ideal:
		// player.teleport(world, selectedHome.x(), selectedHome.y(), selectedHome.z(), selectedHome.pitch(), selectedHome.yaw());
		// this currently only teleports in the same world
		player.teleport(selectedHome.x(), selectedHome.y(), selectedHome.z());

		return 1;
	}

}
