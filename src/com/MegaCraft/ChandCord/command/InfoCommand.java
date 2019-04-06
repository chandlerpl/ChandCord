package com.MegaCraft.ChandCord.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;

public class InfoCommand extends CCCommand {
	public InfoCommand() {
		super("info", "/ChandCord info", "Displays the information of the installed ChandCord plugin.",
				new String[] { "info", "i" }, ChandCord.plugin);
	}

	public void execute(Player player, Message m, List<String> args, DiscordApi api) {
		String message = "";
		
		message += title("ChandCord") + "\n";
		message += alignment("Description") + "\"" + "A plugin developed to integrate Discord into Minecraft. "
				+ "Allowing chat and commands to be sent between Discord and Minecraft." + "\"\n";
		message += alignment("Developer") + "\"" + "Chandler" + "\"\n";
		message += alignment("Version") + ChandCord.plugin.getDescription().getVersion() + "\n";
		
		message += "\n" + title("ConnectedPlugins") + "\n";
		message += alignment("Plugin Name") + "\"" + "Number of commands" + "\"\n";
		
		for(Plugin plugin : CCCommand.pluginInstances.keySet()) {
			message += alignment(plugin.getName()) + CCCommand.getPluginCommands(plugin).size() + "\n";
		}
		
		message += "\n" + title("Commands") + "\n";
		for(Plugin plugin : CCCommand.pluginInstances.keySet()) {
			message += "'" + plugin.getName() + "'" + "\n";
			for(CCCommand command : CCCommand.getPluginCommands(plugin).values()) {
				String name = command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1, command.getName().length());
				message += alignment(name) + "\"" + command.getDescription() + "\"\n";
			}
		}
		
		message = (m != null) ? ChandCordMethods.mlConvertion(message) : message;
		
		sendMessage(player, m, message, false);
	}
}
