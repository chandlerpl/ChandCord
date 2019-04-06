package com.MegaCraft.ChandCord.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public abstract class CCCommand {
	private final String name;
	private final String properUse;
	private final String description;
	private final String[] aliases;
	private final Plugin plugin;
	public static Map<Plugin, HashMap<String, CCCommand>> pluginInstances;
	public static List<String> ranks = Arrays.asList("helper", "moderator", "admin", "megaadmin", "manager", "coowner", "owner");
	
	static {
		pluginInstances = new HashMap<Plugin, HashMap<String, CCCommand>>();
	}

	public CCCommand(String name, String properUse, String description, String[] aliases, Plugin plugin) {
		HashMap<String, CCCommand> commandInstances;
		if(pluginInstances.containsKey(plugin)) {
			commandInstances = pluginInstances.get(plugin);
			pluginInstances.remove(plugin);
		} else {
			commandInstances = new HashMap<String, CCCommand>();
		}
		
		this.name = name;
		this.properUse = properUse;
		this.description = description;
		this.aliases = aliases;
		this.plugin = plugin;
		commandInstances.put(name, this);
		
		pluginInstances.put(plugin, commandInstances);
	}

	public String getName() {
		return name;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public String getProperUse() {
		return properUse;
	}

	public String getDescription() {
		return description;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public static HashMap<String, CCCommand> getPluginCommands(Plugin plugin) {
		return pluginInstances.get(plugin);
	}

	public abstract void execute(Player player, Message message, List<String> args, DiscordApi api);
	
	public void help(Player player, Message m) {
		String message = title(name) + "\n";
		message += alignment("Proper Usage") + "\"" + properUse + "\"\n";
		message += alignment("Description") + "\"" + description + "\"\n";
		message += alignment("Aliases") + Arrays.asList(aliases).toString() + "\n";

		message = (m != null) ? ChandCordMethods.mlConvertion(message) : message;
		
		sendMessage(player, m, message, false);
	}

	public static void sendMessage(Player player, Message m, String message, boolean privateMessage) {
		if(player != null) {
			player.sendMessage(message);
			return;
		} 
		if (m != null) {
			if(privateMessage) {
				m.getAuthor().asUser().ifPresent(user -> user.sendMessage(ChatColor.stripColor(message)));
			} else {
				m.getChannel().sendMessage(ChatColor.stripColor(message));
			}
			return;
		}
	}
	
	protected static boolean hasPermission(Player sender, Message message, String minimum, DiscordApi api) {
		final int minimumId = (ranks.contains(minimum.toLowerCase())) ? ranks.indexOf(minimum) : 0;
		List<String> roles = new ArrayList<String>();
		if(message != null) {
			api.getServerById(ChandCord.serverId).ifPresent(server -> {
				User user = (message.getAuthor().asUser().isPresent()) ? message.getAuthor().asUser().get() : null;
				if(user != null) {
					user.getRoles(server).forEach(role -> {
						roles.add(role.getName().toLowerCase().replace(" ", "").replace("-", ""));
					});
				}
			});
		}
		
		for(String s : ranks) {
			if(ranks.indexOf(s) >= minimumId && 
					((sender != null && ChandCord.permission.playerInGroup(sender, s))) 
					|| (message != null && roles.contains(s))) {
				return true;
			}
		}
		
		if(sender != null) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
		} else {
			message.getChannel().sendMessage("You don't have permission to do that.");
		}
		return false;
	}

	public static String title(String s) {
		String startingTitle = "——————————————————————————————————————————————————————————————————————";
		s = " '" + s + "' ";
		String titleCover = startingTitle.substring(0, (startingTitle.length() - s.length())/2);
		titleCover = titleCover + s + titleCover;
		
		if(titleCover.length() % 2 != 0) {
			titleCover += "—";
		}
		return titleCover.substring(0, startingTitle.length());
	}
	
	public static String alignment(String s) {
		String indent = "                    ";
		return s + indent.substring(0, indent.length() - s.length());
	}
	
	public static boolean isSenderIngame(Player p, Message m) {
		if(p != null) {
			return true;
		} else {
			return false;
		}
	}
}
