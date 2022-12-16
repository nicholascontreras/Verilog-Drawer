package gates;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import main.GateLayout;
import signals.GateSignalPort;
import signals.SignalComponent.Direction;
import signals.Wire;

public class ComboGate extends LogicGate {

	private static final int PORT_SPACING = 20;

	private String name;

	private final Set<LogicGate> logicGates;
	private final Set<Wire> wires;

	private LinkedHashMap<GateSignalPort, Input> inputs;
	private LinkedHashMap<Output, GateSignalPort> outputs;

	public ComboGate(String defaultName, GateLayout layout) {
		this.name = defaultName;

		logicGates = new HashSet<LogicGate>(layout.getLogicGates());
		wires = new HashSet<Wire>(layout.getWires());

		ArrayList<Input> sortedInputGates = new ArrayList<Input>();
		ArrayList<Output> sortedOutputGates = new ArrayList<Output>();
		for (LogicGate curGate : logicGates) {
			if (curGate instanceof Input) {
				Input curInput = (Input) curGate;
				if (curInput.isExposedExternally()) {
					sortedInputGates.add(curInput);
				}
			} else if (curGate instanceof Output) {
				sortedOutputGates.add((Output) curGate);
			}
		}

		sortedInputGates.sort((in1, in2) -> in1.getLocation().y - in2.getLocation().y);
		sortedOutputGates.sort((out1, out2) -> out1.getLocation().y - out2.getLocation().y);

		setSize(new Dimension(getSize().width,
				(int) ((Math.max(sortedInputGates.size(), sortedOutputGates.size()) + 2.5) * PORT_SPACING)));

		int curY = PORT_SPACING * 2;
		inputs = new LinkedHashMap<GateSignalPort, Input>();
		for (Input curInputGate : sortedInputGates) {
			GateSignalPort gsp = new GateSignalPort(this, new Point(10, curY), Direction.INPUT);
			gsp.setSignalWidth(curInputGate.exposeSignalPorts()[0].getSignalWidth());
			inputs.put(gsp, curInputGate);
			curY += PORT_SPACING;
		}

		curY = PORT_SPACING * 2;
		outputs = new LinkedHashMap<Output, GateSignalPort>();
		for (Output curOutputGate : sortedOutputGates) {
			GateSignalPort gsp = new GateSignalPort(this, new Point(90, curY), Direction.OUTPUT);
			gsp.setSignalWidth(curOutputGate.exposeSignalPorts()[0].getSignalWidth());
			outputs.put(curOutputGate, gsp);
			curY += PORT_SPACING;
		}
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = createBaseImage();
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));

		drawSignalPortLeads(g2d);

		g2d.setColor(Color.WHITE);
		g2d.fillRoundRect(15, 15, 70, image.getHeight() - 30, 5, 5);

		g2d.setColor(Color.BLACK);
		g2d.drawRoundRect(15, 15, 70, image.getHeight() - 30, 5, 5);

		int comboGateNameWidth = g2d.getFontMetrics().stringWidth(name);
		g2d.drawString(name, 50 - (comboGateNameWidth / 2), 30);

		FontMetrics fm = g2d.getFontMetrics();
		int fontHeight = fm.getAscent() - fm.getDescent() - fm.getLeading();

		int curY = PORT_SPACING * 2 + fontHeight / 2;
		for (Input inputLogicGate : inputs.values()) {
			g2d.drawString(inputLogicGate.getName(), 20, curY);
			curY += PORT_SPACING;
		}

		curY = PORT_SPACING * 2 + fontHeight / 2;
		for (Output outputLogicGate : outputs.keySet()) {
			int nameWidth = g2d.getFontMetrics().stringWidth(outputLogicGate.getName());
			g2d.drawString(outputLogicGate.getName(), 80 - nameWidth, curY);
			curY += PORT_SPACING;
		}

		g2d.dispose();
		return image;
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		GateSignalPort[] ports = new GateSignalPort[inputs.size() + outputs.size()];

		GateSignalPort[] inputsArray = inputs.keySet().toArray(new GateSignalPort[0]);
		for (int i = 0; i < inputsArray.length; i++) {
			ports[i] = inputsArray[i];
		}

		GateSignalPort[] outputsArray = outputs.values().toArray(new GateSignalPort[0]);
		for (int i = 0; i < outputsArray.length; i++) {
			ports[i + inputsArray.length] = outputsArray[i];
		}

		return ports;
	}

	@Override
	public double getAspectRatio() {
		if (inputs == null) {
			return 1;
		} else {
			double height = (Math.max(inputs.size(), outputs.size()) + 2.5) * PORT_SPACING;
			return DEFAULT_SIZE.width / height;
		}
	}

	@Override
	public void update() {
		for (GateSignalPort inputGSP : inputs.keySet()) {
			boolean[] signal = inputGSP.getSignal();
			inputs.get(inputGSP).exposeSignalPorts()[0].setSignal(signal);
		}

		for (LogicGate curGate : logicGates) {
			curGate.update();
		}

		for (Wire curWire : wires) {
			curWire.update();
		}

		for (Output outputLogicGate : outputs.keySet()) {
			boolean[] signal = outputLogicGate.exposeSignalPorts()[0].getSignal();
			outputs.get(outputLogicGate).setSignal(signal);
		}
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit Input", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperNamePanel = new JPanel();
		upperNamePanel.setLayout(new BoxLayout(upperNamePanel, BoxLayout.X_AXIS));
		upperNamePanel.add(new JLabel("Name: "));
		JTextField nameField = new JTextField(name);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		upperNamePanel.add(nameField);

		outerPanel.add(upperNamePanel, BorderLayout.NORTH);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			// Update the name
			name = nameField.getText();
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);
		dialog.pack();

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}
}
