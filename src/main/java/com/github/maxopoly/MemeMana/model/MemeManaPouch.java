package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class MemeManaPouch {

	// chronologically ordered!
	private List<MemeManaUnit> units;

	public MemeManaPouch() {
		this.units = new ArrayList<MemeManaUnit>();
	}

	/**
	 * Removes mana past the maximum keep time
	 */
	public void cleanupPouch() {
		Iterator<MemeManaUnit> iter = units.iterator();
		long currentTime = System.currentTimeMillis();
		long rotTime = MemeManaPlugin.getInstance().getManaConfig().getManaRotTime();
		while (iter.hasNext()) {
			MemeManaUnit unit = iter.next();
			if (currentTime - unit.getGainTime() > rotTime) {
				iter.remove();
			} else {
				// chronologic ordering in the list ensures that if one element isnt rot, all the ones afterwards wont
				// be as well
				break;
			}
		}
	}

	public void addNewUnit(MemeManaUnit unit) {
		units.add(unit);
	}

	/**
	 * @return How much mana is currently in this pouch
	 */
	public double getContent() {
		double sum = 0;
		cleanupPouch();
		for (MemeManaUnit unit : units) {
			sum += unit.getCurrentAmount();
		}
		return sum;
	}

	/**
	 * Attempts to remove the given amount from this pouch
	 *
	 * @param amount
	 *            Amount to remove
	 * @return True if removal was successfull, false if not
	 */
	public boolean deposit(double amount) {
		if (getContent() < amount) {
			return false;
		}
		double leftToRemove = amount;
		Iterator<MemeManaUnit> iter = units.iterator();
		while (iter.hasNext() && leftToRemove > 0.0001f) {
			MemeManaUnit unit = iter.next();
			if (unit.getCurrentAmount() <= leftToRemove) {
				leftToRemove -= unit.getCurrentAmount();
				iter.remove();
				MemeManaPlugin.getInstance().getDAO().snipeManaUnit(unit);
			} else {
				unit.removeAmount(leftToRemove);
				break;
			}
		}
		return true;
	}
}
