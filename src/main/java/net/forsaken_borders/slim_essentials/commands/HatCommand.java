package net.forsaken_borders.slim_essentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class HatCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		PlayerInventory inv = player.getInventory();

		//Get currently held item and headgear item
		ItemStack handItem = player.getEquippedStack(EquipmentSlot.MAINHAND);
		ItemStack headItem = player.getEquippedStack(EquipmentSlot.HEAD);

		//Swap slots
		inv.armor.set(3, handItem);
		inv.main.set(inv.selectedSlot, headItem);

		return 1;
	}
}
