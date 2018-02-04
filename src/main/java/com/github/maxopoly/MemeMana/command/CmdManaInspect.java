package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.List;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaInspect extends PlayerCommand {

	public CmdManaInspect(String name) {
		super(name);
		setIdentifier("manainspect");
		setDescription("Inspect a player's mana and streak");
		setUsage("/manainspect <player>");
		setArguments(1,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		MemeManaPlayerOwner owner = CmdManaAdmin.playerCheck(sender, args [1]);
		if (owner == null) {
			return true;
		}
		CmdManaAdmin.inspectManaAmount(sender, owner);
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
