package ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class GateImageManager {

	private static final int IMAGE_WIDTH = 100;

	private static final HashMap<String, BufferedImage> IMAGES = new HashMap<String, BufferedImage>();

	public static BufferedImage getImage(String imgName) {
		if (!IMAGES.containsKey(imgName)) {
			loadImage(imgName);
		}

		return IMAGES.get(imgName);
	}

	private static void loadImage(String imgName) {
		InputStream is = GateImageManager.class.getResourceAsStream("/" + imgName + ".png");
		try {
			BufferedImage image = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage resizeImage(BufferedImage img, int newWidth) {
		double scale = (double) newWidth / img.getWidth();
		int newHeight = (int) (img.getWidth() * scale);
		BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, img.getType());
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.scale(scale, scale);
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return scaledImage;
	}
}
