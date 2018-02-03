package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.NameAPI;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.PearlType;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.MemeManaPlayerOwner;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;
import java.util.function.IntFunction;
import java.text.DecimalFormat;

public class CmdManaShow extends PlayerCommand {
	private static final DecimalFormat manaFormat = new DecimalFormat("####.###");
	public CmdManaShow(String name) {
		super(name);
		setIdentifier("manashow");
		setDescription("Show your own mana");
		setUsage("/manashow");
		setArguments(0,0);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't show your own mana from console.");
			return true;
		}
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayer((Player)sender);
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(owner);
		double manaAvailable = pouch.getContent();
		msg("<i>You have<g> %s<i> mana",manaFormat.format(manaAvailable));
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		if(stat.getStreak() != 0) {
			msg("<g>You are on a <i>%d<g> day login streak",stat.getStreak());
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
