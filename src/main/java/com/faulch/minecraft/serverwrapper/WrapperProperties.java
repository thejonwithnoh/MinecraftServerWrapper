package com.faulch.minecraft.serverwrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Manages the loading and creating of application properties.  Known properties
 * are exposed as read only properties of this object for convenience, and to
 * prevent errors associated with the overuse of string literals.
 * 
 * @author Jonathan Faulch
 */
public class WrapperProperties
{
	private static final String
		defaultPropertiesFileName = "default.properties",
		wrapperPropertiesFileName = "wrapper.properties";
	
	private Properties properties;
	private File serverDirectory;
	private Pattern serverFileRegex;
	private Pattern inputRegex;
	private Pattern outputRegex;
	private File scriptDirectory;
	private String scriptType;
	private String scriptExtension;
	private String characterEncoding;
	
	/**
	 * Creates a <code>WrapperProperties</code> object populated with the
	 * application's default properties.
	 * 
	 * @throws IOException
	 *         if the default properties could not be loaded
	 */
	public WrapperProperties() throws IOException
	{
		properties = new Properties();
		properties.load(getClass().getResourceAsStream(defaultPropertiesFileName));
		loadFields(properties);
	}
	
	/**
	 * Loads properties into this object from the external properties file, or
	 * creates the external properties file if it does not exist.
	 * 
	 * @throws IOException
	 *         if the external properties file exists but could not be read, or
	 *         if the external properties file does not exist but could not be
	 *         created
	 */
	public void loadExternalProperties() throws IOException
	{
		FileInputStream input = null;
		try
		{
			properties.load(input = new FileInputStream(wrapperPropertiesFileName));
		}
		catch (FileNotFoundException e)
		{
			FileOutputStream output = null;
			try
			{
				properties.store(output = new FileOutputStream(wrapperPropertiesFileName), null);
			}
			finally
			{
				Utility.tryClose(output);
			}
		}
		finally
		{
			Utility.tryClose(input);
		}
		loadFields(properties);
	}
	
	/**
	 * Loads all of the properties from the specified <code>Properties</code>
	 * into the fields of this object.
	 * 
	 * @param   properties
	 *          the properties object which contains the properties which will
	 *          be loaded into the fields of this object
	 * @return  <code>true</code> every property from the properties object was
	 *          successfully loaded into this object's fields;
	 *          <code>false</code> otherwise
	 */
	private boolean loadFields(Properties properties)
	{
		boolean loadedAll = true;
		for (String propertyName : properties.stringPropertyNames())
		{
			try
			{
				Field field = getClass().getDeclaredField(propertyName);
				field.setAccessible(true);
				field.set(this, Utility.convertString(properties.getProperty(propertyName), field.getType()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				loadedAll = false;
			}
		}
		return loadedAll;
	}
	
	/**
	 * Gets the directory which the application will use to search for the
	 * server file.  This will NOT become the working directory of the
	 * application or the server.
	 * 
	 * @return the server directory where the server file is located
	 */
	public File getServerDirectory()
	{
		return serverDirectory;
	}
	
	/**
	 * Gets the regex which will be used to search for the server file in the
	 * server directory.
	 * 
	 * @return the server file regex used to search for server files
	 */
	public Pattern getServerFileRegex()
	{
		return serverFileRegex;
	}
	
	/**
	 * Gets the regex used for matching against lines from standard input, which
	 * will trigger script execution.  This regex should only contain a single
	 * capture group which identifies the script command text.
	 * 
	 * @return the input regex matched against standard input
	 */
	public Pattern getInputRegex()
	{
		return inputRegex;
	}
	
	/**
	 * Gets the regex used for matching against lines from standard output,
	 * which will trigger script execution.  This regex should contain two
	 * capture groups, of which the first identifies the name of the script
	 * invoker, and the second identifies the script command text. 
	 * 
	 * @return the output regex matched against standard output
	 */
	public Pattern getOutputRegex()
	{
		return outputRegex;
	}
	
	/**
	 * Gets the directory which will be searched when attempting to execute a
	 * script.  Scripts should end with the appropriate extension, and be coded
	 * to the appropriate script type.
	 * 
	 * @return the script directory which contains scripts which can be executed
	 */
	public File getScriptDirectory()
	{
		return scriptDirectory;
	}
	
	/**
	 * Gets the name of the type of scripts which will be executed.  This will
	 * be used by the Java Scripting API to identify the script engine which
	 * should be located to execute scripts.
	 * 
	 * @return the script type which identifies which script engine will be used
	 */
	public String getScriptType()
	{
		return scriptType;
	}
	
	/**
	 * Gets the extension (including the period), of the script files in the
	 * script directory which are available for execution.
	 * 
	 * @return the script extension used by all scripts available for execution
	 */
	public String getScriptExtension()
	{
		return scriptExtension;
	}
	
	/**
	 * Gets the name of the character encoding used to decode strings which will
	 * be piped into standard input.
	 * 
	 * @return the name of the character encoding used
	 */
	public String getCharacterEncoding()
	{
		return characterEncoding;
	}
	
	/**
	 * Gets the <code>Charset</code> derived from the value of the
	 * <code>characterEncoding</code> property.  If property value is
	 * <code>null</code>, then the default <code>Charset</code> is returned.
	 * 
	 * @return the <code>Charset</code> corresponding to the
	 *         <code>characterEncoding</code> property
	 */
	public Charset getCharset()
	{
		return characterEncoding == null ? Charset.defaultCharset() : Charset.forName(characterEncoding);
	}
}