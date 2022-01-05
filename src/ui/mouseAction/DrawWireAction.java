package ui.mouseAction;

import signals.GateSignalPort;
import signals.SignalComponent;

import java.awt.*;

public class DrawWireAction extends MouseAction {

    private SignalComponent from;

    public DrawWireAction() {
        super(MouseActionType.DRAW_WIRE);
    }

    public void setSignalComponentFrom(SignalComponent from) {
        this.from = from;

    }

    public SignalComponent getSignalComponentFrom() {
        return from;
    }
}
