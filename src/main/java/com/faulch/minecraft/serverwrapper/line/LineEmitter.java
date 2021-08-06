package com.faulch.minecraft.serverwrapper.line;

import java.util.ArrayList;
import java.util.List;

/**
 * A convenience class for managing the subscription of line processors to a
 * line source, and the emitting of lines to subscribed processors.  It
 * maintains a list of subscribed line processors, and allows subclasses to emit
 * lines to subscribed processors.
 *
 * @author Jonathan Faulch
 */
public class LineEmitter {

	private List<LineProcessor> lineProcessors;

	/**
	 * Creates a <code>LineEmitter</code> with an empty processing list.
	 */
	public LineEmitter() {
		lineProcessors = new ArrayList<LineProcessor>();
	}

	/**
	 * Emits the specified line of text to all attached line processors, and
	 * returns whether or not the associated event was cancelled.
	 *
	 * @param line the line of text to emit to all attached line processors
	 * @return <code>true</code> if the event was cancelled during processing;
	 * <code>false</code> otherwise
	 */
	protected boolean emitLine(String line) {
		LineEvent event = new LineEvent(line);
		for (LineProcessor lineListener : lineProcessors) {
			lineListener.processLine(event);
		}
		return event.isCancelled();
	}

	/**
	 * Adds a line processor to the end of the line processing list, so that it
	 * will be called whenever a line is emitted.  A line processor can be added
	 * multiple times, so that it will be called multiple times.
	 *
	 * @param lineProcessor the line processor to append to the end of the processing list
	 * @return <code>true</code> if the line processor was successfully added
	 * to the end of the processing list; <code>false</code> otherwise.
	 * This method should always return true.
	 */
	public boolean addLineProcessor(LineProcessor lineProcessor) {
		return lineProcessors.add(lineProcessor);
	}

	/**
	 * Removes the first occurrence of a line processor from the line processing
	 * list.  Other occurrences will still exist within the list.
	 *
	 * @param lineProcessor the line processor to remove from the processing list
	 * @return <code>true</code> if an instance of the specified line processor
	 * was removed from the processing list; <code>false</code>
	 * otherwise
	 */
	public boolean removeLineProcessor(LineProcessor lineProcessor) {
		return lineProcessors.remove(lineProcessor);
	}

}
