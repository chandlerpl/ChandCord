package com.MegaCraft.ChandCord;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.MegaCraft.ChandCord.command.Commands;
import com.MegaCraft.ChandCord.configuration.ChandCordConfig;
import com.MegaCraft.ChandCord.listener.ChandCordListener;
import com.MegaCraft.ChandCord.storage.DBConnection;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.DiscordApiBuilder;
import de.btobastian.javacord.entities.ActivityType;
import de.btobastian.javacord.entities.channels.ChannelCategory;
import de.btobastian.javacord.entities.channels.ServerTextChannel;
import de.btobastian.javacord.entities.permissions.Role;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class ChandCord extends JavaPlugin {
	public static Permission permission = null;
	public static Chat chat = null;
	public static ChandCord plugin = null;
	public static ChandCordLogger logger = null;
	
	public static long serverId = 232205883189231617L;
	private static DiscordApi api = null;
	
	public static HashMap<String, Long> channelIDs = new HashMap<String, Long>();
	public static HashMap<String, Long> channelCategoryIDs = new HashMap<String, Long>();
	public static HashMap<String, Long> RoleIDs = new HashMap<String, Long>();
	
	private boolean setupVault() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		
		return (permission != null && chat != null);
	}

	@Override
	public void onEnable() {
		plugin = this;
		logger = new ChandCordLogger(this);
		
		if (!isJava8orHigher()) {
			logger.info("MegaBending requires Java 8+! Disabling Discord...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!isSpigot()) {
			logger.info("Bukkit detected, ChandCord will not function properly.");
		}

		if (setupVault()) {
			logger.info("ChandCord is now hooked with Vault");
		} else {
			logger.info("ChandCord failed to hook with Vault");
		}
		
		new ChandCordConfig(this);
		new Thread(() -> this.getServer().getPluginManager().registerEvents((Listener) new Commands(this), this)).start();
		new Thread(() -> this.getServer().getPluginManager().registerEvents((Listener) new ChandCordListener(this), this)).start();

		if (!DBConnection.init()) {
			logger.severe("Disabling due to database error");
			Bukkit.getPluginManager().disablePlugin(ChandCord.plugin);
			return;
		}
		
		if (getConfig().contains("token")) {
			String botToken = getConfig().getString("token");
			if (botToken.trim().equalsIgnoreCase(""))
				return;
			new DiscordApiBuilder().setToken(botToken).login().whenComplete((api, throwable) -> {
			    if (throwable != null) {
			    	logger.severe(throwable.toString());
					Bukkit.getPluginManager().disablePlugin(ChandCord.plugin);
			        return;
			    }
			    ChandCord.api = api;
			    
			    api.updateActivity("MegaCraft", ActivityType.WATCHING);
			    
			    api.getServerById(serverId).ifPresent(server -> {
			    	for(ServerTextChannel channel : server.getTextChannels()) {
			    		channelIDs.put(channel.getName(), channel.getId());
			    	}
			    	for(ChannelCategory channelCat : server.getChannelCategories()) {
			    		channelCategoryIDs.put(channelCat.getName(), channelCat.getId());
			    	}
			    	for(Role role : server.getRoles()) {
			    		RoleIDs.put(role.getName(), role.getId());
			    	}
			    });
			    
	            api.addMessageCreateListener(event -> {
	            	ChandCordListenersEvents.messageCreateEvent(event.getMessage(), api);
	            });
	            
	            api.addServerChannelCreateListener(event -> {
	            	if(!channelIDs.containsKey(event.getChannel().getName())) {
	            		channelIDs.put(event.getChannel().getName(), event.getChannel().getId());
	            	}
	            });
	            
	            api.addServerChannelDeleteListener(event -> {
	            	if(channelIDs.containsKey(event.getChannel().getName())) {
	            		channelIDs.remove(event.getChannel().getName());
	            	}
	            });
	            
	            api.addServerChannelChangeNameListener(event -> {
	            	if(channelIDs.containsKey(event.getOldName())) {
	            		channelIDs.remove(event.getOldName());
	            		channelIDs.put(event.getNewName(), event.getChannel().getId());
	            	}
	            });
	            
	            api.addRoleCreateListener(event -> {
	            	if(!RoleIDs.containsKey(event.getRole().getName())) {
	            		RoleIDs.put(event.getRole().getName(), event.getRole().getId());
	            	}
	            });

	            api.addRoleDeleteListener(event -> {
	            	if(RoleIDs.containsKey(event.getRole().getName())) {
	            		RoleIDs.remove(event.getRole().getName());
	            	}
	            });
	        });
		}
	}

	public static boolean isSpigot() {
		return plugin.getServer().getVersion().toLowerCase().contains("spigot");
	}

	private boolean isJava8orHigher() {
		return Integer.valueOf(System.getProperty("java.version").substring(2, 3)) >= 8;
	}

	public static DiscordApi getApi() {
		return api;
	}
	
	public void onDisable() {
		if (DBConnection.sqLite != null) {
			DBConnection.sqLite.close();
		}
		if(api != null)
			api.disconnect();
	}
}
