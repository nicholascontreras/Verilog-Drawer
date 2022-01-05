package ui.mouseAction;

import gates.LogicGate;

import java.awt.*;

public class MoveGateAction extends MouseAction {

    private final LogicGate gate;
    private final Point initialMouseLocation;
    private final Point initialGateLocation;

    public MoveGateAction(LogicGate gate, Point mouseLocation) {
        super(MouseActionType.MOVE_GATE);

        this.gate = gate;
        this.initialMouseLocation = mouseLocation;
        this.initialGateLocation = gate.getLocation();
    }

    @Override
    public void cancel() {
        gate.setLocation(initialGateLocation);
    }

    public LogicGate getGate() {
        return gate;
    }

    public Point getInitialMouseLocation() {
        return initialMouseLocation;
    }

    public Point getInitialGateLocation() {
        return initialGateLocation;
    }
}
