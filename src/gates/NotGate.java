package gates;

import signals.GateSignalPort;
import signals.SignalComponent;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class NotGate extends LogicGate {

	private final GateSignalPort input, output;

	public NotGate() {
		input = new GateSignalPort(this, new Point(10, 50), SignalComponent.Direction.INPUT);
		output = new GateSignalPort(this, new Point(90, 50), SignalComponent.Direction.OUTPUT);
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = createBaseImage();
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));

		// Draw the input and output leads
		drawSignalPortLeads(g2d);

		// Draw the triangle part of the gate
		Polygon mainTriangle = createPoly(new Point(15, 20), new Point(15, 80), new Point(75, 50));
		g2d.setColor(Color.WHITE);
		g2d.fill(mainTriangle);

		g2d.setColor(Color.BLACK);
		g2d.draw(mainTriangle);

		// Draw the bubble to follow the inverted output
		if (output.isMonoAndHigh()) {
			g2d.setColor(Color.RED);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.fillOval(75, 45, 10, 10);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(75, 45, 10, 10);

		g2d.dispose();

		return image;
	}

	public static BufferedImage getPreviewImage() {
		BufferedImage image = new BufferedImage(DEFAULT_SIZE.width, DEFAULT_SIZE.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));

		Polygon mainTriangle = createPoly(new Point(15, 20), new Point(15, 80), new Point(75, 50));
		g2d.draw(mainTriangle);
		g2d.drawOval(75, 45, 10, 10);

		g2d.drawLine(10, 50, 15, 50);
		g2d.drawLine(85, 50, 90, 50);
		g2d.dispose();

		return image;
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		return new GateSignalPort[] { input, output };
	}

	@Override
	public void update() {
		boolean[] invertedSignal = new boolean[input.getSignalWidth()];
		for (int i = 0; i < invertedSignal.length; i++) {
			invertedSignal[i] = !input.getSignal()[i];
		}
		output.setSignal(invertedSignal);
	}

	@Override
	public double getAspectRatio() {
		return 1;
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit NOT Gate", true);
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
			input.setSignalWidth(newSignalWidth);
			output.setSignalWidth(newSignalWidth);
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);

		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}
}
