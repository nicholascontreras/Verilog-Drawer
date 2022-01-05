package ui.mouseAction;

import gates.LogicGate;

public class CreateGateAction extends MouseAction {

    private final LogicGate gate;

    public CreateGateAction(LogicGate gate) {
        super(MouseActionType.CREATE_GATE);
        this.gate = gate;
    }

    public LogicGate getGate() {
        return gate;
    }
}
