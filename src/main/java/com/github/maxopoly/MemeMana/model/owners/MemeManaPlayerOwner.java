package com.github.maxopoly.MemeMana.model.owners;

import com.civclassic.altmanager.AltManager;
import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.util.UUID;
import org.bukkit.entity.Player;
import vg.civcraft.mc.namelayer.NameAPI;

public class MemeManaPlayerOwner extends  MemeManaOwner {

	public MemeManaPlayerOwner(int id, int altGroupId) {
		super(id, altGroupId);
	}

	public static MemeManaPlayerOwner fromUUID(UUID uuid) {
		return (MemeManaPlayerOwner) MemeManaPlugin.getInstance().getOwnerManager().
				getOwnerByExternal(getManaOwnerType(MemeManaPlayerOwner.class), AltManager.instance().getAssociationGroup(uuid));
	}

	public static MemeManaPlayerOwner fromPlayerName(String playerName) {
		UUID u = NameAPI.getUUID(playerName);
		return u == null ? null : MemeManaPlayerOwner.fromUUID(NameAPI.getUUID(playerName));
	}

	public static MemeManaPlayerOwner fromPlayer(Player player) {
		return MemeManaPlayerOwner.fromUUID(player.getUniqueId());
	}

	@Override
	public String getNiceName() {
		// TODO Auto-generated method stub
		return null;
	}

}
