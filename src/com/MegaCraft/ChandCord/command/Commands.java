package com.MegaCraft.ChandCord.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.events.DiscordMessageMentionsMeEvent;
import com.MegaCraft.ChandCord.events.DiscordPrivateMessageEvent;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class Commands implements Listener {
	public static HashMap<Plugin, ArrayList<String>> commandAliases = new HashMap<Plugin, ArrayList<String>>();

	public Commands(ChandCord plugin) {
		ArrayList<String> commandAliases = new ArrayList<String>();
		commandAliases.add("chandcord");
		commandAliases.add("dc");
		commandAliases.add("cc");
		Commands.commandAliases.put(ChandCord.plugin, commandAliases);
		
		new AllowChatCommand();
		new HelpCommand();
		new MessageCommand();
		new ResetUsernameCommand();
		new VerifyCommand();
		new InfoCommand();
	}
	
	private boolean commandInterface(Player sender, Message message, String[] args, DiscordApi api) {
		List<String> sendingArgs = null;
		
		for(Plugin plugin : Commands.commandAliases.keySet()) {
			ArrayList<String> commandAliases = Commands.commandAliases.get(plugin);

			if (!commandAliases.contains(args[0].toLowerCase())) {
				continue;
			}
			
			if (commandAliases.contains(args[0].toLowerCase()) && args.length < 2) {
				if(CCCommand.pluginInstances.get(plugin).containsKey("info")) {
					CCCommand.pluginInstances.get(plugin).get("info").execute(sender, message, null, api);
				} else if(CCCommand.pluginInstances.get(plugin).containsKey("help")) {
					CCCommand.pluginInstances.get(plugin).get("help").execute(sender, message, null, api);
				}
				return false;
			}
			
			sendingArgs = Arrays.asList(args).subList(1, args.length);
			for(CCCommand dccommand : CCCommand.pluginInstances.get(plugin).values()) {
				if(Arrays.asList(dccommand.getAliases()).contains(args[1].toLowerCase())) {
					dccommand.execute(sender, message, sendingArgs, api);
					return true;
				}
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if(commandInterface(event.getPlayer(), null, event.getMessage().replace("/", "").split("\\s+"), null)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPrivateDiscordCommand(DiscordPrivateMessageEvent event) {
		if(commandInterface(null, event.getMessage(), event.getMessage().getContent().split("\\s+"), event.getApi())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDiscordCommand(DiscordMessageMentionsMeEvent event) {
		if(commandInterface(null, event.getMessage(), event.getMessage()
				.getContent().replace("<@394112489400434689> ", "").replace("<@&401568270257487872> ", "")
				.split("\\s+"), event.getApi())) {
			event.setCancelled(true);
		}
	}
}
