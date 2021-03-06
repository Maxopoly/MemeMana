package com.github.maxopoly.MemeMana;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import com.devotedmc.ExilePearl.PearlType;

public class MemeManaConfig {

	private MemeManaPlugin plugin;

	private long manaRotTime;
	private double decayMultiplier;
	private long manaDecayTime;
	private int maximumDailyMana;

	public MemeManaConfig(MemeManaPlugin plugin) {
		this.plugin = plugin;
		parse();
	}

	public void parse() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		// 90 day default
		manaRotTime = config.getLong("manaRotTime", 90L * 24L * 60L * 60L * 1000L);
		// 30 day default
		manaDecayTime = config.getLong("manaDecayTime", 30L * 24L * 60L * 60L * 1000L);
		decayMultiplier = config.getDouble("decayMultiplier", 0.5);
		maximumDailyMana = config.getInt("maxDailyMana", 10);
	}

	public MemeManaDAO setupDatabase() {
		ConfigurationSection config = plugin.getConfig().getConfigurationSection("mysql");
		String host = config.getString("host");
		int port = config.getInt("port");
		String user = config.getString("user");
		String pass = config.getString("password");
		String dbname = config.getString("database");
		int poolsize = config.getInt("poolsize");
		long connectionTimeout = config.getLong("connectionTimeout");
		long idleTimeout = config.getLong("idleTimeout");
		long maxLifetime = config.getLong("maxLifetime");
		try {
			return new MemeManaDAO(plugin, user, pass, host, port, dbname, poolsize, connectionTimeout, idleTimeout, maxLifetime);
		} catch(Exception e) {
			plugin.warning("Could not connect to database, stopping MemeMana", e);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return null;
		}
	}

	/**
	 * @return How many milliseconds it takes mana to fully disappear. -1 for never
	 */
	public long getManaRotTime() {
		return manaRotTime;
	}

	/**
	 * @return The factory by which mana is reduced after the decay interval
	 */
	public double getDecayMultiplier() {
		return decayMultiplier;
	}

	/**
	 * @return How many milliseconds it takes mana to progress through a decay stage
	 */
	public long getManaDecayTime() {
		return manaDecayTime;
	}

	/**
	 * @return How much mana a player can get on a maximum streak
	 */
	public int getMaximumDailyMana() {
		return maximumDailyMana;
	}

	/**
	 * @return How much a unit of mana refills a pearl of the given type
	 */
	public int getPearlRefillAmount(PearlType type) {
		return plugin.getConfig().getInt("pearlRefillAmount." + type);
	}
}
