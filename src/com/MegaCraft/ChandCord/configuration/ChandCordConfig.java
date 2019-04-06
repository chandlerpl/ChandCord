package com.MegaCraft.ChandCord.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import com.MegaCraft.ChandCord.ChandCord;

import main.MegaData;

public class ChandCordConfig {

	static ChandCord plugin;
	static FileConfiguration config;
	static FileConfiguration MDConfig;
	public static String server;
	
	public ChandCordConfig(ChandCord plugin) {
		ChandCordConfig.plugin = plugin;
		config = ChandCordConfig.plugin.getConfig();
		
		if(ChandCord.plugin.getServer().getPluginManager().getPlugin("MegaData") != null) {
			MDConfig = MegaData.plugin.getConfig();
			server = MDConfig.getString("Storage.ServerID").toLowerCase();
		}
		
		loadConfigCore();
	}

	private void loadConfigCore() {
		config.addDefault("token", "Mzk0MTEyNDg5NDAwNDM0Njg5.DR_ldQ.RZnmt8bru3AF2iOt5vnoEaPheWk");
		config.addDefault("chatSendRecieve", false);

		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
}
