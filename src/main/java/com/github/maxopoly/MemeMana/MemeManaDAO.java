package com.github.maxopoly.MemeMana;

import com.github.maxopoly.MemeMana.model.ManaGainStat;
import com.github.maxopoly.MemeMana.model.MemeManaOwner;
import com.github.maxopoly.MemeMana.model.MemeManaPouch;
import com.github.maxopoly.MemeMana.model.MemeManaUnit;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

public class MemeManaDAO extends ManagedDatasource {

	private Logger logger;

	public MemeManaDAO(MemeManaPlugin plugin, String user, String pass, String host, int port, String database,
			int poolSize, long connectionTimeout, long idleTimeout, long maxLifetime) {
		super(plugin, user, pass, host, port, database, poolSize, connectionTimeout, idleTimeout, maxLifetime);
		this.logger = plugin.getLogger();
		prepareMigrations();
		updateDatabase();
	}

	/**
	 * Creates and updates tables
	 */
	private void prepareMigrations() {
		registerMigration(
				0,
				false,
				"create table if not exists manaOwners (id int auto_increment unique, foreignId int not null, foreignIdType enum('PLAYER') not null, primary key(foreignId,foreignIdType));",
				"create table if not exists manaUnits (id int not null, baseAmount double not null, fillGrade double not null default 1.0,"
						+ "date timestamp not null default now(), ownerId int not null references manaOwners(id), unique (id), index `ownerIdIndex` (ownerId), primary key(id));",
				"create table if not exists manaStats (altgroupid int primary key, streak int not null, lastDay bigint not null);");
	}

	public void cleanseManaUnits() {
		try (Connection connection = getConnection();
				PreparedStatement cleanseManaUnits = connection
						.prepareStatement("delete from manaUnits where fillGrade < ? or date < ?;")) {
			double epsilon = 0.0001;
			cleanseManaUnits.setDouble(1, epsilon);
			long currentTime = System.currentTimeMillis();
			long rotTime = MemeManaPlugin.getInstance().getManaConfig().getManaRotTime();
			cleanseManaUnits.setTimestamp(2, new Timestamp(currentTime - rotTime));
			cleanseManaUnits.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem cleansing mana units", e);
		}
	}

	public Integer getOwnerId(MemeManaOwner owner) {
		try (Connection connection = getConnection();
				PreparedStatement insertOwnerId = connection
						.prepareStatement("insert ignore into manaOwners (foreignId, foreignIdType) values(?,?);");
				PreparedStatement getOwnerId = connection
						.prepareStatement("select id from manaOwners where foreignId=? and foreignIdType=?;")) {
			insertOwnerId.setInt(1, owner.getID());
			insertOwnerId.setString(2, owner.getType().toString());
			insertOwnerId.execute();
			getOwnerId.setInt(1, owner.getID());
			getOwnerId.setString(2, owner.getType().toString());
			ResultSet rs = getOwnerId.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			return getOwnerId.executeQuery().getInt(1);
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting owner id", e);
		}
		return null;
	}


	public void addManaUnit(MemeManaUnit unit, MemeManaOwner owner) {
		try (Connection connection = getConnection();
				PreparedStatement addManaUnit = connection
						.prepareStatement("insert into manaUnits (id, baseAmount, fillGrade, date, ownerId) values(?,?,?,?,?);")) {
			addManaUnit.setInt(1, unit.getID());
			addManaUnit.setDouble(2, unit.getOriginalAmount());
			addManaUnit.setDouble(3, unit.getFillGrade());
			addManaUnit.setTimestamp(4, new Timestamp(unit.getGainTime()));
			addManaUnit.setInt(5, getOwnerId(owner));
			addManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem adding mana unit", e);
		}
	}

	public void snipeManaUnit(MemeManaUnit unit) {
		try (Connection connection = getConnection();
				PreparedStatement snipeManaUnit = connection
						.prepareStatement("delete from manaUnits where id=?;")) {
			snipeManaUnit.setInt(1, unit.getID());
			snipeManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem sniping mana unit", e);
		}
	}

	public void drainManaUnit(MemeManaUnit unit, double newPercentage) {
		try (Connection connection = getConnection();
				PreparedStatement drainManaUnit = connection
						.prepareStatement("update manaUnits set fillGrade=? where id=?;")) {
			drainManaUnit.setDouble(1, newPercentage);
			drainManaUnit.setInt(2, unit.getID());
			drainManaUnit.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem draining mana unit", e);
		}
	}

	public Map<Integer,MemeManaPouch> getManaPouches() {
		try (Connection connection = getConnection();
				PreparedStatement getManaPouches = connection
						.prepareStatement("select * from manaUnits order by ownerId, date;");
				ResultSet rs = getManaPouches.executeQuery();) {
			Map<Integer,MemeManaPouch> out = new HashMap<Integer,MemeManaPouch>();
			MemeManaPouch thisPouch = new MemeManaPouch();
			int thisOwner = -1;
			while(rs.next()) {
				if(thisOwner != rs.getInt(5)) {
					out.put(thisOwner,thisPouch);
					thisPouch = new MemeManaPouch();
					thisOwner = rs.getInt(5);
				}
				thisPouch.addNewUnit(new MemeManaUnit(rs.getInt(1),rs.getDouble(2),rs.getTimestamp(4).getTime(),rs.getDouble(3)));
			}
			out.put(thisOwner,thisPouch);
			return out;
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana pouches", e);
			return null;
		}
	}

	public Integer getNextManaId() {
		try (Connection connection = getConnection();
				PreparedStatement getNextMana = connection
						.prepareStatement("select max(id) from manaUnits;");
				ResultSet rs = getNextMana.executeQuery()) {
			rs.first();
			return rs.getInt(1) + 1; // Add one for the *next* mana id
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting next mana id", e);
			return null;
		}
	}
	private void insertChron(MemeManaUnit unit,List<MemeManaUnit> units) {
		int i = units.size() - 1;
		while(i >= 0 && unit.getGainTime() < units.get(i).getGainTime()) {
			i--;
		}
		units.add(i + 1,unit);
	}

	public void updateManaStat(MemeManaPlayerOwner owner, ManaGainStat stat) {
		try (Connection connection = getConnection();
				PreparedStatement updateManaStat = connection
						.prepareStatement("replace into manaStats (altgroupid, streak, lastDay) values(?,?,?);")) {
			updateManaStat.setInt(1, owner.getID());
			updateManaStat.setInt(2, stat.getStreak());
			updateManaStat.setLong(3, stat.getLastDay());
			updateManaStat.execute();
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem updating mana stat", e);
		}
	}

	// TODO: Don't ignore the owner type here
	public Map<Integer,ManaGainStat> getManaStats() {
		try (Connection connection = getConnection();
				PreparedStatement getManaStats = connection
						.prepareStatement("select * from manaStats;")) {
			ResultSet rs = getManaStats.executeQuery();
			Map<Integer,ManaGainStat> out = new HashMap<Integer,ManaGainStat>();
			while(rs.next()) {
				out.put(rs.getInt(1),new ManaGainStat(rs.getInt(2),rs.getLong(3)));
			}
			return out;
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Problem getting mana stats", e);
			return null;
		}
	}
}
