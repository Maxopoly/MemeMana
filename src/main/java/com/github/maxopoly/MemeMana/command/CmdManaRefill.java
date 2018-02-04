package com.github.maxopoly.MemeMana.command;

import com.devotedmc.ExilePearl.ExilePearl;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.owners.MemeManaPlayerOwner;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class CmdManaRefill extends PlayerCommand {
	public CmdManaRefill(String name) {
		super(name);
		setIdentifier("manarefill");
		setDescription("Refill a pearl using mana");
		setUsage("/manarefill [Amount]");
		setArguments(0,1);
	}

	@Override
	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Heck off console man");
			return true;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			sender.sendMessage(ChatColor.RED + "You must be holding a pearl to refill it");
			return true;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			sender.sendMessage(ChatColor.RED + "That pearl is already at maximum health!");
			return true;
		}
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		MemeManaPouch pouch = MemeManaPlayerOwner.fromPlayer(player).getPouch();
		double manaAvailable = pouch.getContent();
		int healthBefore = pearl.getHealth();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), (int)manaAvailable);
		if(manaToUse <= 0) {
			sender.sendMessage(ChatColor.RED + "You don't have enough mana to refill this pearl at all");
			return true;
		}
		if(pouch.deposit(manaToUse)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			sender.sendMessage(ChatColor.GREEN + String.format("The pearl was repaired from %d health to %d health, consuming %d mana!",
					toPercent.apply(healthBefore),toPercent.apply(pearl.getHealth()),manaToUse));
		}
		return true;
	}

	@Override
	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
