package gates;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import signals.GateSignalPort;
import signals.SignalComponent.Direction;

public class ConcatGate extends LogicGate {

	private final GateSignalPort input1, input2, output;

	public ConcatGate() {
		input1 = new GateSignalPort(this, new Point(10, 45), Direction.INPUT);
		input2 = new GateSignalPort(this, new Point(10, 70), Direction.INPUT);

		output = new GateSignalPort(this, new Point(90, 50), Direction.OUTPUT);
		output.setSignalWidth(2);
	}

	private static void drawGateBody(Graphics2D g2d) {
		g2d.drawRoundRect(15, 15, 70, 70, 5, 5);
		int textWidth = g2d.getFontMetrics().stringWidth("CONCAT");
		g2d.drawString("CONCAT", 50 - textWidth / 2, 30);
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

		g2d.drawLine(5, 45, 15, 45);
		g2d.drawLine(5, 70, 15, 70);
		g2d.drawLine(85, 50, 95, 50);

		drawGateBody(g2d);

		g2d.dispose();
		return image;
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
			if (i < input1.getSignalWidth()) {
				outputSignal[i] = input1.getSignal()[i];
			} else {
				outputSignal[i] = input2.getSignal()[i - input1.getSignalWidth()];
			}
		}

		output.setSignal(outputSignal);
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit Concat", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel centerDataWidthPanel = new JPanel();
		centerDataWidthPanel.setLayout(new BoxLayout(centerDataWidthPanel, BoxLayout.Y_AXIS));

		JPanel upperInputDataWidthPanel = new JPanel();
		upperInputDataWidthPanel.setLayout(new BoxLayout(upperInputDataWidthPanel, BoxLayout.X_AXIS));
		upperInputDataWidthPanel.add(new JLabel("Upper Input Width: "));
		JSpinner upperInputDataWidthSpinner = new JSpinner(
				new SpinnerNumberModel(input1.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		upperInputDataWidthPanel.add(upperInputDataWidthSpinner);

		centerDataWidthPanel.add(upperInputDataWidthPanel);

		centerDataWidthPanel.add(Box.createVerticalStrut(5));

		JPanel lowerInputDataWidthPanel = new JPanel();
		lowerInputDataWidthPanel.setLayout(new BoxLayout(lowerInputDataWidthPanel, BoxLayout.X_AXIS));
		lowerInputDataWidthPanel.add(new JLabel("Lower Input Width: "));
		JSpinner lowerInputDataWidthSpinner = new JSpinner(
				new SpinnerNumberModel(input2.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		lowerInputDataWidthPanel.add(lowerInputDataWidthSpinner);

		centerDataWidthPanel.add(lowerInputDataWidthPanel);

		outerPanel.add(centerDataWidthPanel, BorderLayout.CENTER);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			int newUpperInputWidth = (int) upperInputDataWidthSpinner.getModel().getValue();
			int newLowerInputWidth = (int) lowerInputDataWidthSpinner.getModel().getValue();

			input1.setSignalWidth(newUpperInputWidth);
			input2.setSignalWidth(newLowerInputWidth);
			output.setSignalWidth(newUpperInputWidth + newLowerInputWidth);
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);

		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}

}
