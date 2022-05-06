package image

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun saveImage(image: Array<Array<Color>>, fileName: String) {
    val buffer = BufferedImage(image.size, image[0].size, BufferedImage.TYPE_INT_RGB)
    for(i in 0 until buffer.width) {
        for(j in 0 until buffer.height) {
            // image origin is bottom-left, buffer origin is top-left
            val color = image[i][j]
            buffer.setRGB(i, buffer.height - j - 1, color.toRGB())
        }
    }

    val file = File(fileName)
    ImageIO.write(buffer, "png", file)
}