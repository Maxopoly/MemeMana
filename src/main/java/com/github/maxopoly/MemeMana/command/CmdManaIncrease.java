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

public class CmdManaIncrease extends PlayerCommand {
	public CmdManaIncrease(String name) {
		super(name);
		setIdentifier("manaincrease");
		setDescription("Increase a player's mana");
		setUsage("/manaincrease Player Amount");
		setArguments(2,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		MemeManaOwner owner = MemeManaPlayerOwner.fromPlayerName(args[0]);
		if(owner == null) {
			msg("<c>%s <b>is not a valid mana owner",args[0]);
			return false;
		}
		Integer giveAmount = null;
		try {
			giveAmount = Integer.parseInt(args[1]);
		} catch (Exception e) {
			msg("<i>%s <b>is not a valid amount of mana",args[1]);
			return false;
		}
		MemeManaPlugin.getInstance().getManaManager().addMana(owner,giveAmount);
		msg("<g>Increased the mana of <c>%s <g>by<i> %d",args[0],giveAmount);
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
