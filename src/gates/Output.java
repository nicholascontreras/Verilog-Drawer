package gates;

import signals.GateSignalPort;
import signals.SignalComponent;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public class Output extends LogicGate {

	private String name;

	private final GateSignalPort input;

	public Output() {
		this.name = "XYZ";
		input = new GateSignalPort(this, new Point(10, 20), SignalComponent.Direction.INPUT);
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = createBaseImage();
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));

		drawSignalPortLeads(g2d);

		g2d.setColor(Color.BLACK);

		drawGateBody(g2d);

		FontMetrics fm = g2d.getFontMetrics();
		int nameWidth = fm.stringWidth(name);
		g2d.drawString(name, (image.getWidth() / 2) - (nameWidth / 2) + 5, (image.getHeight() / 2) + (fm.getDescent()));
		g2d.dispose();

		return image;
	}

	public static BufferedImage getPreviewImage() {
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));

		g2d.translate(0, 30);
		g2d.drawLine(10, 20, 20, 20);
		drawGateBody(g2d);
		g2d.translate(0, -30);

		FontMetrics fm = g2d.getFontMetrics();
		int nameWidth = fm.stringWidth("XYZ");
		g2d.drawString("XYZ", (image.getWidth() / 2) - (nameWidth / 2) + 5,
				(image.getHeight() / 2) + (fm.getDescent()));
		g2d.dispose();

		return image;
	}

	private static void drawGateBody(Graphics2D g2d) {
		g2d.drawLine(10, 10, 90, 10);
		g2d.drawLine(90, 10, 90, 30);
		g2d.drawLine(10, 30, 90, 30);

		g2d.drawLine(10, 10, 20, 20);
		g2d.drawLine(10, 30, 20, 20);
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		return new GateSignalPort[] { input };
	}

	@Override
	public void update() {
	}

	@Override
	public double getAspectRatio() {
		return 10.0 / 4;
	}

	public String getName() {
		return name;
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Edit Output", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperConfigurationPanel = new JPanel();
		upperConfigurationPanel.setLayout(new BoxLayout(upperConfigurationPanel, BoxLayout.Y_AXIS));

		JPanel upperNamePanel = new JPanel();
		upperNamePanel.setLayout(new BoxLayout(upperNamePanel, BoxLayout.X_AXIS));
		upperNamePanel.add(new JLabel("Output Name: "));
		JTextField nameField = new JTextField(name);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		upperNamePanel.add(nameField);

		upperConfigurationPanel.add(upperNamePanel);

		JPanel upperDataWidthPanel = new JPanel();
		upperDataWidthPanel.setLayout(new BoxLayout(upperDataWidthPanel, BoxLayout.X_AXIS));
		upperDataWidthPanel.add(new JLabel("Output Signal Width: "));
		JSpinner dataWidthField = new JSpinner(new SpinnerNumberModel(input.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		upperDataWidthPanel.add(dataWidthField);

		upperConfigurationPanel.add(upperDataWidthPanel);

		outerPanel.add(upperConfigurationPanel, BorderLayout.NORTH);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			// Update the name
			name = nameField.getText();

			// Update the signal width
			input.setSignalWidth((int) dataWidthField.getModel().getValue());
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);
		dialog.pack();

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}
}
