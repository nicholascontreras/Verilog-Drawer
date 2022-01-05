package signals;

import java.awt.*;
import java.io.Serializable;

public class Wire implements Serializable {

	private final SignalComponent source, destination;
	private boolean matchingSignalWidth;

	public Wire(SignalComponent source, SignalComponent destination) {
		this.source = source;
		this.destination = destination;
	}

	public void update() {
		if (source.getSignalWidth() == destination.getSignalWidth()) {
			matchingSignalWidth = true;
			destination.setSignal(source.getSignal());
		} else {
			matchingSignalWidth = false;
			destination.setSignal(new boolean[destination.getSignalWidth()]);
		}
	}

	public SignalComponent getSource() {
		return source;
	}

	public SignalComponent getDestination() {
		return destination;
	}

	public boolean getMatchingSignalWidth() {
		return matchingSignalWidth;
	}
}
