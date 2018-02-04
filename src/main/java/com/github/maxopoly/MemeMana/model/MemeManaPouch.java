package com.github.maxopoly.MemeMana.model;

import com.github.maxopoly.MemeMana.MemeManaPlugin;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MemeManaPouch {

	private static NumberFormat formatter = new DecimalFormat("#####0.##");

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
		if (getContent() < amount) {
			return false;
		}
		List <MemeManaUnit> units = new LinkedList<MemeManaUnit>();
		double leftToRemove = amount;
		Iterator<MemeManaUnit> iter = units.iterator();
		while (iter.hasNext() && leftToRemove > 0.0001f) {
			MemeManaUnit unit = iter.next();
			if (unit.getCurrentAmount() <= leftToRemove) {
				leftToRemove -= unit.getCurrentAmount();
				iter.remove();
				MemeManaPlugin.getInstance().getDAO().snipeManaUnit(unit);
			} else {
				double maximumAtThisTime = unit.getOriginalAmount() * unit.getDecayMultiplier();
				double percentage = leftToRemove / maximumAtThisTime;
				unit.setFillGrade(unit.getFillGrade() - percentage);
				break;
			}
		}
		return true;
	}

}
