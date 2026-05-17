package pt.isep.desofs.vendnet.infrastructure.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExifStripperTest {

	private ExifStripper exifStripper;

	@BeforeEach
	void setUp() {
		exifStripper = new ExifStripper();
	}

	@Test
	void stripExif_png_shouldReturnStrippedImage() throws IOException {
		BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, 10, 10);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		byte[] original = baos.toByteArray();

		byte[] result = exifStripper.stripExif(original, "png");
		assertNotNull(result);
		assertTrue(result.length > 0);
	}

	@Test
	void stripExif_jpeg_shouldReturnStrippedImage() throws IOException {
		BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, 10, 10);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpeg", baos);
		byte[] original = baos.toByteArray();

		byte[] result = exifStripper.stripExif(original, "jpeg");
		assertNotNull(result);
		assertTrue(result.length > 0);
	}

	@Test
	void stripExif_invalidImage_shouldReturnOriginal() {
		byte[] invalid = "not an image".getBytes();
		byte[] result = exifStripper.stripExif(invalid, "png");
		assertArrayEquals(invalid, result);
	}
}