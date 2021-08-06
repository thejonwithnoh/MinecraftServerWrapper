package com.faulch.minecraft.serverwrapper;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

/**
 * A static library of general purpose utility constants and functions.  Members
 * which can't find a home in other classes, and are not specific to the 
 * functionality of the application can find a home here.
 * 
 * @author Jonathan Faulch
 */
public final class Utility
{
	/**
	 * The lone private constructor used to discourage creating instances of
	 * this class, since it is intended to be a static library.
	 */
	private Utility() { }
	
	/**
	 * The platform specific line separator string.  This field exists as a
	 * cached value to prevent needless repeated system property lookups.  
	 */
	public static final String lineSeparator = System.getProperty("line.separator");

	/**
	 * Searches a directory for all files matching a given pattern.
	 *
	 * @param   directory
	 *          the directory to search for files within
	 * @param   pattern
	 *          the pattern to match files against
	 * @return  an array of files loaded from the given directory matching the
	 *          given pattern
	 */
	public static final File[] getFiles(File directory, final Pattern pattern)
	{
		File[] files = directory.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return pattern.matcher(name).matches();
			}
		});
		return files == null ? new File[0] : files;
	}

	/**
	 * Creates a <code>URLClassLoader</code> from an array of jar files
	 * provided as a <code>File</code> array.
	 *
	 * @param   files
	 *          the jar files to be loaded
	 * @return  a <code>URLClassLoader</code> using the provided jar files
	 * @throws  MalformedURLException
	 *          if the files given somehow don't convert to URLs
	 */
	public static final URLClassLoader createURLClassLoader(File... files) throws MalformedURLException
	{
		URL[] urls = new URL[files.length];
		for (int i = 0; i < files.length; i++)
		{
			urls[i] = files[i].toURI().toURL();
		}
		return URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());
	}
	
	/**
	 * Attempts to close the specified object, and returns any exceptions
	 * generated, rather than throwing them.  This is intended to simplify
	 * closing resources in a finally block, since resource handling logic in
	 * Java 6 is already convoluted enough.  This method accepts objects of any
	 * type, since not all resources implement the <code>Closable</code>
	 * interface.
	 * 
	 * @param   object
	 *          the object which represents a resource for which closing should
	 *          be attempted
	 * @return  the exception generated by attempting to close the resource, or
	 *          <code>null</code> if no exception was generated
	 */
	public static final Exception tryClose(Object object)
	{
		try
		{
			if (object != null)
			{
				object.getClass().getMethod("close").invoke(object);
			}
			return null;
		}
		catch (Exception e)
		{
			return e;
		}
	}
	
	/**
	 * Converts a <code>String</code> object, to an object of the specified
	 * type.  This is admittedly an ad hoc solution to the problem of converting
	 * strings to other types, but it does the job for now.  Should the need
	 * arise for a more sophisticated method for string conversion, then that
	 * bridge will be crossed later.
	 * 
	 * @param   value
	 *          the <code>String</code> value which will be converted into an
	 *          object of type <code>type</code>
	 * @param   type
	 *          the type of object that <code>value</code> should be converted
	 *          into
	 * @return  an object of type <code>type</code> created by converting the
	 *          <code>value</code> string
	 * @throws  IllegalArgumentException
	 *          if the supplied type is not supported
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T convertString(String value, Class<T> type)
	{
		if (type.equals(String.class))
		{
			return (T)value;
		}
		else if (type.equals(Pattern.class))
		{
			return (T)Pattern.compile(value);
		}
		else if (type.equals(File.class))
		{
			return (T)new File(value);
		}
		else
		{
			throw new IllegalArgumentException(type + " is not a supported type.");
		}
	}
}