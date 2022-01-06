package gates;

import signals.GateSignalPort;
import signals.SignalComponent.Direction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.function.BooleanSupplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class LogicGate implements Serializable {

	static final Dimension DEFAULT_SIZE = new Dimension(100, 100);

	private Point location;
	private Dimension size;

	private boolean deleted;

	public LogicGate() {
		this.location = new Point(-Integer.MAX_VALUE, -Integer.MAX_VALUE);
		this.size = new Dimension(DEFAULT_SIZE.width, (int) (DEFAULT_SIZE.height / getAspectRatio()));
		this.deleted = false;
	}

	public Point getLocation() {
		return new Point(location);
	}

	public void setLocation(Point newPosition) {
		Point delta = new Point(newPosition.x - location.x, newPosition.y - location.y);
		for (GateSignalPort gsp : exposeSignalPorts()) {
			Point newLocForPort = new Point(gsp.getLocation().x + delta.x, gsp.getLocation().y + delta.y);
			gsp.setLocation(newLocForPort);
		}
		this.location = newPosition;
	}

	public Dimension getSize() {
		return new Dimension(size);
	}

	void setSize(Dimension newSize) {
		this.size = new Dimension(newSize);
	}

	public boolean isDeleted() {
		return deleted;
	}

	abstract public BufferedImage getImage();

	abstract public GateSignalPort[] exposeSignalPorts();

	abstract public double getAspectRatio();

	abstract public void update();

	abstract public JDialog getEditDialog(JFrame owner);

	JComponent createEditDialogLowerBar(JDialog dialog, BooleanSupplier onSave) {
		JPanel saveCancelDeleteLowerPanel = new JPanel();
		saveCancelDeleteLowerPanel.setLayout(new BoxLayout(saveCancelDeleteLowerPanel, BoxLayout.X_AXIS));

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener((event) -> {
			if (onSave.getAsBoolean()) {
				dialog.dispose();
			}
		});
		saveCancelDeleteLowerPanel.add(saveButton);

		saveCancelDeleteLowerPanel.add(Box.createHorizontalGlue());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((event) -> {
			dialog.dispose();
		});
		saveCancelDeleteLowerPanel.add(cancelButton);

		saveCancelDeleteLowerPanel.add(Box.createHorizontalGlue());

		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener((event) -> {
			deleted = true;
			dialog.dispose();
		});
		saveCancelDeleteLowerPanel.add(deleteButton);

		return saveCancelDeleteLowerPanel;
	}

	void drawSignalPortLeads(Graphics2D g2d) {
		for (GateSignalPort gsp : exposeSignalPorts()) {
			Point relativePosition = new Point(gsp.getLocation().x - location.x, gsp.getLocation().y - location.y);

			g2d.setColor(gsp.isMonoAndHigh() ? Color.RED : Color.BLACK);

			if (gsp.getDirection() == Direction.INPUT) {
				g2d.drawLine(relativePosition.x, relativePosition.y, relativePosition.x + 5, relativePosition.y);
			} else if (gsp.getDirection() == Direction.OUTPUT) {
				g2d.drawLine(relativePosition.x - 5, relativePosition.y, relativePosition.x, relativePosition.y);
			} else {
				throw new EnumConstantNotPresentException(Direction.class, gsp.getDirection().toString());
			}

			if (gsp.getSignalWidth() > 1) {
				g2d.setColor(Color.MAGENTA);
				g2d.drawString(gsp.getSignalWidth() + "", relativePosition.x - 5, relativePosition.y - 2);
			}
		}
	}

	BufferedImage createBaseImage() {
		return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
	}

	static Polygon createPoly(Point... points) {
		Polygon poly = new Polygon();
		for (Point p : points) {
			poly.addPoint(p.x, p.y);
		}
		return poly;
	}
}
