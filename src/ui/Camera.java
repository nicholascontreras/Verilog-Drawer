package ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class Camera {

	private static Camera camera;

	private static final double ZOOM_SPEED = 0.1;
	private static final double MIN_ZOOM = 0.2, MAX_ZOOM = 1;

	private int x = 0, y = 0;
	private double zoom = 1;

	public static Camera getInstance() {
		if (camera == null) {
			camera = new Camera();
		}
		return camera;
	}

	public void pan(int deltaX, int deltaY) {
		x += deltaX * (1 / zoom);
		y += deltaY * (1 / zoom);
	}

	public void zoom(double amount) {
		zoom += amount * ZOOM_SPEED;

		if (zoom < MIN_ZOOM) {
			zoom = MIN_ZOOM;
		} else if (zoom > MAX_ZOOM) {
			zoom = MAX_ZOOM;
		}
	}

	public AffineTransform getTransform(Dimension panelSize) {
		AffineTransform transform = new AffineTransform();

		transform.translate(panelSize.width / 2, panelSize.height / 2);
		transform.scale(zoom, zoom);
		transform.translate(x, y);

		return transform;
	}

	public Point transformPoint(Point point, Dimension panelSize) {
		int transformedX = (int) (((point.x - (panelSize.width / 2)) / zoom) - x);
		int transformedY = (int) (((point.y - (panelSize.height / 2)) / zoom) - y);
		return new Point(transformedX, transformedY);
	}
}
