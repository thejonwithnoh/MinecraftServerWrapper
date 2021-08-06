package com.faulch.minecraft.serverwrapper.invoker;

import com.faulch.minecraft.serverwrapper.Console;

/**
 * Represents a player which has issued a command to invoke a script.  The
 * script can then use this object to gain information about the player,
 * communicate with the player, or execute commands on their behalf.
 *
 * @author Jonathan Faulch
 */
public class Player extends AbstractInvoker {

	private final String name;

	/**
	 * Creates a <code>Player</code> which can be used to identify and
	 * communicate with as the invoker of a script.
	 *
	 * @param name    the name of the player
	 * @param console the console associated with this player
	 */
	public Player(String name, Console console) {
		super(console);
		this.name = name;
	}

	/**
	 * Gets the name of this player.  This name is injected into commands which
	 * need to be run from the context of this player.
	 *
	 * @return the name of this player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Augments the command supplied, so that it appears to originate from the
	 * player, and then passes it to the console for execution.
	 *
	 * @return the command as it was augmented by this player for execution
	 */
	@Override
	public String execute(String command) {
		return getConsole().execute(String.format(getConsole().getWrapperProperties().getExecuteCommand(), name, command));
	}

	/**
	 * {@inheritDoc}
	 * The message will appear in the player's chat as white text.
	 *
	 * @return the full text of the tellraw command used to send the message
	 */
	@Override
	public String print(String message) {
		return print(message, "white");
	}

	/**
	 * {@inheritDoc}
	 * The error message will appear in the player's chat as red text.
	 *
	 * @return the full text of the tellraw command used to send the message
	 */
	@Override
	public String printError(String message) {
		return print(message, "red");
	}

	/**
	 * Sends a chat message to the player which will appear in the given color.
	 *
	 * @param message the message to send to this player
	 * @param color   the color that the sent message should appear in
	 * @return the full text of the tellraw command used to send the message
	 */
	public String print(String message, String color) {
		return tellraw("{\"text\":\"" + message.replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"color\":\"" + color + "\"}");
	}

	/**
	 * Sends a tellraw message to the player using the supplied json value.
	 *
	 * @param json the json which will be passed to the tellraw command
	 * @return the full text of the tellraw command used to send the message
	 */
	public String tellraw(String json) {
		return getConsole().execute("tellraw " + name + " " + json);
	}

}
