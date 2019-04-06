package com.MegaCraft.ChandCord.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.MegaCraft.ChandCord.ChandCord;

public class SQLite extends Database {
	private String location;
	private String database;
	private File SQLfile;

	public SQLite(String prefix, String database, String location) {
		super(prefix, "[SQLite] ");
		this.database = database;
		this.location = location;

		File folder = new File(this.location);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.SQLfile = new File(folder.getAbsolutePath() + File.separator + this.database);
	}

	@Override
	public Connection open() {
		try {
			Class.forName("org.sqlite.JDBC");

			this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.SQLfile.getAbsolutePath());
			ChandCord.logger.log(Level.INFO, "Connection established.");

			return this.connection;
		} catch (ClassNotFoundException e) {
			ChandCord.logger.log(Level.SEVERE, "JDBC driver not found!", e);
			return null;
		} catch (SQLException e) {
			ChandCord.logger.log(Level.SEVERE, "SQLite exception during connection.", e);
			return null;
		}
	}

}
