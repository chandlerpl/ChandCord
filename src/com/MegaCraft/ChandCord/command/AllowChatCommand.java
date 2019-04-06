package com.MegaCraft.ChandCord.command;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;
import net.md_5.bungee.api.ChatColor;

public class AllowChatCommand extends CCCommand {

	public AllowChatCommand() {
		super("allowchat", "/discord allowchat", "Changes whether messages can be sent between channels.",
				new String[] { "allowchat", "ac" }, ChandCord.plugin);
	}

	public void execute(Player player, Message message, List<String> args, DiscordApi api) {
		if(!hasPermission(player, message, "manager", api)) {
			return;
		}
		
		FileConfiguration config = ChandCord.plugin.getConfig();
		config.set("chatSendRecieve", !config.getBoolean("chatSendRecieve"));
		config.options().copyDefaults(true);
		ChandCord.plugin.saveConfig();
		
		boolean chatSendRecieve = ChandCord.plugin.getConfig().getBoolean("chatSendRecieve");
		
		String message1 = ChatColor.WHITE + "The chat is now ";
		if(chatSendRecieve) {
			message1 += "allowed";
		} else {
			message1 += "not allowed";
		}
		message1 += " between the channels.";
		
		sendMessage(player, message, ChandCordMethods.dispatchChat(message1, "Bot"), false);
	}
}
