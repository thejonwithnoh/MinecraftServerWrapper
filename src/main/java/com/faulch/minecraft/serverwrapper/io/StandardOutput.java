package com.faulch.minecraft.serverwrapper.io;

import com.faulch.minecraft.serverwrapper.line.LineEmitter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Replaces the existing standard output stream with a new standard output
 * stream, which forwards written bytes to the original standard output stream,
 * but also emits lines to subscribers.  Canceling a <code>LineEvent</code> has
 * no effect, since there is no work to be done afterwards.
 *
 * @author Jonathan Faulch
 */
public class StandardOutput extends LineEmitter {

	private final PrintStream originalOutput;

	/**
	 * Creates a <code>StandardOutput</code> object, which replaces the current
	 * standard output stream with a monitoring output stream, which forwards
	 * lines to subscribed listeners.
	 */
	public StandardOutput() {
		originalOutput = System.out;
		System.setOut(new PrintStream(new OutputStream() {
			private final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

			@Override
			public void write(int b) {
				originalOutput.write(b);
				if ("\r\n".contains(Character.toString((char) b))) {
					if (byteBuffer.size() > 0) {
						String line = byteBuffer.toString();
						byteBuffer.reset();
						emitLine(line);
					}
				} else {
					byteBuffer.write(b);
				}
			}
		}));
	}

}
