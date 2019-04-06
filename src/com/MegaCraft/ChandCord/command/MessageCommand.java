package com.MegaCraft.ChandCord.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;

public class MessageCommand extends CCCommand {
	public MessageCommand() {
		super("message", "/dc message (message)", "Sends a message to the other program.", new String[] { "message", "m" }, ChandCord.plugin);
	}

	@Override
	public void execute(Player player, Message message, List<String> args, DiscordApi api) {
		if(!hasPermission(player, message, "megaadmin", api)) {
			return;
		}
		
		String message1 = String.join(" ", args.subList(1, args.size()));

		if(isSenderIngame(player, message)) {
			ChandCordMethods.sendMessageToDiscord("general", message1, api);
		} else {
			Bukkit.broadcastMessage(ChandCordMethods.dispatchChat(message1, "Bot"));
		}
	}
}
