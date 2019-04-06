package com.MegaCraft.ChandCord.storage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitRunnable;

import com.MegaCraft.ChandCord.ChandCord;

public abstract class Database {

	protected final String prefix;
	protected final String dbprefix;
	protected Connection connection = null;

	public Database(String prefix, String dbprefix) {
		this.prefix = prefix;
		this.dbprefix = dbprefix;
	}

	/**
	 * Returns the current Connection.
	 *
	 * @return Connection if exists, else null
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Opens connection to Database.
	 *
	 * @return Connection if successful
	 */
	abstract Connection open();

	/**
	 * Close connection to Database.
	 */
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				ChandCord.logger.log(Level.SEVERE, "Got an exception!", e);
			}
		} else {
			ChandCord.logger.log(Level.INFO, "There was no SQL connection open.");
		}
	}

	/**
	 * Queries the Database, for queries which modify data.
	 *
	 * @param query
	 *            Query to run
	 */
	public void modifyQuery(final String query) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement stmt = connection.prepareStatement(query);
					stmt.execute();
					stmt.close();
				} catch (SQLException e) {
					ChandCord.logger.log(Level.SEVERE, "Got an exception!", e);
				}
			}
		}.runTaskAsynchronously(ChandCord.plugin);
	}

	/**
	 * Queries the Database, for queries which return results.
	 *
	 * @param query
	 *            Query to run
	 * @return Result set of ran query
	 */
	public ResultSet readQuery(String query) {
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			return rs;
		} catch (SQLException e) {
			ChandCord.logger.log(Level.SEVERE, "Got an exception!", e);
			return null;
		}
	}

	/**
	 * Check database to see if a table exists.
	 * 
	 * @param table
	 *            Table name to check
	 * @return true if table exists, else false
	 */
	public boolean tableExists(String table) {
		try {
			DatabaseMetaData dmd = connection.getMetaData();
			ResultSet rs = dmd.getTables(null, null, table, null);

			return rs.next();
		} catch (Exception e) {
			ChandCord.logger.log(Level.SEVERE, "Got an exception!", e);
			return false;
		}
	}
}
