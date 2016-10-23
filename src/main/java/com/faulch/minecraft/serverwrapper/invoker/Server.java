package com.faulch.minecraft.serverwrapper.invoker;

import java.io.PrintStream;

import com.faulch.minecraft.serverwrapper.Console;

/**
 * Represents a server which has issued a command to invoke a script.  The
 * script can then use this object to gain information about the server,
 * communicate with the server, or execute commands on behalf of the server.
 * 
 * @author Jonathan Faulch
 */
public class Server extends AbstractInvoker
{
	/**
	 * Creates a <code>Server</code> which can be used to identify and
	 * communicate with as the invoker of a script.
	 * 
	 * @param  console
	 *         the console associated with this server
	 */
	public Server(Console console)
	{
		super(console);
	}
	
	/**
	 * Determines whether or not this invoker represents a server. This is
	 * intended as a convenience method to make it easier for scripts to perform
	 * logic based in the type of invoker that ran the script.  This will always
	 * return <code>true</code>, since this object will always be a server. 
	 * 
	 * @return  <code>true</code>, since this object will always be a server
	 */
	@Override
	public boolean isServer()
	{
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * The message is sent to the standard output stream.
	 */
	@Override
	public String print(String message)
	{
		return print(message, System.out);
	}
	
	/**
	 * {@inheritDoc}
	 * The message is sent to the standard error stream.
	 */
	@Override
	public String printError(String message)
	{
		return print(message, System.err);
	}
	
	/**
	 * Prints the specified message to the specified stream, and returns the
	 * message for convenience.
	 * 
	 * @param   message
	 *          the message to print to the specified stream
	 * @param   stream
	 *          the stream to have the specified message printed to
	 * @return  the value passed to this method as a convenience
	 */
	private String print(String message, PrintStream stream)
	{
		stream.println(message);
		return message;
	}
}