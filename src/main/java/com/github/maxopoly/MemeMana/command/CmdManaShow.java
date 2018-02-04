package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaShow extends PlayerCommand {

	public CmdManaShow(String name) {
		super(name);
		setIdentifier("manashow");
		setDescription("Show your own mana");
		setUsage("/manashow");
		setArguments(0,0);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Heck off console man");
			return true;
		}
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayer((Player)sender);
		sender.sendMessage(ChatColor.GOLD + "You have " + owner.getPouch().getFormattedContent() + " mana");
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			sender.sendMessage(ChatColor.GOLD + String.format("Your login streak is %d", stat.getStreak()));
		}
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
