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

public class CmdManaReset extends PlayerCommand {
	public CmdManaReset(String name) {
		super(name);
		setIdentifier("manareset");
		setDescription("Reset a player's mana stats");
		setUsage("/manareset Player");
		setArguments(1,1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		MemeManaPlayerOwner owner = MemeManaPlayerOwner.fromPlayerName(args[0]);
		if(owner == null) {
			msg("<c>%s <b>is not a valid player",args[0]);
			return false;
		}
		ManaGainStat stat = MemeManaPlugin.getInstance().getActivityManager().getForPlayer(owner);
		stat.reset();
		MemeManaPlugin.getInstance().getDAO().updateManaStat(owner,stat);
		msg("<g>Reset mana statistics for <c>%s",args[0]);
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
