package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import com.github.maxopoly.MemeMana.model.owners.MemeManaOwner;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MemeManaPouch {

	private static NumberFormat formatter = new DecimalFormat("#####0.##");

	// chronologically ordered!
	private List<MemeManaUnit> units;
	private MemeManaOwner owner;

	public MemeManaPouch(MemeManaOwner owner) {
		this.units = new ArrayList<MemeManaUnit>();
		this.owner = owner;
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


	/**
	 * Adds the given unit to the pouch without adding it to the database. For most use cases, consider using the createMana() method instead
	 * @param unit Mana to add
	 */
	public void addNewUnit(MemeManaUnit unit) {
		units.add(unit);
	}

	/**
	 * Sorts the internal representation of mana units. This should only be used once after loading from the database
	 */
	public void sortManaChronologically() {
		units.sort(Comparator.comparing(MemeManaUnit::getGainTime));
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
	 * Due to the internal representation as double, mana may be a very ugly decimal amount.
	 * To avoid ugly UI's, this method rounds the value to two decimal points
	 * @return Mana content as a string for user output
	 */
	public String getFormattedContent() {
		return formatter.format(getContent());
	}

	/**
	 * Attempts to remove the given amount from this pouch, deleting it permanently
	 *
	 * @param amount
	 *            Amount to remove
	 * @return True if successfull, false if not
	 */
	public boolean deposit(double amount) {
		MemeManaUnit newestDeletedUnit = null;
		if (getContent() < amount) {
			return false;
		}
		double leftToRemove = amount;
		Iterator<MemeManaUnit> iter = units.iterator();
		while (iter.hasNext() && leftToRemove > 0.0001f) {
			MemeManaUnit unit = iter.next();
			if (unit.getCurrentAmount() <= leftToRemove) {
				leftToRemove -= unit.getCurrentAmount();
				unit.setFillGrade(0.0);
				iter.remove();
				newestDeletedUnit = unit;
			} else {
				double maximumAtThisTime = unit.getOriginalAmount() * unit.getDecayMultiplier();
				double percentage = leftToRemove / maximumAtThisTime;
				unit.setFillGrade(unit.getFillGrade() - percentage);
				MemeManaPlugin.getInstance().getDAO().updateManaFillGrade(unit);
				break;
			}
		}
		if (newestDeletedUnit != null) {
			MemeManaPlugin.getInstance().getDAO().deleteManaUnitsUpUntil(owner, newestDeletedUnit);
		}
		return true;
	}

	/**
	 * Attempts to transfer the given amount from this pouch to another
	 *
	 * @param amount
	 *            Amount to remove
	 * @return True if successfull, false if not
	 */
	public boolean transfer(MemeManaOwner receiver, double amount) {
		MemeManaUnit newestDeletedUnit = null;
		if (getContent() < amount) {
			return false;
		}
		double leftToRemove = amount;
		Iterator<MemeManaUnit> iter = units.iterator();
		while (iter.hasNext() && leftToRemove > 0.0001f) {
			MemeManaUnit unit = iter.next();
			if (unit.getCurrentAmount() <= leftToRemove) {
				leftToRemove -= unit.getCurrentAmount();
				unit.setFillGrade(0.0);
				iter.remove();
				newestDeletedUnit = unit;
			} else {
				double maximumAtThisTime = unit.getOriginalAmount() * unit.getDecayMultiplier();
				double percentage = leftToRemove / maximumAtThisTime;
				unit.setFillGrade(unit.getFillGrade() - percentage);
				MemeManaPlugin.getInstance().getDAO().updateManaFillGrade(unit);
				MemeManaUnit partialRest = new MemeManaUnit(id, originalAmount)
				//TODO figure out where to put unit ticket pulling
				break;
			}
		}
		if (newestDeletedUnit != null) {
			MemeManaPlugin.getInstance().getDAO().transferManaUnitsUpUntil(owner, receiver, newestDeletedUnit);
		}
		return true;
	}

}
