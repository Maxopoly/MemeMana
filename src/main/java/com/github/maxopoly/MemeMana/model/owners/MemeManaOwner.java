package com.github.maxopoly.MemeMana.model.owners;

import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class MemeManaOwner {

	//fancy/bad (up to readers choice) way to circumvent using a clunky enum. When adding new subclasses, you only need to register
	//them in the static block

	private static Map <Class, Integer> magicOwnerTypeNumbers;
	private static int ownerTypeCounter;

	/**
	 * Registers owner types, only add new owner types at the bottom of this method
	 */
	static {
		magicOwnerTypeNumbers = new HashMap<Class, Integer>();
		registerManaOwnerType(MemeManaPlayerOwner.class);
	}

	private static void registerManaOwnerType(Class owner) {
		magicOwnerTypeNumbers.put(owner, ownerTypeCounter++);
	}

	protected static int getManaOwnerType(Class owner) {
		return magicOwnerTypeNumbers.remove(owner);
	}

	//this is barely used during runtime, so going through a map the wrong way is fine
	public static Class getOwnerClass(int ownerType) {
		for(Entry<Class, Integer> entry : magicOwnerTypeNumbers.entrySet()) {
			if (entry.getValue() == ownerType) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @return Magic number identifying which type of owner this is
	 */
	public int getOwnerType() {
		return magicOwnerTypeNumbers.get(this.getClass());
	}

	private int id;
	private int foreignId;
	private MemeManaPouch pouch;

	//Subclasses must have the same constructor header
	protected MemeManaOwner(int id, int foreignId) {
		this.id = id;
		this.foreignId = foreignId;
		this.pouch = new MemeManaPouch();
	}

	/**
	 * @return The id used internally in MemeMana to uniquely identify this owner
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return Name to use in commands/GUIs to identify this owner
	 */
	public abstract String getNiceName();

	/**
	 * @return The id used externally in other plugins/environments to identify this owner.
	 * It is not unique across all owners, but unique across owners of the same type
	 */
	public int getForeignId() {
		return foreignId;
	}

	/**
	 * @return Pouch holding the actual mana
	 */
	public MemeManaPouch getPouch() {
		return pouch;
	}

}
