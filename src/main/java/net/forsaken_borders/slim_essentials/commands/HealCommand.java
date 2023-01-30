package net.forsaken_borders.slim_essentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class HealCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		player.heal(player.getMaxHealth());
		player.sendMessage(Text.translatable("command.heal.success"));
		return 1;
	}

	public int runOnOther(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ServerPlayerEntity otherPlayer = EntityArgumentType.getPlayer(context, "player");
		otherPlayer.heal(otherPlayer.getMaxHealth());
		player.sendMessage(Text.translatable("command.heal.other_success", otherPlayer.getDisplayName()));
		return 1;
	}
}
