package com.MegaCraft.ChandCord.events;

import org.bukkit.event.Cancellable;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.channels.PrivateChannel;
import de.btobastian.javacord.entities.message.Message;

public class DiscordPrivateMessageEvent extends DiscordEvent implements Cancellable {
	/**
	 * The message instance of the event. 
	 */
	Message message;
	
	/**
	 * The channel instance of the event. 
	 */
	PrivateChannel channel;
	
	/**
	 * The user instance of the event. 
	 */
	User user;
	
	public DiscordPrivateMessageEvent(Message message, User user, PrivateChannel channel, DiscordApi api) {
		super(api);
		this.message = message;
		this.channel = channel;
		this.user = user;
	}

	/**
	 * Gets the message instance of the event.
	 * @return returns the message instance.
	 */
	public Message getMessage() {
		return message;
	}
	
	/**
	 * Gets the channel instance of the event.
	 * @return returns the channel instance.
	 */
	public PrivateChannel getChannel() {
		return channel;
	}
	
	/**
	 * Gets the user instance of the event.
	 * @return returns the user instance.
	 */
	public User getAuthor() {
		return user;
	}
}