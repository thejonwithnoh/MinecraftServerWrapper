package com.faulch.minecraft.serverwrapper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * A MinecraftServerWrapper wraps around a Minecraft server by monitoring its
 * standard input and output streams, and responding to textual triggers.  Most
 * of the functionality is performed by the contained <code>Console</code>
 * object.  This class puts all the pieces together by loading application
 * properties, initiating the wrapper console, and then starting the Minecraft
 * server.
 * 
 * @author Jonathan Faulch
 */
public class MinecraftServerWrapper
{
	private WrapperProperties properties;
	
	/**
	 * Starts a MinecraftServerWrapper application by loading properties,
	 * creating a console wrapper, and starting the Minecraft server. 
	 * 
	 * @param  args
	 *         the command line arguments passed to the application
	 * @throws IOException
	 *         if the default properties could not be loaded
	 */
	public MinecraftServerWrapper(String... args) throws IOException
	{
		loadProperties();
		new Console(properties);
		startServer(args);
	}
	
	/**
	 * Loads the wrapper's default properties, then loads the external
	 * properties if they exist, otherwise it creates the external properties. 
	 * 
	 * @throws IOException
	 *         if the default properties could not be loaded
	 */
	private void loadProperties() throws IOException
	{
		properties = new WrapperProperties();
		try
		{
			properties.loadExternalProperties();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the server file as determined by the <code>serverFileRegex</code>
	 * property.  If the server file regex locates multiple files, then the
	 * "maximum" file is selected (as determined by the natural ordering of
	 * <code>File</code>. 
	 * 
	 * @return the server file as determined by the <code>serverFileRegex</code>
	 *         property
	 */
	private File getServerFile()
	{
		File[] serverFiles = properties.getServerDirectory().listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return properties.getServerFileRegex().matcher(name).matches();
			}
		});
		Arrays.sort(serverFiles);
		return serverFiles[serverFiles.length - 1]; 
	}
	
	/**
	 * Starts the server with the specified command line arguments.  The server
	 * is loaded using the system class loader, and therefore exists in the
	 * exact same context as the wrapper application.
	 * 
	 * @param  args
	 *         the command line arguments passed to the application, which will
	 *         be forwarded to the server
	 * @return <code>true</code> if the server started successfully;
	 *         <code>false</code> otherwise
	 */
	private boolean startServer(String... args)
	{
		try
		{
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			File serverFile = getServerFile();
			method.invoke(ClassLoader.getSystemClassLoader(), serverFile.toURI().toURL());
			Class.forName(new JarFile(serverFile).getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS))
				.getMethod("main", String[].class).invoke(null, (Object)args);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Starts a MinecraftServerWrapper application by simply delegating its
	 * arguments to the <code>MinecraftServerWrapper</code> constructor.
	 * 
	 * @param  args
	 *         the command line arguments passed to the application
	 * @throws IOException
	 *         if the default properties could not be loaded
	 */
	public static void main(String... args) throws IOException
	{
		new MinecraftServerWrapper(args);
	}
}