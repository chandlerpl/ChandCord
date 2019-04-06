package com.MegaCraft.ChandCord.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.btobastian.javacord.DiscordApi;

public abstract class DiscordEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	/**
	 * Used to check whether the Event has been cancelled by something.
	 */
	private boolean cancelled = false;
	
	/**
	 * The api instance of the event. 
	 */
	private DiscordApi api;
	
	/**
	 * Used when a new event is fired by Discord.
	 * @param api The api instance which the event was fired from.
	 */
	public DiscordEvent(DiscordApi api) {
		this.api = api;
	}

	/**
	 * Gets the api instance of the event.
	 * @return returns the api instance.
	 */
	public DiscordApi getApi() {
		return api;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	/**
	 * Returns whether the event has been cancelled or not.
	 * @return returns cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	/**
	 * Sets whether the event has been cancelled.
	 * @param cancelled Whether to set the cancelled boolean to true or false.
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
