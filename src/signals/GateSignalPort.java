package signals;

import gates.LogicGate;

import java.awt.Point;

public class GateSignalPort extends SignalComponent {

	public GateSignalPort(LogicGate gate, Point location, Direction direction) {
		super(new Point(location.x + gate.getLocation().x, location.y + gate.getLocation().y), direction);
	}
}
