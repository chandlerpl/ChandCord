package com.MegaCraft.ChandCord;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import com.MegaCraft.ChandCord.command.CCCommand;

public class ChandCordLogger extends Logger {
    private String pluginName;
    private Plugin plugin;
    /**
     * Creates a new ChandCordLogger that extracts the name from a plugin.
     *
     * @param context A reference to the plugin
     */
    public ChandCordLogger(Plugin context) {
        super(context.getClass().getCanonicalName(), null);
        String prefix = context.getDescription().getPrefix();
        plugin = context;
        pluginName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getServer().getLogger());
        setLevel(Level.ALL);
    }

    public void sendToDiscord(Level level, String msg, Throwable thrown) {
    	if(ChandCord.getApi() != null) {
	        String message = CCCommand.title(plugin.getDescription().getName()) + "\n";
	        message += CCCommand.alignment("Level") + level.toString().toLowerCase() + "\n";
	        message += CCCommand.alignment("Message") + "\"" + msg + "\"\n";
	        if(thrown != null) {
		        message += CCCommand.alignment("Thrown Error") + "\"" + thrown.getMessage() + "\"\n";
	        }
	        
	    	ChandCordMethods.sendMessageToDiscord("super_staff", ChandCordMethods.mlConvertion(message), null);
    	}
    }
    
    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(pluginName + logRecord.getMessage());
        sendToDiscord(logRecord.getLevel(), logRecord.getMessage(), logRecord.getThrown());
        super.log(logRecord);
    }
    
    @Override
    public void log(Level level, String msg, Throwable thrown) {
    	LogRecord logRecord = new LogRecord(level, pluginName + msg + "\n" + thrown.getMessage());
    	sendToDiscord(level, msg, thrown);
        super.log(logRecord);
    }
    
    @Override
    public void log(Level level, String msg) {
    	LogRecord logRecord = new LogRecord(level, pluginName + msg);
    	sendToDiscord(level, msg, null);
        super.log(logRecord);
    }
    
    @Override
    public void finest(String msg) {
    	log(Level.FINEST, msg);
    }
    
    @Override
    public void finer(String msg) {
    	log(Level.FINER, msg);
    }
    
    @Override
    public void fine(String msg) {
    	log(Level.FINE, msg);
    }
    
    @Override
    public void config(String msg) {
    	log(Level.CONFIG, msg);
    }
    
    @Override
    public void info(String msg) {
    	log(Level.INFO, msg);
    }
    
    @Override
    public void warning(String msg) {
    	log(Level.WARNING, msg);
    }
    
    @Override
    public void severe(String msg) {
    	log(Level.SEVERE, msg);
    }
}
