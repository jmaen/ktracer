package scene

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

import models.Color

class Image(private val image: Array<Array<Color>>) {
    fun gammaCorrect(gamma: Double = 2.2) {
        for(i in image.indices) {
            for(j in image[0].indices) {
                image[i][j] = image[i][j].gammaCorrect(gamma)
            }
        }
    }

    fun save(path: String) {
        val buffer = BufferedImage(image.size, image[0].size, BufferedImage.TYPE_INT_RGB)
        for(i in 0 until buffer.width) {
            for(j in 0 until buffer.height) {
                // image origin is bottom-left, buffer origin is top-left
                buffer.setRGB(i, buffer.height - j - 1, image[i][j].toRGB())
            }
        }

        val file = File(path)
        ImageIO.write(buffer, "png", file)
    }

    companion object {
        fun load(path: String): Image {
            val file = File(path)
            val buffer = ImageIO.read(file)
            val image: Array<Array<Color>> = Array(buffer.width) { Array(buffer.height) { Color.BLACK } }
            for(i in 0 until buffer.width) {
                for(j in 0 until buffer.height) {
                    // image origin is bottom-left, buffer origin is top-left
                    image[i][buffer.height - j - 1] = Color.fromRGB(buffer.getRGB(i, j))
                }
            }
            return Image(image)
        }
    }
}