package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaTransfer extends PlayerCommand {
	public CmdManaTransfer(String name) {
		super(name);
		setIdentifier("manatransfer");
		setDescription("Transfer some of your mana to someone else");
		setUsage("/manatransfer <player> <amount>");
		setArguments(2,2);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Go away console man, you dont have mana");
			return true;
		}
		MemeManaPlayerOwner owner = CmdManaAdmin.playerCheck(sender, args [0]);
		if (owner == null) {
			return true;
		}
		Player player = (Player) sender;
		int transferAmount = (int) Math.floor(MemeManaPlayerOwner.fromPlayer(player).getPouch().getContent());
		if (args.length == 3) {
			try {
				transferAmount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args [1] + " is not a valid number");
				return false;
			}
		}
		//if (MemeManaPlugin.getInstance().getManaManager().transferMana(MemeManaPlayerOwner.fromPlayer(player),transferTo,transferAmount)) {}
		msg("Sorry, mana transfer is not implemented yet");
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
