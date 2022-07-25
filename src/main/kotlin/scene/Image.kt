package scene

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

import models.Color

class Image(private val image: Array<Array<Color>>) {
    fun save(path: String) {
        val buffer = BufferedImage(image.size, image[0].size, BufferedImage.TYPE_INT_RGB)
        for(i in 0 until buffer.width) {
            for(j in 0 until buffer.height) {
                val color = image[i][j].gammaCorrect(2.2)
                // image origin is bottom-left, buffer origin is top-left
                buffer.setRGB(i, buffer.height - j - 1, color.toRGB())
            }
        }

        val file = File(path)
        ImageIO.write(buffer, "png", file)
    }
}