package net.forsaken_borders.slim_essentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class RepairCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		//In case someone calls this method on accident
		return repairMain(context);
	}


	public int repairMain(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ItemStack item = player.getMainHandStack();
		if(item.isEmpty()) {
			player.sendMessage(Text.translatable("command.repair.main_empty"));
			return 0;
		}

		if(!repairItem(item)) {
			player.sendMessage(Text.translatable("command.repair.norepair"));
			return 0;
		}
		else {
			player.sendMessage(Text.translatable("command.repair.success"));
			return 1;
		}
	}

	public int repairOffhand(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		ItemStack item = player.getOffHandStack();
		if(item.isEmpty()) {
			player.sendMessage(Text.translatable("command.repair.off_empty"));
			return 0;
		}

		if(!repairItem(item)) {
			player.sendMessage(Text.translatable("command.repair.norepair"));
			return 0;
		}
		else {
			player.sendMessage(Text.translatable("command.repair.success"));
			return 1;
		}
	}

	public int repairAll(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null) {
			return -1;
		}

		DefaultedList<ItemStack> stacks = player.getInventory().main;
		DefaultedList<ItemStack> stacksArmor = player.getInventory().armor;
		ItemStack offhandItem = player.getEquippedStack(EquipmentSlot.OFFHAND);

		int repaired = 0;
		for (ItemStack stack : stacks) {
			if(repairItem(stack)) repaired++;
		}
		for (ItemStack stack : stacksArmor) {
			if(repairItem(stack)) repaired++;
		}
		if(repairItem(offhandItem)) repaired++;

		if(repaired == 0) {
			player.sendMessage(Text.translatable("command.repair.bulk.none_repaired"));
		}
		else {
			player.sendMessage(Text.translatable("command.repair.bulk.success", repaired));
		}

		return 1;
	}

	public boolean repairItem(ItemStack item) {
		if(!item.isDamageable()) return false;
		item.setDamage(0);
		return true;
	}
}
