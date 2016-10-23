package com.faulch.minecraft.serverwrapper.invoker;

/**
 * Represents an entity which can be held responsible for executing a script.
 * This entity also exposes its ability to execute commands, and can be alerted
 * with messages and errors.
 * 
 * @author Jonathan Faulch
 */
public interface Invoker
{
	/**
	 * Determines whether or not this invoker represents a server.  This is
	 * intended as a convenience method to make it easier for scripts to perform
	 * logic based in the type of invoker that ran the script.
	 * 
	 * @return <code>true</code> if this invoker is a server; <code>false</code>
	 *         otherwise
	 */
	boolean isServer();
	
	/**
	 * Executes a command originating from this invoker.
	 * 
	 * @param   command
	 *          a command to be executed by this invoker
	 * @return  the value passed to this method as a convenience
	 */
	String execute(String command);
	
	/**
	 * Sends a message to this invoker.
	 * 
	 * @param   message
	 *          a message to be sent to this invoker
	 * @return  the value passed to this method as a convenience
	 */
	String print(String message);
	
	/**
	 * Sends an error message to this invoker.
	 * 
	 * @param   message
	 *          an error message to be sent to this invoker
	 * @return  the value passed to this method as a convenience
	 */
	String printError(String message);
}