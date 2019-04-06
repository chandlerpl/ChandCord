package com.MegaCraft.ChandCord.storage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.configuration.ChandCordConfig;

import dbStorage.megaDB;

public class DBConnection {

	public static Database sqLite;

	public static boolean isOpen = false;

	public static boolean init() {
		if(ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) {
			try {
				if(((dbStorage.MYSQL)megaDB.sql).getConnection().isClosed()) {
					ChandCord.logger.log(Level.SEVERE, "MySQL from MegaData is not open!");
					if(reopenConnection())
						return true;
					
					return false;
				}
			} catch (SQLException e) {
				ChandCord.logger.log(Level.SEVERE, "Exception caught.", e);
			}
		} else {
			sqLite = new SQLite("Establishing SQLite Connection.", "ChandCord.db",
					ChandCord.plugin.getDataFolder().getAbsolutePath());
			if (((SQLite) sqLite).open() == null) {
				return false;
			}
		}
		isOpen = true;
		
		if (!tableExists("chandcord_users")) {
			ChandCord.logger.info("Creating chandcord_users table.");
			String query = "CREATE TABLE `chandcord_users` (" + "`uuid` VARCHAR(36) PRIMARY KEY," 
			+ "`discordid` TEXT(255), `displayname` TEXT(255));";
			modifyQuery(query);
		}

		if (!tableExists("chandcord_messages")) {
			ChandCord.logger.info("Creating chandcord_messages table.");
			String query = "CREATE TABLE `chandcord_messages` (" + "`messageid` INT(8) PRIMARY KEY,"
			+ "`sender` INT(8), `reciever` INT(8), `command` TEXT(255), `message` TEXT);";
			modifyQuery(query);
		}

		if (!tableExists("chandcord_servers")) {
			ChandCord.logger.info("Creating chandcord_servers table.");
			String query = "CREATE TABLE `chandcord_servers` (`id` INT(8) PRIMARY KEY, `name` TEXT(255));";
			modifyQuery(query);
		}
		
		boolean inDatabase = false;
		ResultSet rs = DBConnection.readQuery("SELECT * FROM chandcord_servers WHERE name='" + ChandCordConfig.server +"'");
		try {
			while(rs.next()) {
				inDatabase = true;
			}
			rs.close();
		} catch (SQLException ex) {
			ChandCord.logger.log(Level.SEVERE, "Got an exception!", ex);
		}
		
		if(inDatabase)
			modifyQuery("INSERT INTO chandcord_servers VALUES (" + ChandCordConfig.server + "," + false + ")");
		
		return true;
	}
	
	public static boolean reopenConnection() {
		try {
			return (ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) 
				? (((dbStorage.MYSQL)megaDB.sql).getConnection().isClosed()) ? ((dbStorage.MYSQL)megaDB.sql).open() != null : false
				: (sqLite.getConnection().isClosed()) ? sqLite.open() != null : false;
		} catch (SQLException e) {
			ChandCord.logger.log(Level.SEVERE, "SQL Exception!", e);
			return false;
		}
	}
	
	public static boolean tableExists(String table) {
		if(reopenConnection())
			ChandCord.logger.log(Level.INFO, "SQL has been reconnected.");
		return (ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) 
				? megaDB.sql.tableExists(table) : sqLite.tableExists(table);
	}
	
	public static void modifyQuery(String query) {
		if(reopenConnection())
			ChandCord.logger.log(Level.INFO, "SQL has been reconnected.");
		if(ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) {
			megaDB.sql.modifyQuery(query);
		} else {
			sqLite.modifyQuery(query);
		}
	}
	
	public static ResultSet readQuery(String query) {
		if(reopenConnection())
			ChandCord.logger.log(Level.INFO, "SQL has been reconnected.");
		return (ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) 
				? megaDB.sql.readQuery(query) : sqLite.readQuery(query);
	}
	
	public static Connection getConnection() {
		if(reopenConnection())
			ChandCord.logger.log(Level.INFO, "SQL has been reconnected.");
		return (ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) 
				? megaDB.sql.getConnection() : sqLite.getConnection();
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
}
