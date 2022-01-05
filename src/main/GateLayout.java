package main;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import gates.LogicGate;
import signals.GateSignalPort;
import signals.SignalComponent;
import signals.Wire;

public class GateLayout implements Serializable {

	private final Set<LogicGate> logicGates;
	private final Set<Wire> wires;

	public GateLayout() {
		logicGates = new HashSet<LogicGate>();
		wires = new HashSet<Wire>();
	}

	public void addLogicGate(LogicGate gate) {
		logicGates.add(gate);
	}

	public Set<LogicGate> getLogicGates() {
		return new HashSet<LogicGate>(logicGates);
	}

	public void addWire(Wire wire) {
		wires.add(wire);
	}

	public Set<Wire> getWires() {
		return new HashSet<Wire>(wires);
	}

	public void update() {
		Set<LogicGate> deletedGates = new HashSet<LogicGate>();
		for (LogicGate curGate : logicGates) {
			if (curGate.isDeleted()) {
				deletedGates.add(curGate);
			}
		}

		for (LogicGate curGate : deletedGates) {
			removeLogicGate(curGate);
		}

		for (LogicGate curGate : logicGates) {
			curGate.update();
		}

		for (Wire curWire : wires) {
			curWire.update();
		}
	}

	private void removeLogicGate(LogicGate gate) {
		if (!logicGates.contains(gate)) {
			throw new IllegalArgumentException("Gate not in layout!");
		}

		logicGates.remove(gate);
		for (GateSignalPort gsp : gate.exposeSignalPorts()) {
			for (Wire curWire : wires) {
				if (curWire.getSource() == gsp || curWire.getDestination() == gsp) {
					// Removing elements in a for-each loop allowable because we immediately break
					wires.remove(curWire);
					break;
				}
			}
		}
	}

	public SignalComponent getSignalAvailableComponentAt(Point point) {
		SignalComponent closestSignalComponent = null;
		double closestDistance = 10;
		for (SignalComponent curSignalComponent : getAvailableSignalComponents()) {
			double curDistance = Math.hypot(curSignalComponent.getLocation().x - point.x,
					curSignalComponent.getLocation().y - point.y);

			if (curDistance < closestDistance) {
				closestSignalComponent = curSignalComponent;
				closestDistance = curDistance;
			}
		}
		return closestSignalComponent;
	}

	public Set<SignalComponent> getAvailableSignalComponents() {
		Set<SignalComponent> occupiedSignalPorts = new HashSet<>();

		for (Wire curWire : wires) {
			occupiedSignalPorts.add(curWire.getSource());
			occupiedSignalPorts.add(curWire.getDestination());
		}

		Set<SignalComponent> availableSignalPorts = new HashSet<>();
		for (LogicGate curLogicGate : logicGates) {
			for (GateSignalPort curGateSignalPort : curLogicGate.exposeSignalPorts()) {
				if (!occupiedSignalPorts.contains(curGateSignalPort)) {
					availableSignalPorts.add(curGateSignalPort);
				}
			}
		}

		return availableSignalPorts;
	}
}
