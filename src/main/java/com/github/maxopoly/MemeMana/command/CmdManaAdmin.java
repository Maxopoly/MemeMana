package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaAdmin extends PlayerCommand {
	public CmdManaAdmin(String name) {
		super(name);
		setIdentifier("manaadmin");
		setDescription("The admin command for MemeMana. Inspects shows a players streak/mana amount, give gives a certain amount and reset resets a daily streak");
		setUsage("\n/manaadmin <inspect|give|reset> <player> [amount]");
		setArguments(1,3);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify a player");
			return false;
		}
		MemeManaPlayerOwner owner = playerCheck(sender, args [1]);
		if (owner == null) {
			return true;
		}
		switch (args [0]) {
			case "inspect":
				inspectManaAmount(sender,owner);
				break;
			case "give":
				doGive(sender, owner, args);
				break;
			case "reset":
				doReset(sender, owner);
				break;
			default:
				sender.sendMessage(ChatColor.RED + "Action " + args [0]  + " is not known");
				return false;
		}
		return true;
	}

	public static void inspectManaAmount(CommandSender sender, MemeManaPlayerOwner owner) {
		MemeManaPouch pouch = owner.getPouch();
		sender.sendMessage(ChatColor.GOLD + String.format("%s has %s mana", owner.getNiceName(), pouch.getFormattedContent()));
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		sender.sendMessage(ChatColor.GOLD + String.format("%s is on a %d day login streak", owner.getNiceName(), stat.getStreak()));
	}

	public static void doGive(CommandSender sender, MemeManaPlayerOwner owner, String[] args) {
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "You must specify the amount of mana to give");
			return;
		}
		int giveAmount;
		try {
			giveAmount = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + args [2] + " is not a valid integer");
			return;
		}
		MemeManaPlugin.getInstance().getTransactionManager().giveMana(owner, giveAmount);
		sender.sendMessage(ChatColor.GREEN + String.format("Gave %d mana to %s", giveAmount, owner.getNiceName()));
	}

	public static void doReset(CommandSender sender, MemeManaPlayerOwner owner) {
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		stat.reset();
		MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
		sender.sendMessage(ChatColor.GREEN + String.format("Reset streak for %s",  owner.getNiceName()));
	}

	public static MemeManaPlayerOwner playerCheck(CommandSender sender, String name) {
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayerName(name);
		if(owner == null) {
			sender.sendMessage(ChatColor.RED + name + "is not a valid player");
		}
		return owner;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		switch(args.length) {
			case 0:
				return new ArrayList<String>(Arrays.asList("reset", "give", "inspect"));
			case 1:
				if (args [1].length() == 0) {
					return new ArrayList<String>(Arrays.asList("reset", "give", "inspect"));
				}
				switch (args [1].charAt(0)) {
					case 'r':
						return new ArrayList<String>(Arrays.asList("reset"));
					case 'i':
						return new ArrayList<String>(Arrays.asList("inspect"));
					case 'g':
						return new ArrayList<String>(Arrays.asList("give"));
				}
			case 2:
				return null;
			default:
				return new LinkedList<String>();
		}
	}
}
