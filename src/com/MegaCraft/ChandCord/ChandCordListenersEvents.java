package com.MegaCraft.ChandCord;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;

import com.MegaCraft.ChandCord.events.DiscordMessageCreateEvent;
import com.MegaCraft.ChandCord.events.DiscordMessageMentionsMeEvent;
import com.MegaCraft.ChandCord.events.DiscordPrivateMessageEvent;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;

public class ChandCordListenersEvents {
	public static void messageCreateEvent(Message message, DiscordApi api) {
    	List<User> mentionedUsers = message.getMentionedUsers();
    	List<Role> mentionedRoles = message.getMentionedRoles();
		if (message.getAuthor().getId() == api.getYourself().getId())
			return;

		message.getAuthor().asUser().ifPresent(user -> {
			message.getServerTextChannel().ifPresent(channel -> {
				if(mentionedUsers.contains(api.getYourself())) {
					Bukkit.getServer().getPluginManager().callEvent(
							new DiscordMessageMentionsMeEvent(message, user, channel, api));
					return;
				}
				api.getServerById(ChandCord.serverId).ifPresent(server -> {
					Collection<Role> roles = api.getYourself().getRoles(server);
					for(Role r : roles) {
						if(mentionedRoles.contains(r)) {
							Bukkit.getServer().getPluginManager().callEvent(
									new DiscordMessageMentionsMeEvent(message, user, channel, api));
							return;
						}
					}
				});
				
				Bukkit.getServer().getPluginManager().callEvent(
						new DiscordMessageCreateEvent(message, user, channel, api));
			});
			
			message.getPrivateChannel().ifPresent(channel -> {
				Bukkit.getServer().getPluginManager().callEvent(
						new DiscordPrivateMessageEvent(message, user, channel, api));
			});
			
		});
	}
}
