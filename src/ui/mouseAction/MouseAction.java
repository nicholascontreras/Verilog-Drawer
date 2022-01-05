package ui.mouseAction;

import java.awt.*;

public abstract class MouseAction {

    private final MouseActionType actionType;

    public MouseAction(MouseActionType actionType) {
        this.actionType = actionType;
    }

    public MouseActionType getType() {
        return actionType;
    }

    public void cancel() {

    }

    public enum MouseActionType {
        IDLE, CREATE_GATE, MOVE_GATE, DRAW_WIRE;
    }
}
