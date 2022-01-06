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

public class Input extends LogicGate {

	private String name;

	private final GateSignalPort output;

	public Input() {
		this.name = "ABC";
		output = new GateSignalPort(this, new Point(90, 20), SignalComponent.Direction.OUTPUT);
	}

	@Override
	public BufferedImage getImage() {
		BufferedImage image = createBaseImage();
		Graphics2D g2d = image.createGraphics();
		g2d.setStroke(new BasicStroke(3));

		drawSignalPortLeads(g2d);

		g2d.setColor(Color.BLACK);

		g2d.drawLine(10, 10, 75, 10);
		g2d.drawLine(10, 10, 10, 30);
		g2d.drawLine(10, 30, 75, 30);

		g2d.drawLine(75, 10, 85, 20);
		g2d.drawLine(75, 30, 85, 20);

		FontMetrics fm = g2d.getFontMetrics();
		int nameWidth = fm.stringWidth(name);
		g2d.drawString(name, (image.getWidth() / 2) - (nameWidth / 2) - 5, (image.getHeight() / 2) + (fm.getDescent()));
		g2d.dispose();

		return image;
	}

	public static BufferedImage getPreviewImage() {
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));

		g2d.translate(0, 30);

		g2d.drawLine(85, 20, 90, 20);

		g2d.drawLine(10, 10, 75, 10);
		g2d.drawLine(10, 10, 10, 30);
		g2d.drawLine(10, 30, 75, 30);

		g2d.drawLine(75, 10, 85, 20);
		g2d.drawLine(75, 30, 85, 20);

		g2d.translate(0, -30);

		FontMetrics fm = g2d.getFontMetrics();
		int nameWidth = fm.stringWidth("ABC");
		g2d.drawString("ABC", (image.getWidth() / 2) - (nameWidth / 2) - 5,
				(image.getHeight() / 2) + (fm.getDescent()));
		g2d.dispose();

		return image;
	}

	@Override
	public GateSignalPort[] exposeSignalPorts() {
		return new GateSignalPort[] { output };
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
		JDialog dialog = new JDialog(owner, "Edit Input", true);
		JPanel outerPanel = new JPanel(new BorderLayout(5, 5));
		outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel upperConfigurationPanel = new JPanel();
		upperConfigurationPanel.setLayout(new BoxLayout(upperConfigurationPanel, BoxLayout.Y_AXIS));

		JPanel upperNamePanel = new JPanel();
		upperNamePanel.setLayout(new BoxLayout(upperNamePanel, BoxLayout.X_AXIS));
		upperNamePanel.add(new JLabel("Input Name: "));
		JTextField nameField = new JTextField(name);
		nameField.setHorizontalAlignment(JTextField.CENTER);
		upperNamePanel.add(nameField);

		upperConfigurationPanel.add(upperNamePanel);
		upperConfigurationPanel.add(Box.createVerticalStrut(5));

		JPanel upperDataWidthPanel = new JPanel();
		upperDataWidthPanel.setLayout(new BoxLayout(upperDataWidthPanel, BoxLayout.X_AXIS));
		upperDataWidthPanel.add(new JLabel("Input Signal Width: "));
		JSpinner dataWidthField = new JSpinner(
				new SpinnerNumberModel(output.getSignalWidth(), 1, Integer.MAX_VALUE, 1));
		upperDataWidthPanel.add(dataWidthField);

		upperConfigurationPanel.add(upperDataWidthPanel);

		outerPanel.add(upperConfigurationPanel, BorderLayout.NORTH);

		ArrayList<JPanel> labelAndInputPanels = new ArrayList<JPanel>();
		ArrayList<JComboBox<String>> booleanSelectors = new ArrayList<JComboBox<String>>();

		JPanel compactingOuterInputsPanel = new JPanel(new BorderLayout());

		JPanel inputsPanel = new JPanel();
		inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));

		// Adjust the number of input drop-downs when the data width spinner changes
		ChangeListener dataWidthChangeListener = (event) -> {
			int newSignalWidth = (Integer) dataWidthField.getModel().getValue();

			// Add additional drop-downs if needed
			for (int i = labelAndInputPanels.size(); i < newSignalWidth; i++) {

				JPanel labelAndInputPanel = new JPanel();
				labelAndInputPanel.setLayout(new BoxLayout(labelAndInputPanel, BoxLayout.X_AXIS));
				labelAndInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
				
				JLabel booleanSelectorLabel = new JLabel("Input " + i + ": ");
				labelAndInputPanel.add(booleanSelectorLabel);

				JComboBox<String> booleanSelector = new JComboBox<String>(new String[] { "LOW", "HIGH" });
				booleanSelector.setSelectedItem(i < output.getSignalWidth() && output.getSignal()[i] ? "HIGH" : "LOW");
				labelAndInputPanel.add(booleanSelector);
				booleanSelectors.add(booleanSelector);

				inputsPanel.add(labelAndInputPanel);
				labelAndInputPanels.add(labelAndInputPanel);
			}

			// Remove extra drop-downs if needed
			while (labelAndInputPanels.size() > newSignalWidth) {
				booleanSelectors.remove(booleanSelectors.size() - 1);
				inputsPanel.remove(labelAndInputPanels.remove(labelAndInputPanels.size() - 1));
			}

			dialog.revalidate();
		};

		// Trigger and add the change listener
		dataWidthChangeListener.stateChanged(null);
		dataWidthField.getModel().addChangeListener(dataWidthChangeListener);

		compactingOuterInputsPanel.add(inputsPanel, BorderLayout.NORTH);

		JScrollPane inputsPanelScrollPane = new JScrollPane(compactingOuterInputsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		inputsPanelScrollPane.setPreferredSize(new Dimension(1, 200));
		outerPanel.add(inputsPanelScrollPane, BorderLayout.CENTER);

		outerPanel.add(createEditDialogLowerBar(dialog, () -> {
			// Update the name
			name = nameField.getText();

			// Update the output signal width
			output.setSignalWidth(booleanSelectors.size());

			// Update the output signal
			boolean[] newOutputSignal = new boolean[booleanSelectors.size()];
			for (int i = 0; i < booleanSelectors.size(); i++) {
				JComboBox<String> curBooleanSelector = booleanSelectors.get(i);
				String selected = (String) curBooleanSelector.getModel().getSelectedItem();
				newOutputSignal[i] = selected.equals("HIGH");
			}
			output.setSignal(newOutputSignal);
			return true;
		}), BorderLayout.SOUTH);

		dialog.add(outerPanel);
		dialog.pack();

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		return dialog;
	}
}
