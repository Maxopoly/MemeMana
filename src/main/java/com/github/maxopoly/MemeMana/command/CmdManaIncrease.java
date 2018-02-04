package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaIncrease extends PlayerCommand {
	public CmdManaIncrease(String name) {
		super(name);
		setIdentifier("manaincrease");
		setDescription("Admin command to increase a player's mana");
		setUsage("/manaincrease Player Amount");
		setArguments(2,2);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		MemeManaPlayerOwner owner = CmdManaAdmin.playerCheck(sender, args [1]);
		if (owner == null) {
			return true;
		}
		CmdManaAdmin.doGive(sender, owner, args);
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		if (args.length < 2) {
			return null; // Defaults to players
		}
		return new LinkedList<String>();
	}
}
