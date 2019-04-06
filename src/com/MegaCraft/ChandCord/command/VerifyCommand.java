package com.MegaCraft.ChandCord.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.MegaCraft.ChandCord.ChandCord;
import com.MegaCraft.ChandCord.ChandCordMethods;
import com.MegaCraft.ChandCord.storage.DBConnection;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class VerifyCommand extends CCCommand {
	private static HashMap<Long, User> verifyList = new HashMap<Long, User>();
	
	public VerifyCommand() {
		super("verify", "/ChandCord verify", "Connects your Discord account to your Minecraft account.",
				new String[] { "verify", "v" }, ChandCord.plugin);
	}

	/**
	 * rewrite
	 */
	public void execute(Player player, Message m, List<String> args, DiscordApi api) {
		if(args.size() == 2) {
			if(args.get(1).equalsIgnoreCase("remove")) {
				String result = "";
				String type = "";
				if(m != null) {
					result = String.valueOf(m.getAuthor().getId());
					type = "discordid";
				} else if(player != null) {
					result = player.getUniqueId().toString();
					type = "uuid";
				}
				
			    try {
			      String query = "delete from chandcord_users where " + type + "= '" + result + "'";
			      DBConnection.modifyQuery(query);
			      String message = "Your accounts have been disconnected from each other.";
			      sendMessage(player, m, ChandCordMethods.dispatchChat(message, "Bot"), true);
			    } catch (Exception e) {
			    	ChandCord.logger.log(Level.SEVERE, "Got an exception!", e);
			    }
				return;
			}
		}

		if(m != null && ChandCordMethods.isUserConnected(m.getAuthor().asUser().get(), null)) {
			m.getAuthor().asUser().get().sendMessage("This Discord account is already connected to a Minecraft account, if you need to connect to a new account then please disconnect @MegaBot verify remove on ChandCord or /dc verify remove in game");
		} else if(m != null && !verifyList.containsValue(m.getAuthor().asUser().get())) {
			Random rand = new Random();
			boolean valueSet = false;
			Long key = rand.nextLong();
			
			while(!valueSet) {
				if(verifyList.containsKey(key)) {
					key = rand.nextLong();
				} else {
					valueSet = true;
				}
			}
			verifyList.put(key, m.getAuthor().asUser().get());
			
			m.getAuthor().asUser().get().sendMessage("This is your verification key: " + key.toString() + "\nIt will be valid until next restart.");
		} else if (player != null) {
			if(ChandCordMethods.isUserConnected(null, player)) {
				sendMessage(player, m, ChandCordMethods.dispatchChat("This Minecraft account is already connected to a Discord account, if you need to connect to a new account then please disconnect using @MegaBot verify remove on Discord or /dc verify remove in game", "Bot"), false);
			}
			Long key = Long.parseLong(args.get(1));
			
			if(verifyList.containsKey(key)) {
				User DiscordUser = verifyList.get(key);
				
				boolean alreadyConnected = false;
				ResultSet rs2 = DBConnection.readQuery("SELECT * FROM chandcord_users WHERE uuid='" +player.getUniqueId().toString() +"'");
				try {
					while(rs2.next()) {
						alreadyConnected = true;
					}
					rs2.close();
				}
				catch (SQLException ex) {
					ChandCord.logger.log(Level.SEVERE, "Got an exception!", ex);
				}
				
				rs2 = DBConnection.readQuery("SELECT * FROM chandcord_users WHERE discordid='" + DiscordUser.getId() +"'");
				try {
					while(rs2.next()) {
						alreadyConnected = true;
					}
					rs2.close();
				}
				catch (SQLException ex) {
					ChandCord.logger.log(Level.SEVERE, "Got an exception!", ex);
				}
				
				if(alreadyConnected) {
					String message = "One of your accounts have already been connected to an account.";
					sendMessage(player, m, message, true);
					verifyList.remove(key);
				} else {
					if(addUser(player, DiscordUser)) {
						String message = "Your Minecraft account by the name of " + player.getName() + " and your Discord account by the name of " + DiscordUser.getName() + " have been connected.";
						sendMessage(player, m, message, true);
						verifyList.remove(key);
					} else {
						String message = "The accounts have not been disconnected, there has been an error. Please speak to a member of staff.";
						sendMessage(player, m, message, true);
					}
				}
			} else {
				String message = "Invalid key.";
				sendMessage(player, null, ChandCordMethods.dispatchChat(message,"Bot"), true);
			}
		} else {
			
		}
	}
	
	private static boolean addUser(Player p, User user)
	{
		Connection conn = null;
		PreparedStatement ps = null;
    try {
        conn = DBConnection.getConnection();
        String sql = "INSERT INTO chandcord_users (uuid, discordid, displayname) VALUES (?,?,?)";
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, p.getUniqueId().toString());
		ps.setString(2, String.valueOf(user.getId()));
		ps.setString(3, p.getDisplayName());
        
        ps.executeUpdate();
        return true;
    } catch (SQLException ex) {
        ChandCord.logger.log(Level.SEVERE, "Could not execute update!", ex);
    } finally {
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException ex) {
        	ChandCord.logger.log(Level.SEVERE, "Exception caught, closing database.", ex);
        }
    }
    return false;
	}
}
