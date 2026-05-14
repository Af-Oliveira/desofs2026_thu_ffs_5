package pt.isep.desofs.vendnet.infrastructure.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class ExifStripper {

    public byte[] stripExif(byte[] imageData, String formatName) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) {
                log.warn("Could not read image for EXIF stripping, returning original");
                return imageData;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, baos);
            log.debug("EXIF metadata stripped from image");
            return baos.toByteArray();
        } catch (IOException e) {
            log.warn("EXIF stripping failed, returning original", e);
            return imageData;
        }
    }
}
