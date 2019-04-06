package com.MegaCraft.ChandCord;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.MegaCraft.ChandCord.storage.DBConnection;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.channels.ServerChannel;
import de.btobastian.javacord.entities.channels.ServerTextChannelBuilder;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.permissions.PermissionState;
import de.btobastian.javacord.entities.permissions.PermissionType;
import de.btobastian.javacord.entities.permissions.PermissionsBuilder;
import de.btobastian.javacord.entities.permissions.RoleBuilder;

public class ChandCordMethods {
	public static HashMap<String, Boolean> discordNotificationsPerPlugin = new HashMap<String, Boolean>();
	public static HashMap<String, List<String>> storedMessages = new HashMap<String, List<String>>();

	/** Rewrite **/
	public static void broadcastMessage(Plugin plugin, String message, boolean send, boolean sendToDiscord) {
		String pluginName = plugin.getName();
		message = dispatchChat(message, "Bot");
		
		if(discordNotificationsPerPlugin.containsKey(pluginName)) {
			if(discordNotificationsPerPlugin.get(pluginName) && send) {
				Bukkit.broadcastMessage(message);
				if(sendToDiscord) {
					ChandCordMethods.sendMessageToDiscord("general", ChatColor.stripColor(message).substring(15), null);
				}
			} else {
				if(send) {
					List<String> storedMessage = new ArrayList<String>();
					if(storedMessages.containsKey(plugin.getName())) {
						storedMessage = storedMessages.get(plugin.getName());
						storedMessages.remove(plugin.getName());
					}
					storedMessage.add(message);
					storedMessages.put(plugin.getName(), storedMessage);
				}
			}
		} else {
			Bukkit.broadcastMessage(message);
			if(sendToDiscord) {
				ChandCordMethods.sendMessageToDiscord("general", ChatColor.stripColor(message).substring(15), null);
			}
		}
	}
	
	/** Rewrite **/
	public static String dispatchChat(String message, String plugin) {
		String message1 = ChatColor.WHITE + "[" + ChatColor.BLUE + plugin + ChatColor.WHITE + "] " + ChatColor.DARK_RED + "MegaBot" + ChatColor.WHITE + ": ";
		message1 += message;
		return message1;
	}

	/** Rewrite **/
	public static boolean getBroadcastMessagesAllowed(Plugin plugin) {
		return discordNotificationsPerPlugin.get(plugin.getName());
	}

	/** Rewrite **/
	public static void setBroadcastMessagesAllowed(Plugin plugin, boolean discordNotificationsEnabled) {
		if(discordNotificationsPerPlugin.containsKey(plugin.getName())) {
			discordNotificationsPerPlugin.remove(plugin.getName());
		}
		discordNotificationsPerPlugin.put(plugin.getName(), discordNotificationsEnabled);
		if(discordNotificationsEnabled == false) {
			BukkitRunnable runnable = new BukkitRunnable() {
			    @Override
			    public void run() {
					if(discordNotificationsPerPlugin.get(plugin.getName())) {
						String messageToSend = "";
						List<String> storedMessage = storedMessages.get(plugin.getName());
						for(String message : storedMessage) {
							messageToSend += message + "\n";
						}
						storedMessages.remove(plugin.getName());
						broadcastMessage(plugin, messageToSend, true, true);
						cancel();
					}
			    }
			};
			runnable.runTaskTimerAsynchronously(ChandCord.plugin, 0, 1);
		}
	}

	/**
	 * Converts the sent String into an ml code block for Discord.
	 * @param message
	 * @return
	 */
	public static String mlConvertion(String message) {
		MessageBuilder mb = new MessageBuilder();
		mb.appendCode("ml", ChatColor.stripColor(message));
		return mb.toString();
	}

	public static void createChannel(String name, String category, DiscordApi api) {
		if(api == null) {
			api = ChandCord.getApi();
		}
		api.getServerById(ChandCord.serverId).ifPresent(server -> {
			server.getChannelCategoryById(ChandCord.channelCategoryIDs.get(category)).ifPresent(channelCategory -> {
				ServerTextChannelBuilder cb = new ServerTextChannelBuilder(server);
				cb.setName(name);
				cb.setCategory(channelCategory);
				cb.create().whenComplete((channel, throwable) -> {
					if(throwable != null) {
						ChandCord.logger.log(Level.SEVERE, "Error creating a channel!", throwable);
						return;
					}
					RoleBuilder rb = new RoleBuilder(server);
					rb.setName(name);
					rb.setDisplaySeparately(false);
					rb.setMentionable(false);
					rb.setColor(Color.gray);
					rb.setPermissions(server.getEveryoneRole().getPermissions());
					rb.create().whenComplete((role, roleThrowable) -> {
						if(roleThrowable != null) {
							ChandCord.logger.log(Level.SEVERE, "Error creating a channel role!", roleThrowable);
							return;
						}
						
						channel.asServerChannel()
				        .map(ServerChannel::getUpdater)
				        .ifPresent(serverChannelUpdater -> serverChannelUpdater.addPermissionOverwrite(
				                role,
				                new PermissionsBuilder()
				                        .setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED)
				                        .setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED)
				                        .build()).addPermissionOverwrite(
				                server.getEveryoneRole(), 
                        		new PermissionsBuilder()
				                        .setState(PermissionType.READ_MESSAGES, PermissionState.DENIED)
				                        .setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED)
				                        .build())
				        		.update().whenComplete((r, thr) -> {
									ChandCord.logger.log(Level.SEVERE, "Error updating the " + role.getName() + " role permissions!", thr);
				        		}));
					});
				});
			});
		});
	}
	
	public static void deleteChannel(String name, DiscordApi api) {
		if(api == null) {
			api = ChandCord.getApi();
		}
		api.getServerById(ChandCord.serverId).ifPresent(server -> {
			server.getRoleById(ChandCord.RoleIDs.get(name)).ifPresent(role -> {
				role.delete().whenComplete((r, throwable) -> {
					if(throwable != null) {
						ChandCord.logger.log(Level.SEVERE, "Error deleting a channel role!", throwable);
						return;
					}
				});
			});
			server.getChannelById(ChandCord.channelIDs.get(name.toLowerCase())).ifPresent(channel -> {
				channel.delete().whenComplete((c, throwable) -> {
					if(throwable != null) {
						ChandCord.logger.log(Level.SEVERE, "Error deleting a channel!", throwable);
						return;
					}
				});
			});
		});
	}

	public static void addUserToRole(Player player, String roleName, DiscordApi api) {
		if(api == null) {
			api = ChandCord.getApi();
		}
		
		User user = (isUserConnected(null, player) && api.getUserById(getUserInformation(null, player)[0]).isPresent()) 
				? api.getUserById(getUserInformation(null, player)[0]).get() : null;
		
		if(user == null) {
			ChandCord.logger.log(Level.INFO, "Error getting User.");
			return;
		}
		
		api.getServerById(ChandCord.serverId).ifPresent(server -> {
			server.getRoleById(ChandCord.RoleIDs.get(roleName)).ifPresent(role -> {
				server.getUpdater().addRoleToUser(user, role).update().whenComplete((u, throwable) -> {
					if(throwable != null) {
						ChandCord.logger.log(Level.SEVERE, "Error adding " + user.getName() + " to the " + role.getName() + " role!", throwable);
						return;
					}
					ChandCord.logger.log(Level.INFO, "Added " + user.getName() + " to the " + role.getName() + " role!");
				});
				ChandCord.logger.log(Level.INFO, "Test");
			});
		});
	}
	
	public static void removeUserToRole(Player player, String roleName, DiscordApi api) {
		if(api == null) {
			api = ChandCord.getApi();
		}
		
		User user = (isUserConnected(null, player) && api.getChannelById(getUserInformation(null, player)[0]).isPresent()) 
				? api.getUserById(getUserInformation(null, player)[0]).get() : null;
		
		if(user == null) {
			return;
		}
		
		api.getServerById(ChandCord.serverId).ifPresent(server -> {
			server.getRoleById(ChandCord.RoleIDs.get(roleName)).ifPresent(role -> {
				server.getUpdater().removeRoleFromUser(user, role);
			});
		});
	}
	
	/**
	 * Used to send messages to the Discord server in the channel indicated as the bot.
	 * @param channelName The name of the channel that you want to message sent to.
	 * @param message The message which you want sent to Discord.
	 * @param api The DiscordApi which is needed to retrieve the servers and send to Discord. Gained by using DiscordEvent#getApi().
	 */
	public static void sendMessageToDiscord(String channelName, String message, DiscordApi api) {
		if(api == null) {
			api = ChandCord.getApi();
		}
		
		long ID = ChandCord.channelIDs.get(channelName);
		api.getServerById(ChandCord.serverId).ifPresent(server -> {
			server.getTextChannelById(ID).ifPresent(channel -> {
				channel.sendMessage(ChatColor.stripColor(message));
			});
		});
	}

	/**
	 * Will send true if the User or Player sent is in the Discord-MC Database.
	 * @param discordUser A Javacord User entity used to check if a User is in the Database.
	 * @param player A Bukkit Player entity used to check if a Player is in the Database.
	 * @return Returns true if the User or Player can be found in the connection Database.
	 */
	public static Boolean isUserConnected(User discordUser, Player player) {
		String type = (discordUser != null) ? "discordid" : "uuid";
		String value = (discordUser != null) ? discordUser.getIdAsString() : player.getUniqueId().toString();
		ResultSet rs2 = DBConnection.readQuery("SELECT * FROM chandcord_users WHERE " + type + "='" + value +"'");
		
		try {
			while(rs2.next()) {
				return true;
			}
			rs2.close();
		}
		catch (SQLException ex) {
			ChandCord.logger.log(Level.SEVERE, "Got an exception!", ex);
		}
		return false;
	}

	/**
	 * Will return the MC UUID if User is sent or a Discord UserID if Player is sent and the MC displayname of the player.
	 * @param discordUser A Javacord User entity used to check if a User is in the Database.
	 * @param player A Bukkit Player entity used to check if a Player is in the Database.
	 * @return Returns a String[] with the two values, the first one will be the ID for either MC or Discord (the opposite to the one sent) and the second is the Minecraft displayname.
	 */
	public static String[] getUserInformation(User discordUser, Player player) {
		String type = (discordUser != null) ? "discordid" : "uuid";
		String value = (discordUser != null) ? discordUser.getIdAsString() : player.getUniqueId().toString();
		String returnType = (discordUser != null) ? "uuid" : "discordid";
		ResultSet rs2 = DBConnection.readQuery("SELECT * FROM chandcord_users WHERE " + type + "='" + value +"'");
		try {
			while(rs2.next()) {
				return (rs2.getString(returnType) + " #!!!# " + rs2.getString("displayname")).split(" #!!!# ", 2);
			}
			rs2.close();
		}
		catch (SQLException ex) {
			ChandCord.logger.log(Level.SEVERE, "Got an exception!", ex);
		}
		return null;
	}
}
