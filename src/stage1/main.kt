package stage1

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Image(
    val width: Int,
    val height: Int,
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB))
{
    constructor(width: Int, height: Int, init: (Int, Int) -> Color) : this(width, height) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                set(x, y, init(x, y))
            }
        }
    }

    operator fun get(x: Int, y: Int): Color {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${width - 1}")
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${height - 1}")
        }
        return Color(image.getRGB(x, y))
    }

    operator fun set(x: Int, y: Int, color: Color) {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${width - 1}")
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${height - 1}")
        }
        image.setRGB(x, y, color.rgb)
    }

    fun save(filename: String) {
        ImageIO.write(image, "png", File("$filename.png"));
    }
}


fun main(args: Array<String>) {
    val width = 10
    val height = 10
    Image(width, height) {
            x, y ->
        if (x == y || width - x == y + 1) {
            Color.RED
        }
        else {
            Color.BLACK
        }
    }.save("out")
}

