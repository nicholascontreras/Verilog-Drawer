package gates;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import signals.GateSignalPort;
import signals.SignalComponent.Direction;

public class OrGate extends LogicGate {

	private final GateSignalPort input1, input2, output;

	public OrGate() {
		input1 = new GateSignalPort(this, new Point(10, 30), Direction.INPUT);
		input2 = new GateSignalPort(this, new Point(10, 70), Direction.INPUT);
		output = new GateSignalPort(this, new Point(90, 50), Direction.OUTPUT);
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = createBaseImage();
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));

		drawSignalPortLeads(g2d);
		g2d.setColor(Color.BLACK);
		drawGateBody(g2d);

		g2d.dispose();
		return image;
	}

	public static BufferedImage getPreviewImage() {
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.BLACK);

		// Draw input and output leads
		g2d.drawLine(10, 30, 15, 30);
		g2d.drawLine(10, 70, 15, 70);
		g2d.drawLine(85, 50, 90, 50);

		drawGateBody(g2d);

		g2d.dispose();
		return image;
	}

	private static void drawGateBody(Graphics2D g2d) {
		g2d.drawArc(-10, 10, 30, 80, 90, -180);
		g2d.drawLine(5, 10, 40, 10);
		g2d.drawLine(5, 90, 40, 90);
		g2d.drawArc(-10, 10, 100, 160, 30, 60);
		g2d.drawArc(-10, -70, 100, 160, 330, -60);
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		return new GateSignalPort[] { input1, input2, output };
	}

	@Override
	public double getAspectRatio() {
		return 1;
	}

	@Override
	public void update() {
		boolean[] outputSignal = new boolean[output.getSignalWidth()];
		for (int i = 0; i < outputSignal.length; i++) {
			outputSignal[i] = input1.getSignal()[i] || input2.getSignal()[i];
		}

		output.setSignal(outputSignal);
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit OR Gate", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperDataWidthPanel = new JPanel();
		upperDataWidthPanel.setLayout(new BoxLayout(upperDataWidthPanel, BoxLayout.X_AXIS));
		upperDataWidthPanel.add(new JLabel("Signal Width: "));
		JSpinner dataWidthField = new JSpinner(
				new SpinnerNumberModel(output.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		upperDataWidthPanel.add(dataWidthField);

		outerPanel.add(upperDataWidthPanel, BorderLayout.NORTH);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			int newSignalWidth = (int) dataWidthField.getModel().getValue();
			input1.setSignalWidth(newSignalWidth);
			input2.setSignalWidth(newSignalWidth);
			output.setSignalWidth(newSignalWidth);
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);

		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}
}
