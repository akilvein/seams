import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel


interface Picture {
    val height: Int
    val width: Int
    val image: BufferedImage
    operator fun get(x: Int, y: Int): Color
    operator fun set(x: Int, y: Int, color: Color)
}

class BufferedImagePicture : Picture {
    override val image: BufferedImage
    override val width: Int
    override val height: Int

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    }

    constructor(width: Int, height: Int, init: (Int, Int) -> Color) {
        this.width = width
        this.height = height
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                set(x, y, init(x, y))
            }
        }
    }

    constructor(image: BufferedImage) {
        this.image = image
        width = image.width
        height = image.height
    }

    constructor(filename: String) {
        val file = File(filename)
        val image = ImageIO.read(file)
        this.image = image
        width = image.width
        height = image.height
    }

    override fun get(x: Int, y: Int): Color {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${width - 1}")
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${height - 1}")
        }
        return Color(image.getRGB(x, y))
    }

    override fun set(x: Int, y: Int, color: Color) {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("x must be between 0 and ${width - 1}")
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and ${height - 1}")
        }
        image.setRGB(x, y, color.rgb)
    }

    private fun pixelEnergy(x: Int, y: Int): Double {
        fun dx2(x: Int, y: Int): Int =
            when (x) {
                0 -> dx2(x + 1, y)
                in 1 .. width - 2 -> {
                    val l = get(x - 1, y)
                    val r = get(x + 1, y)
                    (l.red - r.red) * (l.red - r.red) + (l.green - r.green) * (l.green - r.green) + (l.blue - r.blue) * (l.blue - r.blue)
                }
                width - 1 -> dx2(x - 1, y)
                else -> throw IndexOutOfBoundsException("x must be between 0 and ${width - 1}")
            }

        fun dy2(x: Int, y: Int): Int =
            when (y) {
                0 -> dy2(x, y + 1)
                in 1 .. height - 2 -> {
                    val t = get(x, y - 1)
                    val b = get(x, y + 1)
                    (t.red - b.red) * (t.red - b.red) + (t.green - b.green) * (t.green - b.green) + (t.blue - b.blue) * (t.blue - b.blue)
                }
                height - 1 -> dy2(x, y - 1)
                else -> throw IndexOutOfBoundsException("y must be between 0 and ${height - 1}")
            }

        return kotlin.math.sqrt(dx2(x, y).toDouble() + dy2(x, y).toDouble())
    }

    fun energyImage() = BufferedImagePicture(width, height) {
        x, y ->
        val energy = pixelEnergy(x, y)
        val color = (energy / kotlin.math.sqrt(6.0)).toInt()
        Color(color, color, color)
    }
}


class ImagePanel : JPanel() {
    private val image = BufferedImagePicture("dog.jpg").energyImage()

    init {
        preferredSize = Dimension(image.width, image.height)
    }

    override fun paint(g: Graphics) {
        g.drawImage(image.image, 0, 0, this)
    }

    /*
    override fun getWidth() = image.width
    override fun getHeight() = image.height
     */
}

fun main(args: Array<String>) {
    val frame = JFrame()
    val imagePanel = ImagePanel()
    frame.contentPane.add(imagePanel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true

}

