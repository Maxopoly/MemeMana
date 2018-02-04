package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import com.github.maxopoly.MemeMana.model.owners.MemeManaOwner;


public class MemeManaTransactionManager {

	private int nextManaId;
	private MemeManaDAO dao;

	public MemeManaTransactionManager(MemeManaDAO dao) {
		this.dao = dao;
		reloadFromDatabase();
	}

	private void reloadFromDatabase() {
		this.nextManaId = dao.getNextManaId();
	}


	/**
	 * Generates fresh mana with the current time as time stamp and gives it out
	 * @param owner Owner to give the mana to
	 * @param amount Amount to give out
	 */
	public void giveMana(MemeManaOwner owner, double amount) {
		MemeManaUnit unit = new MemeManaUnit(nextManaId++,amount);
		owner.getPouch().addNewUnit(unit);
		dao.addManaUnit(unit, owner);
	}


}
