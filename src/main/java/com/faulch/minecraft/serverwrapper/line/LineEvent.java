package com.faulch.minecraft.serverwrapper.line;

/**
 * Used to pass information between line generators and line processors.  The
 * <code>LineEvent</code> has a <code>cancelled</code> flag which indicates
 * whether or not the line generator should continue with its own processing
 * after all <code>LineProcessor</code> objects have been notified.  Once an
 * event has been cancelled, the flag cannot be reversed.
 *
 * @author Jonathan Faulch
 */
public class LineEvent {

	private final String line;
	private boolean cancelled;

	/**
	 * Creates a <code>LineEvent</code> with an associated line of text.
	 *
	 * @param line the line of text associated with this event
	 */
	public LineEvent(String line) {
		this.line = line;
		cancelled = false;
	}

	/**
	 * Marks this event as cancelled.
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Gets the line of text associated with this event.
	 *
	 * @return the line of text associated with this event
	 */
	public String getLine() {
		return line;
	}

	/**
	 * Determines whether or not this event has been cancelled.
	 *
	 * @return <code>true</code> if this event has been cancelled;
	 * <code>false</code> otherwise
	 */
	public boolean isCancelled() {
		return cancelled;
	}

}
