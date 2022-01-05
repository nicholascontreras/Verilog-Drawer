package signals;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SignalComponent implements Serializable {

	private final Direction direction;
	private Point location;

	private int signalWidth;
	private boolean[] signal;

	public SignalComponent(Point location, Direction direction) {

		this.location = new Point(location);
		this.direction = direction;
		this.signalWidth = 1;
		this.signal = new boolean[signalWidth];
	}

	public Point getLocation() {
		return new Point(location);
	}

	public void setLocation(Point newLocation) {
		this.location = newLocation;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getSignalWidth() {
		return signalWidth;
	}

	public void setSignalWidth(int newSignalWidth) {
		signal = Arrays.copyOf(signal, newSignalWidth);
		this.signalWidth = newSignalWidth;
	}

	public boolean isMonoAndHigh() {
		return signal.length == 1 && signal[0];
	}

	public void setSignal(boolean[] newSignal) {
		if (newSignal.length != signalWidth) {
			throw new IllegalArgumentException("Signal width mismatch! Width of given signal (" + newSignal.length
					+ ") does not match my signal width (" + signalWidth + ")!");
		}

		signal = Arrays.copyOf(newSignal, signalWidth);
	}

	public boolean[] getSignal() {
		return Arrays.copyOf(signal, signal.length);
	}

	public enum Direction {
		INPUT, OUTPUT;
	}
}
