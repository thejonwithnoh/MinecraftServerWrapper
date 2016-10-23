package com.faulch.minecraft.serverwrapper.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.faulch.minecraft.serverwrapper.Utility;
import com.faulch.minecraft.serverwrapper.line.LineEmitter;

/**
 * Replaces the existing standard input stream with a new standard input stream,
 * which forwards written lines to the original standard input stream, but also
 * emits lines to subscribers.  Canceling a <code>LineEvent</code> emitted from
 * this object will prevent the line from being forwarded to the new standard
 * input stream.
 * 
 * @author Jonathan Faulch
 */
public class StandardInput extends LineEmitter
{
	private InputStream originalInput;
	private BlockingQueue<String> lines;
	private ByteArrayInputStream lineStream;
	
	/**
	 * Creates a <code>StandardInput</code> object which replaces the current 
	 * standard input stream with a monitoring standard input stream, which
	 * forwards lines from the original standard input, allows for injecting
	 * lines into standard input, and only provides bytes in batches, one line
	 * at a time.
	 */
	public StandardInput()
	{
		originalInput = System.in;
		lines = new LinkedBlockingQueue<String>();
		lineStream = new ByteArrayInputStream(new byte[0]);
		setStandardInput();
		startForwardingThread();
	}
	
	/**
	 * Creates the new standard input stream, which reads lines from a blocking
	 * queue, and offers up bytes from each line as they become available.
	 */
	private void setStandardInput()
	{
		System.setIn(new InputStream()
		{
			/**
			 * {@inheritDoc}
			 * This implementation will block until an entire line of text is
			 * available to be read.
			 */
			@Override
			public int read() throws IOException
			{
				int value = lineStream.read();
				if (value == -1)
				{
					try
					{
						String line;
						while (emitLine(line = lines.take()));
						lineStream = new ByteArrayInputStream((line + Utility.lineSeparator).getBytes());
						value = lineStream.read();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						value = -1;
					}
				}
				return value;
			}
			
			/**
			 * Reads at most one byte from the input stream and writes int into
			 * the supplied array.  This method has been overriden, so that
			 * <code>BufferedReader</code>s don't attempt to fill their entire
			 * buffer.  The default implementation will always try to fill as
			 * much of the array as possible, which is not the desired behavior
			 * for a <code>BufferedReader</code> reading from standard input.
			 * {@inheritDoc}
			 */
			@Override
			public int read(byte[] b, int off, int len) throws IOException
			{
				if (b == null)
				{
					throw new NullPointerException();
				}
				else if (off < 0 || len < 0 || len > b.length - off)
				{
					throw new IndexOutOfBoundsException();
				}
				else if (len == 0)
				{
					return 0;
				}

				int c = read();
				if (c == -1)
				{
					return -1;
				}
				b[off] = (byte)c;
				return 1;
			}
		});
	}
	
	/**
	 * Starts a daemon thread, which reads from the original standard input
	 * stream, and forwards lines which are read to the new standard input
	 * stream for processing.
	 */
	private void startForwardingThread()
	{
		Thread forwardingThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(originalInput));
					String line;
					while ((line = reader.readLine()) != null)
					{
						lines.add(line);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		forwardingThread.setDaemon(true);
		forwardingThread.start();
	}
	
	/**
	 * Passes a line of text to the underlying standard input stream.
	 * 
	 * @param  line
	 *         the line of text to be passed to standard input
	 */
	public void writeln(String line)
	{
		lines.add(line);
	}
}