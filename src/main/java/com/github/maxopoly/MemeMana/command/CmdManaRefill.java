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

public class CmdManaRefill extends PlayerCommand {
	public CmdManaRefill(String name) {
		super(name);
		setIdentifier("manarefill");
		setDescription("Refill a pearl using mana");
		setUsage("/manarefill [Amount]");
		setArguments(0,1);
	}

	public boolean execute(CommandSender sender, String [] args) {
		if (!(sender instanceof Player)) {
			msg("Can't refill from console");
			return true;
		}
		Player player = (Player) sender;
		ExilePearl pearl = ExilePearlPlugin.getApi().getPearlFromItemStack(player.getInventory().getItemInMainHand());
		if (pearl == null) {
			msg("<b>You must be holding a pearl to refill it");
			return true;
		}
		// Ignore pearls that are at full health
		int maxHealth = ExilePearlPlugin.getApi().getPearlConfig().getPearlHealthMaxValue();
		if (pearl.getHealth() == maxHealth) {
			msg("<g>That pearl is already at max health!");
			return true;
		}
		int repairPerUnitMana = MemeManaPlugin.getInstance().getManaConfig().getPearlRefillAmount(pearl.getPearlType());
		MemeManaPouch pouch = MemeManaPlugin.getInstance().getManaManager().getPouch(MemeManaPlayerOwner.fromPlayer(player));
		double manaAvailable = pouch.getContent();
		int healthBefore = pearl.getHealth();
		int manaToUse = Math.min((int)Math.ceil((maxHealth - healthBefore) / (double)repairPerUnitMana), (int)manaAvailable);
		if(manaToUse <= 0) {
			msg("<b>You don't have enough mana to refill this pearl at all");
			return true;
		}
		if(pouch.deposit(manaToUse)) {
			pearl.setHealth(Math.min(healthBefore + repairPerUnitMana * manaToUse,maxHealth));
			IntFunction<Integer> toPercent = h -> Math.min(100, Math.max(0, (int)Math.round(((double)h / maxHealth) * 100)));
			msg("<g>The pearl was repaired from <i>%d%%<g> health to <i>%d%%<g> health, consuming <i>%d<g> mana!", toPercent.apply(healthBefore),toPercent.apply(pearl.getHealth()),manaToUse);
		}
		return true;
	}

	public List <String> tabComplete(CommandSender sender, String [] args) {
		return new LinkedList <String> (); //empty list
	}
}
