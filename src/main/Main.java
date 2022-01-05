package main;

import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import gates.AndGate;
import gates.Input;
import gates.NotGate;
import gates.OrGate;
import ui.VerilogDrawerUI;

public class Main {

	public static void main(String[] args) {
		GateLayout gl = new GateLayout();
		new VerilogDrawerUI(gl);
	}

}
