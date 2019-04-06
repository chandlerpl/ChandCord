package com.MegaCraft.ChandCord.events;

import org.bukkit.event.Cancellable;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.channels.ServerTextChannel;
import de.btobastian.javacord.entities.message.Message;

public class DiscordMessageCreateEvent extends DiscordEvent implements Cancellable {
	/**
	 * The message instance of the event. 
	 */
	Message message;
	
	/**
	 * The channel instance of the event. 
	 */
	ServerTextChannel channel;
	
	/**
	 * The user instance of the event. 
	 */
	User user;
	
	/**
	 * Used when a new DiscordMessageCreateEvent is fired by Discord.
	 * @param message The Message that was sent.
	 * @param user The Discord User that sent the message.
	 * @param channel The ServerTextChannel which the message was sent from.
	 * @param api The api instance which the event was fired from.
	 */
	public DiscordMessageCreateEvent(Message message, User user, ServerTextChannel channel, DiscordApi api) {
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
	public ServerTextChannel getChannel() {
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
