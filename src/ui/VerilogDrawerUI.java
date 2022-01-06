package ui;

import gates.AndGate;
import gates.ComboGate;
import gates.ConcatGate;
import gates.Input;
import gates.LogicGate;
import gates.NandGate;
import gates.NorGate;
import gates.NotGate;
import gates.OrGate;
import gates.Output;
import gates.SplitGate;
import gates.XorGate;
import main.GateLayout;
import signals.SignalComponent;
import signals.Wire;
import ui.mouseAction.*;
import ui.mouseAction.MouseAction.MouseActionType;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class VerilogDrawerUI {

	private GateLayout layout;
	private MouseAction mouseAction;
	private Point mouseLocationOnPanel, mouseLocationOnPlane;

	private final JFrame frame;

	public VerilogDrawerUI(GateLayout layout) {

		attemptSystemLookAndFeel();

		this.layout = layout;

		mouseAction = new IdleAction();

		mouseLocationOnPanel = new Point();
		mouseLocationOnPlane = new Point();

		frame = new JFrame("Verilog Drawer");

		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.add(new DrawingPanel(), BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

		JButton createWireButton = new JButton("Create New Wire");
		createWireButton.addActionListener((event) -> {
			mouseAction.cancel();
			mouseAction = new DrawWireAction();
		});
		rightPanel.add(createWireButton, BorderLayout.NORTH);

		JPanel gateOptionsOuterPanel = new JPanel(new BorderLayout());

		JPanel gateOptionsPanel = new JPanel(new GridLayout(0, 2));

		gateOptionsPanel.add(createNewGateButton(OrGate.class, "OR"));
		gateOptionsPanel.add(createNewGateButton(AndGate.class, "AND"));
		gateOptionsPanel.add(createNewGateButton(NorGate.class, "NOR"));
		gateOptionsPanel.add(createNewGateButton(NandGate.class, "NAND"));
		gateOptionsPanel.add(createNewGateButton(XorGate.class, "XOR"));
		gateOptionsPanel.add(createNewGateButton(NotGate.class, "NOT"));
		gateOptionsPanel.add(createNewGateButton(Input.class, "INPUT"));
		gateOptionsPanel.add(createNewGateButton(Output.class, "OUTPUT"));
		gateOptionsPanel.add(createNewGateButton(SplitGate.class, "SPLIT"));
		gateOptionsPanel.add(createNewGateButton(ConcatGate.class, "CONCAT"));

		gateOptionsOuterPanel.add(gateOptionsPanel, BorderLayout.NORTH);

		JScrollPane gateOptionsScrollPane = new JScrollPane(gateOptionsOuterPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rightPanel.add(gateOptionsScrollPane, BorderLayout.CENTER);

		JPanel bottomButtonsPanel = new JPanel(new GridLayout(1, 0, 5, 5));

		JButton saveButton = new JButton("SAVE");
		saveButton.addActionListener((event) -> saveDesign());
		bottomButtonsPanel.add(saveButton);

		JButton loadButton = new JButton("LOAD");
		loadButton.addActionListener((event) -> loadDesign());
		bottomButtonsPanel.add(loadButton);

		JButton importButton = new JButton("IMPORT");
		importButton.addActionListener((event) -> importDesign());
		bottomButtonsPanel.add(importButton);

		rightPanel.add(bottomButtonsPanel, BorderLayout.SOUTH);

		outerPanel.add(rightPanel, BorderLayout.EAST);

		frame.add(outerPanel);
		frame.setPreferredSize(new Dimension(600, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				update();
			}
		}, 0, 100);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				frame.repaint();
			}
		}, 0, 20);
	}

	private JButton createNewGateButton(Class<? extends LogicGate> gateType, String buttonText) {
		try {
			// No static inheritance in Java, reflect onto a static method we know exists
			JButton newGateButton = new JButton(
					new ImageIcon((BufferedImage) gateType.getMethod("getPreviewImage").invoke(null)));
			newGateButton.setHorizontalTextPosition(JButton.CENTER);
			newGateButton.setVerticalTextPosition(JButton.BOTTOM);
			newGateButton.setText(buttonText);
			newGateButton.addActionListener((event) -> {
				mouseAction.cancel();
				try {
					// Reflect onto the nullary constructor we know is also there
					mouseAction = new CreateGateAction(gateType.getConstructor().newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return newGateButton;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void update() {
		layout.update();
	}

	private void saveDesign() {
		File userHome = new File(System.getProperty("user.home"));
		File saveDirectory = new File(userHome, "Drawn Circuits");

		if (!saveDirectory.exists()) {
			saveDirectory.mkdir();
		}

		JFileChooser saveLocationChooser = new JFileChooser(saveDirectory);
		saveLocationChooser.setSelectedFile(new File(saveDirectory, "untitled design.circuit"));
		saveLocationChooser.setFileFilter(new FileNameExtensionFilter("Drawn Circuit Files (.circuit)", "circuit"));
		int result = saveLocationChooser.showSaveDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File saveLocation = saveLocationChooser.getSelectedFile();
			if (!saveLocation.getName().endsWith(".circuit")) {
				saveLocation = new File(saveLocation.getParentFile(), saveLocation.getName() + ".circuit");
			}

			try {
				FileOutputStream fos = new FileOutputStream(saveLocation);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(layout);
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadDesign() {
		File userHome = new File(System.getProperty("user.home"));
		File saveDirectory = new File(userHome, "Drawn Circuits");

		if (!saveDirectory.exists()) {
			saveDirectory.mkdir();
		}

		JFileChooser loadLocationChooser = new JFileChooser(saveDirectory);
		loadLocationChooser.setFileFilter(new FileNameExtensionFilter("Drawn Circuit Files (.circuit)", "circuit"));
		int result = loadLocationChooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File loadLocation = loadLocationChooser.getSelectedFile();

			try {
				FileInputStream fis = new FileInputStream(loadLocation);
				ObjectInputStream ois = new ObjectInputStream(fis);
				layout = (GateLayout) ois.readObject();
				ois.close();
				fis.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void importDesign() {
		File userHome = new File(System.getProperty("user.home"));
		File saveDirectory = new File(userHome, "Drawn Circuits");

		if (!saveDirectory.exists()) {
			saveDirectory.mkdir();
		}

		JFileChooser loadLocationChooser = new JFileChooser(saveDirectory);
		loadLocationChooser.setFileFilter(new FileNameExtensionFilter("Drawn Circuit Files (.circuit)", "circuit"));
		int result = loadLocationChooser.showDialog(frame, "Import");

		if (result == JFileChooser.APPROVE_OPTION) {
			File loadLocation = loadLocationChooser.getSelectedFile();

			try {
				FileInputStream fis = new FileInputStream(loadLocation);
				ObjectInputStream ois = new ObjectInputStream(fis);
				GateLayout importedLayout = (GateLayout) ois.readObject();
				ois.close();
				fis.close();

				String friendlyName = loadLocation.getName();
				if (friendlyName.endsWith(".circuit")) {
					friendlyName = friendlyName.substring(0, friendlyName.length() - 8);
				}

				ComboGate comboGate = new ComboGate(friendlyName, importedLayout);

				mouseAction.cancel();
				mouseAction = new CreateGateAction(comboGate);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static void attemptSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private class DrawingPanel extends JPanel {

		private DrawingPanel() {
			MouseHandler mh = new MouseHandler();
			this.addMouseListener(mh);
			this.addMouseMotionListener(mh);
			this.addMouseWheelListener(mh);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.transform(Camera.getInstance().getTransform(getSize()));

			for (LogicGate curGate : layout.getLogicGates()) {
				g2d.drawImage(curGate.getImage(), curGate.getLocation().x, curGate.getLocation().y, null);
			}

			for (Wire curWire : layout.getWires()) {
				Point sourceLoc = curWire.getSource().getLocation();
				Point destLoc = curWire.getDestination().getLocation();

				Color wireColor = Color.BLACK;
				if (!curWire.getMatchingSignalWidth()) {
					wireColor = Color.CYAN;
				} else if (curWire.getSource().isMonoAndHigh()) {
					wireColor = Color.RED;
				}

				drawGeometricWire(g2d, sourceLoc, destLoc, wireColor);
			}

			drawMouseAction(g2d);
		}

		private void drawGeometricWire(Graphics2D g2d, Point from, Point to, Color color) {
			Point midpoint = new Point((from.x + to.x) / 2, (from.y + to.y) / 2);

			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(3));

			if (to.x >= from.x) {
				g2d.drawLine(from.x, from.y, midpoint.x, from.y);
				g2d.drawLine(midpoint.x, from.y, midpoint.x, to.y);
				g2d.drawLine(midpoint.x, to.y, to.x, to.y);
			} else {
				g2d.drawLine(from.x, from.y, from.x, midpoint.y);
				g2d.drawLine(from.x, midpoint.y, to.x, midpoint.y);
				g2d.drawLine(to.x, midpoint.y, to.x, to.y);
			}
		}

		private void drawDashedBoxAroundGate(Graphics2D g2d, LogicGate gate) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, new float[] { 5, 5 }, 0));
			g2d.drawRoundRect(gate.getLocation().x, gate.getLocation().y, gate.getSize().width, gate.getSize().height,
					5, 5);
		}

		private void drawMouseAction(Graphics2D g2d) {
			switch (mouseAction.getType()) {
			case IDLE -> {
			}
			case CREATE_GATE -> {
				CreateGateAction cga = (CreateGateAction) mouseAction;
				LogicGate gate = cga.getGate();
				g2d.drawImage(gate.getImage(), gate.getLocation().x, gate.getLocation().y, null);
				drawDashedBoxAroundGate(g2d, gate);
			}
			case MOVE_GATE -> {
				MoveGateAction mga = (MoveGateAction) mouseAction;
				drawDashedBoxAroundGate(g2d, mga.getGate());
			}
			case DRAW_WIRE -> {
				DrawWireAction dwa = (DrawWireAction) mouseAction;

				SignalComponent from = dwa.getSignalComponentFrom();
				if (from == null) {
					// Highlight available output ports
					for (SignalComponent curSignalComponent : layout.getAvailableSignalComponents()) {
						if (curSignalComponent.getDirection() == SignalComponent.Direction.OUTPUT) {
							g2d.setColor(Color.GREEN);
							g2d.fillOval(curSignalComponent.getLocation().x - 5, curSignalComponent.getLocation().y - 5,
									10, 10);
						}
					}
				} else {
					// Highlight available input ports
					for (SignalComponent curSignalComponent : layout.getAvailableSignalComponents()) {
						if (curSignalComponent.getDirection() == SignalComponent.Direction.INPUT) {
							g2d.setColor(Color.GREEN);
							g2d.fillOval(curSignalComponent.getLocation().x - 5, curSignalComponent.getLocation().y - 5,
									10, 10);
						}
					}

					// Draw the wire form the selected output port to the mouse pointer
					Point fromLoc = from.getLocation();
					drawGeometricWire(g2d, fromLoc, mouseLocationOnPlane, Color.BLACK);
				}
			}
			default -> {
				throw new UnsupportedOperationException(
						"No drawMouseAction handler for action type: " + mouseAction.getType());
			}
			}
		}

		private class MouseHandler extends MouseAdapter {

			private void updateMouseLocation(Point mouseLoc) {
				mouseLocationOnPanel = mouseLoc;
				mouseLocationOnPlane = Camera.getInstance().transformPoint(mouseLoc, getSize());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				updateMouseLocation(e.getPoint());

				switch (mouseAction.getType()) {
				case IDLE -> {
					for (LogicGate curGate : layout.getLogicGates()) {
						if (curGate.getLocation().x < mouseLocationOnPlane.x
								&& curGate.getLocation().y < mouseLocationOnPlane.y) {
							if (curGate.getLocation().x + curGate.getSize().width > mouseLocationOnPlane.x
									&& curGate.getLocation().y + curGate.getSize().height > mouseLocationOnPlane.y) {
								if (e.getButton() == MouseEvent.BUTTON1) {
									mouseAction = new MoveGateAction(curGate, mouseLocationOnPlane);
								} else if (e.getButton() == MouseEvent.BUTTON3) {
									JDialog dialog = curGate.getEditDialog(frame);
									dialog.setLocation(mouseLocationOnPanel.x, mouseLocationOnPanel.y);
									dialog.setVisible(true);
								}
								break;
							}
						}
					}

				}
				case CREATE_GATE -> {
					CreateGateAction cga = (CreateGateAction) mouseAction;
					layout.addLogicGate(cga.getGate());
					mouseAction = new IdleAction();
				}
				case MOVE_GATE -> {
					mouseAction = new IdleAction();
				}
				case DRAW_WIRE -> {
					DrawWireAction dwa = (DrawWireAction) mouseAction;

					if (dwa.getSignalComponentFrom() == null) {
						SignalComponent from = layout.getSignalAvailableComponentAt(mouseLocationOnPlane);
						if (from != null) {
							dwa.setSignalComponentFrom(from);
						}
					} else {
						SignalComponent to = layout.getSignalAvailableComponentAt(mouseLocationOnPlane);
						if (to != null) {
							Wire newWire = new Wire(dwa.getSignalComponentFrom(), to);
							layout.addWire(newWire);
							mouseAction = new IdleAction();
						}
					}
				}
				default -> {
					throw new UnsupportedOperationException(
							"No mouseClicked handler for action type: " + mouseAction.getType());
				}
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				updateMouseLocation(e.getPoint());

				switch (mouseAction.getType()) {
				case IDLE -> {
				}
				case CREATE_GATE -> {
					CreateGateAction cga = (CreateGateAction) mouseAction;
					LogicGate gate = cga.getGate();
					gate.setLocation(new Point(mouseLocationOnPlane.x - gate.getSize().width / 2,
							mouseLocationOnPlane.y - gate.getSize().height / 2));
				}
				case MOVE_GATE -> {
					MoveGateAction mga = (MoveGateAction) mouseAction;
					LogicGate selected = mga.getGate();

					int dx = mouseLocationOnPlane.x - mga.getInitialMouseLocation().x;
					int dy = mouseLocationOnPlane.y - mga.getInitialMouseLocation().y;

					selected.setLocation(
							new Point(mga.getInitialGateLocation().x + dx, mga.getInitialGateLocation().y + dy));
				}
				case DRAW_WIRE -> {
				}
				default -> {
					throw new UnsupportedOperationException(
							"No mouseMoved handler for action type: " + mouseAction.getType());
				}
				}
			}

			public void mouseDragged(MouseEvent e) {
				Point newMouseLocationOnPanel = e.getPoint();

				if (SwingUtilities.isRightMouseButton(e)) {
					int deltaX = newMouseLocationOnPanel.x - mouseLocationOnPanel.x;
					int deltaY = newMouseLocationOnPanel.y - mouseLocationOnPanel.y;
					Camera.getInstance().pan(deltaX, deltaY);
				}

				updateMouseLocation(e.getPoint());
			};

			public void mouseWheelMoved(MouseWheelEvent e) {
				double rotation = -e.getPreciseWheelRotation();
				Camera.getInstance().zoom(rotation);
			};
		}
	}
}
