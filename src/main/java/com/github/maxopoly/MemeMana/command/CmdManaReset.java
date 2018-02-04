package com.github.maxopoly.MemeMana.command;

import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.List;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaReset extends PlayerCommand {
	public CmdManaReset(String name) {
		super(name);
		setIdentifier("manareset");
		setDescription("Reset a player's mana streak");
		setUsage("/manareset Player");
		setArguments(1,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		MemeManaPlayerOwner owner = CmdManaAdmin.playerCheck(sender, args [1]);
		if (owner == null) {
			return true;
		}
		CmdManaAdmin.doReset(sender, owner);
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
