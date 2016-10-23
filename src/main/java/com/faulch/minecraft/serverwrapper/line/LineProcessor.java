package com.faulch.minecraft.serverwrapper.line;

/**
 * Supports objects which need the capability to respond to the generation of
 * lines of text from a line generating source.
 * 
 * @author Jonathan Faulch
 */
public interface LineProcessor
{
	/**
	 * Called when a line source has a new line which needs to be processed by
	 * its attached listeners.
	 * 
	 * @param event
	 *        an event containing the details of the line to be processed
	 */
	void processLine(LineEvent event);
}