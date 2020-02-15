package stage2

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Image {
    private val image: BufferedImage

    val width: Int
        get() = image.width

    val height: Int
        get() = image.height

    constructor(width: Int, height: Int, init: (Int, Int) -> Color) {
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                set(x, y, init(x, y))
            }
        }
    }

    constructor(filename: String) {
        val file = File(filename)
        image = ImageIO.read(file)
    }

    operator fun get(x: Int, y: Int): Color {
        if (x < 0 || x >= image.width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${image.width - 1}")
        }
        if (y < 0 || y >= image.height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${image.height - 1}")
        }
        return Color(image.getRGB(x, y))
    }

    operator fun set(x: Int, y: Int, color: Color) {
        if (x < 0 || x >= image.width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${image.width - 1}")
        }
        if (y < 0 || y >= image.height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${image.height - 1}")
        }
        image.setRGB(x, y, color.rgb)
    }

    fun save(filename: String) {
        val file = File(filename)
        val extension = file.extension
        ImageIO.write(image, extension, file);
    }
}



fun main(args: Array<String>) {
    var inFilename = "surf.png"
    var outFilename = "surf_resized.png"

    for (i in args.indices) {
        when(args[i]) {
            "-in" -> inFilename = args[i + 1]
            "-out" -> outFilename = args[i + 1]
        }
    }

    val image = Image(inFilename)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            if (x == y || image.width - x == y + 1) {
                image[x, y] = Color.RED
            }
        }
    }

    image.save(outFilename)
}

