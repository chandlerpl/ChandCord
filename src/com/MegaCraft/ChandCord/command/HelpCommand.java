package com.MegaCraft.ChandCord.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;

public class HelpCommand extends CCCommand {

	public HelpCommand() {
		super("help", "/ChandCord help", "This command provides information on how to use other commands in the ChandCord plugin.", 
				new String[] { "help", "h" }, ChandCord.plugin);
	}
	
	public void execute(Player player, Message m, List<String> args, DiscordApi api) {
		if (args == null || args.size() == 1) {
			String message = "";
			String discordMessage = "";
			message += title("ChandCord") + "\n";
			
			discordMessage += title("ChandCord") + "\n";
			discordMessage += alignment("Number Of Commands") + CCCommand.getPluginCommands(ChandCord.plugin).size() + "\n";

			discordMessage += "\n" + title("Commands") + "\n";
			for(CCCommand command : CCCommand.getPluginCommands(ChandCord.plugin).values()) {
				String name = command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1, command.getName().length());
				message += name + " - " + command.getDescription() + "\n";
				discordMessage += alignment(name) + "\"" + command.getDescription() + "\"\n";
			}
			message = (m != null) ? ChandCordMethods.mlConvertion(discordMessage) : message;

			sendMessage(player, m, message, false);
			return;
		}
		
		String arg = args.get(1);
		if(CCCommand.getPluginCommands(ChandCord.plugin).keySet().contains(arg.toLowerCase())) {
			CCCommand.getPluginCommands(ChandCord.plugin).get(arg).help(player, m);
		}
	}
}
