import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
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
            throw IndexOutOfBoundsException("x must be between 0 and " + (width - 1))
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and " + (height - 1))
        }
        return Color(image.getRGB(x, y))
    }

    override fun set(x: Int, y: Int, color: Color) {
        if (x < 0 || x >= width) {
            throw IndexOutOfBoundsException("x must be between 0 and " + (width - 1))
        }
        if (y < 0 || y >= height) {
            throw IndexOutOfBoundsException("y must be between 0 and " + (height - 1))
        }
        image.setRGB(x, y, color.rgb)
    }
}


class ImagePanel : JPanel() {
    val image = BufferedImagePicture("dog.jpg")

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

