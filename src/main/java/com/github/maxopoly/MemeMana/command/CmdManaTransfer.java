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

public class CmdManaTransfer extends PlayerCommand {
	public CmdManaTransfer(String name) {
		super(name);
		setIdentifier("manatransfer");
		setDescription("Transfer some of your mana to someone else");
		setUsage("/manatransfer Player Amount");
		setArguments(2,2);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't transfer mana from console");
			return true;
		}
		Player player = (Player) sender;
		MemeManaOwner transferTo = MemeManaPlayerOwner.fromPlayerName(args[0]);
		if (transferTo == null) {
			msg("<c>%s <b>is not a valid player",args[0]);
			return false;
		}
		
		int transferAmount = (int) MemeManaPlugin.getInstance().getManaManager().getPouch(MemeManaPlayerOwner.fromPlayer(player)).getContent();
		if (args.length == 3) {
			try {
				transferAmount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				msg("<i>%s <b>is not a valid amount of mana",args[1]);
				return false;
			}
		}
		//if (MemeManaPlugin.getInstance().getManaManager().transferMana(MemeManaPlayerOwner.fromPlayer(player),transferTo,transferAmount)) {}
		msg("Sorry, mana transfer is not implemented yet");
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return null; // Defaults to players
	}
}
