package com.MegaCraft.ChandCord.command;

import java.util.List;

import org.bukkit.entity.Player;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;
import net.md_5.bungee.api.ChatColor;

public class ResetUsernameCommand extends CCCommand {

	public ResetUsernameCommand() {
		super("resetusername", "/ChandCord message", "Resets your Discord nickname.",
				new String[] { "resetusername", "ru" }, ChandCord.plugin);
	}

	public void execute(Player player, Message message, List<String> args, DiscordApi api) {
		if(player != null) {
			player.sendMessage(ChandCordMethods.dispatchChat(ChatColor.RED + "This is a Discord only command.", "Bot"));
		} else if (message != null) {
			api.getServerById(ChandCord.serverId).ifPresent(server -> {
				message.getAuthor().asUser().ifPresent(user -> {
					user.resetNickname(server);
					message.getChannel().sendMessage("Your username has been reset.");
				});
			});
		}
	}
}
