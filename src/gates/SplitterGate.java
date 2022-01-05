package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JFrame;

import signals.GateSignalPort;
import signals.SignalComponent.Direction;

public class SplitterGate extends LogicGate {

	private final GateSignalPort input, output1, output2;

	public SplitterGate(Point location) {
		super(location);

		input = new GateSignalPort(this, new Point(10, 50), Direction.INPUT);

		output1 = new GateSignalPort(this, new Point(90, 45), Direction.OUTPUT);
		output2 = new GateSignalPort(this, new Point(90, 70), Direction.OUTPUT);
	}

	private static void drawGateBody(Graphics2D g2d) {
		g2d.drawRoundRect(15, 15, 70, 70, 5, 5);
		int textWidth = g2d.getFontMetrics().stringWidth("SPLITTER");
		g2d.drawString("SPLITTER", 50 - textWidth / 2, 30);
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
		
	}

	@Override
	public JDialog getEditDialog(JFrame owner) {
		// TODO Auto-generated method stub
		return null;
	}

}
