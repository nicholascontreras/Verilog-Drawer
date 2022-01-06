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

public class SplitGate extends LogicGate {

	private final GateSignalPort input, output1, output2;
	private int output1Start, output2Start;

	public SplitGate() {
		input = new GateSignalPort(this, new Point(10, 50), Direction.INPUT);

		output1 = new GateSignalPort(this, new Point(90, 45), Direction.OUTPUT);
		output2 = new GateSignalPort(this, new Point(90, 70), Direction.OUTPUT);

		output1Start = 0;
		output2Start = 0;
	}

	private static void drawGateBody(Graphics2D g2d) {
		g2d.drawRoundRect(15, 15, 70, 70, 5, 5);
		int textWidth = g2d.getFontMetrics().stringWidth("SPLIT");
		g2d.drawString("SPLIT", 50 - textWidth / 2, 30);
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

		g2d.drawLine(5, 50, 15, 50);
		g2d.drawLine(85, 45, 95, 45);
		g2d.drawLine(85, 70, 95, 70);

		drawGateBody(g2d);

		g2d.dispose();
		return image;
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		return new GateSignalPort[] { input, output1, output2 };
	}

	@Override
	public double getAspectRatio() {
		return 1;
	}

	@Override
	public void update() {
		boolean[] output1Signal = new boolean[output1.getSignalWidth()];
		boolean[] output2Signal = new boolean[output2.getSignalWidth()];

		for (int i = 0; i < input.getSignalWidth(); i++) {
			if (i >= output1Start && i < output1Start + output1.getSignalWidth()) {
				output1Signal[i - output1Start] = input.getSignal()[i];
			}

			if (i >= output2Start && i < output2Start + output2.getSignalWidth()) {
				output2Signal[i - output2Start] = input.getSignal()[i];
			}
		}

		output1.setSignal(output1Signal);
		output2.setSignal(output2Signal);
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit Split", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperDataWidthPanel = new JPanel();
		upperDataWidthPanel.setLayout(new BoxLayout(upperDataWidthPanel, BoxLayout.X_AXIS));
		upperDataWidthPanel.add(new JLabel("Signal Width: "));
		JSpinner dataWidthField = new JSpinner(new SpinnerNumberModel(input.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		upperDataWidthPanel.add(dataWidthField);

		outerPanel.add(upperDataWidthPanel, BorderLayout.NORTH);

		JPanel centerDataWidthPanel = new JPanel();
		centerDataWidthPanel.setLayout(new BoxLayout(centerDataWidthPanel, BoxLayout.Y_AXIS));

		centerDataWidthPanel.add(new JLabel("Upper Output Signal Range: "));

		JPanel upperOutputDataWidthPanel = new JPanel();
		upperOutputDataWidthPanel.setLayout(new BoxLayout(upperOutputDataWidthPanel, BoxLayout.X_AXIS));
		JSpinner upperOutputDataWidthLowerBoundSpinner = new JSpinner(
				new SpinnerNumberModel(output1Start, 0, input.getSignalWidth() - 1, 1));
		upperOutputDataWidthPanel.add(upperOutputDataWidthLowerBoundSpinner, BorderLayout.WEST);
		upperOutputDataWidthPanel.add(new JLabel(" to "));
		JSpinner upperOutputDataWidthUpperBoundSpinner = new JSpinner(
				new SpinnerNumberModel(output1Start + output1.getSignalWidth() - 1, 0, input.getSignalWidth() - 1, 1));
		upperOutputDataWidthPanel.add(upperOutputDataWidthUpperBoundSpinner, BorderLayout.EAST);

		centerDataWidthPanel.add(upperOutputDataWidthPanel);

		centerDataWidthPanel.add(Box.createVerticalStrut(5));
		centerDataWidthPanel.add(new JLabel("Lower Output Signal Range: "));

		JPanel lowerOutputDataWidthPanel = new JPanel();
		lowerOutputDataWidthPanel.setLayout(new BoxLayout(lowerOutputDataWidthPanel, BoxLayout.X_AXIS));
		JSpinner lowerOutputDataWidthLowerBoundSpinner = new JSpinner(
				new SpinnerNumberModel(output2Start, 0, input.getSignalWidth() - 1, 1));
		lowerOutputDataWidthPanel.add(lowerOutputDataWidthLowerBoundSpinner, BorderLayout.WEST);
		lowerOutputDataWidthPanel.add(new JLabel(" to "));
		JSpinner lowerOutputDataWidthUpperBoundSpinner = new JSpinner(
				new SpinnerNumberModel(output2Start + output2.getSignalWidth() - 1, 0, input.getSignalWidth() - 1, 1));
		lowerOutputDataWidthPanel.add(lowerOutputDataWidthUpperBoundSpinner, BorderLayout.EAST);

		centerDataWidthPanel.add(lowerOutputDataWidthPanel);

		dataWidthField.getModel().addChangeListener((event) -> {
			int newInputDataWidth = (int) dataWidthField.getModel().getValue();

			// Cap max value of lower bound for upper output port
			((SpinnerNumberModel) upperOutputDataWidthLowerBoundSpinner.getModel()).setMaximum(newInputDataWidth - 1);
			if ((int) upperOutputDataWidthLowerBoundSpinner.getModel().getValue() > newInputDataWidth - 1) {
				upperOutputDataWidthLowerBoundSpinner.getModel().setValue(newInputDataWidth - 1);
			}

			// Cap max value of upper bound for upper output port
			((SpinnerNumberModel) upperOutputDataWidthUpperBoundSpinner.getModel()).setMaximum(newInputDataWidth - 1);
			if ((int) upperOutputDataWidthUpperBoundSpinner.getModel().getValue() > newInputDataWidth - 1) {
				upperOutputDataWidthUpperBoundSpinner.getModel().setValue(newInputDataWidth - 1);
			}

			// Cap max value of lower bound for lower output port
			((SpinnerNumberModel) lowerOutputDataWidthLowerBoundSpinner.getModel()).setMaximum(newInputDataWidth - 1);
			if ((int) lowerOutputDataWidthLowerBoundSpinner.getModel().getValue() > newInputDataWidth - 1) {
				lowerOutputDataWidthLowerBoundSpinner.getModel().setValue(newInputDataWidth - 1);
			}

			// Cap max value of upper bound for lower output port
			((SpinnerNumberModel) lowerOutputDataWidthUpperBoundSpinner.getModel()).setMaximum(newInputDataWidth - 1);
			if ((int) lowerOutputDataWidthUpperBoundSpinner.getModel().getValue() > newInputDataWidth - 1) {
				lowerOutputDataWidthUpperBoundSpinner.getModel().setValue(newInputDataWidth - 1);
			}
		});

		outerPanel.add(centerDataWidthPanel, BorderLayout.CENTER);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			int newSignalWidth = (int) dataWidthField.getModel().getValue();

			int newUpperOutputLowerBound = (int) upperOutputDataWidthLowerBoundSpinner.getModel().getValue();
			int newUpperOutputUpperBound = (int) upperOutputDataWidthUpperBoundSpinner.getModel().getValue();
			int newLowerOutputLowerBound = (int) lowerOutputDataWidthLowerBoundSpinner.getModel().getValue();
			int newLowerOutputUpperBound = (int) lowerOutputDataWidthUpperBoundSpinner.getModel().getValue();

			// Check to make sure ranges aren't inverted (min value of ranger greater than
			// max value)
			if (newUpperOutputLowerBound > newUpperOutputUpperBound) {
				JOptionPane.showMessageDialog(dialog, "Range limits on upper output port are inverted!",
						"Unable to Save", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (newLowerOutputLowerBound > newLowerOutputUpperBound) {
				JOptionPane.showMessageDialog(dialog, "Range limits on lower output port are inverted!",
						"Unable to Save", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			input.setSignalWidth(newSignalWidth);

			output1.setSignalWidth((newUpperOutputUpperBound - newUpperOutputLowerBound) + 1);
			output2.setSignalWidth((newLowerOutputUpperBound - newLowerOutputLowerBound) + 1);

			output1Start = newUpperOutputLowerBound;
			output2Start = newLowerOutputLowerBound;
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);

		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}

}
