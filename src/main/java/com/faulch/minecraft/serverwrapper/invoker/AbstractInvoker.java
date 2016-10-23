package com.faulch.minecraft.serverwrapper.invoker;

import com.faulch.minecraft.serverwrapper.Console;

/**
 * Invokers typically execute commands by having access to the console.  Also,
 * most invokers shouldn't be servers.  This class provides some default
 * implementation of an invokers methods as a convenience for subclasses.
 * 
 * @author Jonathan Faulch
 */
public abstract class AbstractInvoker implements Invoker
{
	private Console console;
	
	/**
	 * Creates an <code>AbstractInvoker</code> and associates a console with the
	 * invoker which can be used for executing commands.
	 * 
	 * @param  console
	 *         the console which will be associated with the invoker
	 */
	public AbstractInvoker(Console console)
	{
		this.console = console;
	}
	
	/**
	 * Gets the console which is associated with the invoker.  This is the
	 * console used to execute commands by default.
	 * 
	 * @return the console which is associated with the invoker
	 */
	public Console getConsole()
	{
		return console;
	}
	
	/**
	 * {@inheritDoc}
	 * Since most invokers will not be servers, this method's return value
	 * defaults to <code>false</code>.
	 */
	@Override
	public boolean isServer()
	{
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * By default, this method executes the command on the associated console.
	 */
	@Override
	public String execute(String command)
	{
		return console.execute(command);
	}
}