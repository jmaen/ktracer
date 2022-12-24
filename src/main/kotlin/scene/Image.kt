package scene

import models.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataFormatImpl
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.FileImageOutputStream


class Image(
    private val image: Array<Array<Color>>, private val summary: Map<String, String> = mapOf()) {
    fun gammaCorrect(gamma: Double = 2.2) {
        for(i in image.indices) {
            for(j in image[0].indices) {
                image[i][j] = image[i][j].gammaCorrect(gamma)
            }
        }
    }

    fun save(file: File) {
        // set image data
        val buffer = BufferedImage(image.size, image[0].size, BufferedImage.TYPE_INT_RGB)
        for(i in 0 until buffer.width) {
            for(j in 0 until buffer.height) {
                // image origin is bottom-left, buffer origin is top-left
                buffer.setRGB(i, buffer.height - j - 1, image[i][j].toRGB())
            }
        }

        // set metadata
        val writer = ImageIO.getImageWritersByFormatName("png").next()
        val imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)
        val param = writer.defaultWriteParam
        val metadata = writer.getDefaultImageMetadata(imageType, param)

        val textEntry = IIOMetadataNode("TextEntry")
        textEntry.setAttribute("keyword", "Description")
        textEntry.setAttribute("value", summary.toString())

        val text = IIOMetadataNode("Text")
        text.appendChild(textEntry)

        val root = IIOMetadataNode(IIOMetadataFormatImpl.standardMetadataFormatName)
        root.appendChild(text)
        metadata.mergeTree(IIOMetadataFormatImpl.standardMetadataFormatName, root)

        val img = IIOImage(buffer, null, metadata)

        // write image
        val outputStream = FileImageOutputStream(file)
        writer.output = outputStream
        writer.write(img)
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