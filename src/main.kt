import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

fun shortestPath(picture: BufferedImagePicture) : List<Int> {
    fun weight(x: Int, y: Int) = when (y) {
        0 -> 0.0
        in 1 .. picture.height -> picture.pixelEnergy(x, y + 1)
        picture.height + 1 -> 0.0
        else -> IndexOutOfBoundsException("x must be between 0 and ${picture.width + 1}")
    }
    val result = mutableListOf<Int>()




    return result
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

