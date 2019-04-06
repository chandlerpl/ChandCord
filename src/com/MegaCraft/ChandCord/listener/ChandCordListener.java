package com.MegaCraft.ChandCord.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;
import com.MegaCraft.ChandCord.configuration.ChandCordConfig;
import com.MegaCraft.ChandCord.events.DiscordMessageCreateEvent;
import com.MegaCraft.ChandCord.storage.DBConnection;

public class ChandCordListener implements Listener {
	public ChandCord plugin;
	
	public ChandCordListener(ChandCord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDiscordMessage(DiscordMessageCreateEvent event) {
		if(ChandCordConfig.server == null)
			return;
		if(event.getChannel().getId() == ChandCord.channelIDs.get(ChandCordConfig.server)) {
			String message = "";
			if(ChandCordMethods.isUserConnected(event.getAuthor(), null)) {
				String[] results = ChandCordMethods.getUserInformation(event.getAuthor(), null);
				String uuid = results[0];
				String displayName = results[1];
				OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
				if(op.isOnline()) {
					message += op.getPlayer().getDisplayName() + ": ";
				} else if(displayName != null) {
						message += displayName + ": ";
				} else {
					message += event.getAuthor().getName() + ": ";
				}
			} else {
				message += event.getAuthor().getName() + ": ";
			}
			message += event.getMessage().getContent();
			boolean chatSendRecieve = plugin.getConfig().getBoolean("chatSendRecieve");
			if(chatSendRecieve) {
				message = ChatColor.WHITE + "[" + ChatColor.BLUE + "Discord" + ChatColor.WHITE + "] " + message;
				Bukkit.broadcastMessage(message);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(ChandCordMethods.isUserConnected(null, event.getPlayer())) 
			DBConnection.modifyQuery("update chandcord_users set displayname='" + event.getPlayer().getDisplayName() 
					+ "' where uuid= '" + event.getPlayer().getUniqueId() + "'");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatMessage(AsyncPlayerChatEvent event) {
		if(ChandCordConfig.server == null)
			return;
		Player player = event.getPlayer();
		
		boolean chatSendRecieve = plugin.getConfig().getBoolean("chatSendRecieve");
		if(chatSendRecieve) {
			ChandCord.getApi().getServerById(ChandCord.serverId).ifPresent(server -> {
				server.getTextChannelById(ChandCord.channelIDs.get(ChandCordConfig.server)).ifPresent(channel -> {
					String name = ChatColor.stripColor(player.getDisplayName());
					channel.sendMessage(ChatColor.stripColor("[Minecraft] " + name + ": " + event.getMessage()));
				});
			});
		}	
	}
}
