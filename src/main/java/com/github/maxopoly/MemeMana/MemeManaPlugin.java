package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.listener.LoginListener;
import com.github.maxopoly.MemeMana.command.MemeManaCommandHandler;
import vg.civcraft.mc.civmodcore.ACivMod;

public class MemeManaPlugin extends ACivMod {

	private static MemeManaPlugin instance;

	private MemeManaConfig config;
	private PlayerActivityManager activityManager;
	private MemeManaManager manaManager;
	private MemeManaDAO dao;

	public static MemeManaPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		config = new MemeManaConfig(this);
		dao = config.setupDatabase();
		dao.cleanseManaUnits();
		manaManager = new MemeManaManager();
		activityManager = new PlayerActivityManager(manaManager);
		registerListener();
		// Register commands.
		MemeManaCommandHandler handle = new MemeManaCommandHandler();
		setCommandHandler(handle);
		handle.registerCommands();
	}

	private void registerListener() {
		getServer().getPluginManager().registerEvents(new LoginListener(), this);
	}

	@Override
	protected String getPluginName() {
		return "MemeMana";
	}

	public MemeManaConfig getManaConfig() {
		return config;
	}

	public PlayerActivityManager getActivityManager() {
		return activityManager;
	}

	public MemeManaManager getManaManager() {
		return manaManager;
	}

	public MemeManaDAO getDAO() {
		return dao;
	}

}
