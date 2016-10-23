package com.faulch.minecraft.serverwrapper;

import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.faulch.minecraft.serverwrapper.invoker.Invoker;
import com.faulch.minecraft.serverwrapper.invoker.Player;
import com.faulch.minecraft.serverwrapper.invoker.Server;
import com.faulch.minecraft.serverwrapper.io.StandardInput;
import com.faulch.minecraft.serverwrapper.io.StandardOutput;
import com.faulch.minecraft.serverwrapper.line.LineEvent;
import com.faulch.minecraft.serverwrapper.line.LineProcessor;

/**
 * A console wrapper which monitors the standard input and output streams for
 * textual triggers which cause scripts to be executed.  This also contains
 * convenience methods which can be used by executed scripts for performing
 * useful work.
 * 
 * @author Jonathan Faulch
 */
public class Console
{
	private WrapperProperties properties;
	private StandardInput standardInput;
	private StandardOutput standardOutput;
	
	/**
	 * Creates a <code>Console</code> wrapper, which monitors the standard input
	 * and output streams for textual triggers which cause scripts to be
	 * executed.
	 * 
	 * @param  properties
	 *         the application properties used to dictate how this console
	 *         monitors the standard streams
	 */
	public Console(WrapperProperties properties)
	{
		this.properties = properties;
		this.standardInput = new StandardInput();
		this.standardOutput = new StandardOutput();
		addInputProcessor();
		addOutputProcessor();
	}
	
	/**
	 * Adds the line processor to standard input, which this console uses for
	 * parsing and executing commands.  Ideally, these commands are executed
	 * from standard input from a console/terminal window, or some other server
	 * wrapper's interface, by a server administrator.  The input regex in the
	 * applications properties will identify when a server administrator has
	 * issued a command.
	 */
	private void addInputProcessor()
	{
		standardInput.addLineProcessor(new LineProcessor()
		{
			@Override
			public void processLine(LineEvent event)
			{
				Matcher lineMatcher = properties.getInputRegex().matcher(event.getLine().trim());
				if (lineMatcher.matches())
				{
					runScript(new Server(Console.this), lineMatcher.group(1));
					event.cancel();
				}
			}
		});
	}
	
	/**
	 * Adds the line processor to standard out, which this console uses for
	 * parsing and executing commands.  Ideally, these commands are executed by
	 * players, and the output regex in the application's properties will
	 * identify when a player has issued a command.
	 */
	private void addOutputProcessor()
	{
		standardOutput.addLineProcessor(new LineProcessor()
		{
			@Override
			public void processLine(LineEvent event)
			{
				Matcher lineMatcher = properties.getOutputRegex().matcher(event.getLine().trim());
				if (lineMatcher.matches())
				{
					runScript(new Player(lineMatcher.group(1), Console.this), lineMatcher.group(2));
				}
			}
		});
	}
	
	/**
	 * Runs a script and supplies it with access to the supplied invoker,
	 * arguments, and this console.  The script execution engine is directed by
	 * this object's associated application properties.
	 * 
	 * @param  invoker
	 *         the invoker of the script which executed the supplied command
	 * @param  commandText
	 *         the command to be executed by this console
	 */
	private void runScript(Invoker invoker, String commandText)
	{
		String[] tokens = commandText.split("\\s+");
		File scriptFile = new File(properties.getScriptDirectory(), tokens[0] + properties.getScriptExtension());
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName(properties.getScriptType());
		engine.put(ScriptEngine.FILENAME, scriptFile.toString());
		FileReader reader = null;
		try
		{
			engine.put("args", tokens);
			engine.put("console", this);
			engine.put("invoker", invoker);
			engine.eval(reader = new FileReader(scriptFile));
		}
		catch (Exception e)
		{
			invoker.printError(e.getMessage());
		}
		finally
		{
			Utility.tryClose(reader);
		}
	}
	
	/**
	 * Gets the application properties associated with this console.  These
	 * properties are used to dictate the behavior of the standard input and
	 * output wrappers.
	 * 
	 * @return the application properties associated with this console
	 */
	public WrapperProperties getWrapperProperties()
	{
		return properties;
	}
	
	/**
	 * Gets the standard input wrapper associated with this console.  Used to
	 * submit commands to the server and intercept manually executed server
	 * commands for pre-processing.
	 * 
	 * @return the standard input wrapper associated with this console
	 */
	public StandardInput getStandardInput()
	{
		return standardInput;
	}
	
	/**
	 * Gets the standard output wrapper associated with this console.  Used to
	 * listen to commands which should be triggered by appearing as output.
	 * 
	 * @return the standard output wrapper associated with this console
	 */
	public StandardOutput getStandardOutput()
	{
		return standardOutput;
	}
	
	/**
	 * Executes the supplied command, by submitting it to standard input.
	 * 
	 * @param   command
	 *          the command to be executed or interpreted by any entity
	 *          listening to standard input
	 * @return  the value passed to this method as a convenience
	 */
	public String execute(String command)
	{
		standardInput.writeln(command);
		return command;
	}
	
	/**
	 * A convenience method for easily allowing scripts to sleep during
	 * execution.  This may be helpful with animating commands, or allowing the
	 * server time to catch up during processing.  Admittedly, good legitimate
	 * uses of this method will be sparse.
	 * 
	 * @param   millis
	 *          the length of time to sleep in milliseconds
	 * @return  the value passed to this method as a convenience
	 * @throws  InterruptedException
	 *          If any thread has interrupted the current thread.  The
	 *          <i>interrupted status</i> of the current thread is cleared when
	 *          this exception is thrown.
	 */
	public long sleep(long millis) throws InterruptedException
	{
		Thread.sleep(millis);
		return millis;
	}
}