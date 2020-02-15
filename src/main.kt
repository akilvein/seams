import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel



fun pictureWithSeam(filename: String): BufferedImagePicture {
    val image = BufferedImagePicture(filename)
    val seam = image.getVerticalSeam()

    for (y in seam.indices) {
        val x = seam[y]
        image[x, y] = Color.RED
    }

    return image
}

class ImagePanel(var image: BufferedImagePicture) : JPanel() {
    init {
        preferredSize = Dimension(image.width, image.height)
    }

    override fun paint(g: Graphics) {
        g.drawImage(image.image, 0, 0, this)
    }
}

fun showFrameWithImage(image: BufferedImagePicture) {
    val frame = JFrame()
    val imagePanel = ImagePanel(image)
    frame.contentPane.add(imagePanel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
}

fun main() {
    var image = BufferedImagePicture("boats.jpg")
    showFrameWithImage(image)

    showFrameWithImage(image.resize(image.width - 100))
    //image.resize(image.width - 100)


    /*
    frame.addComponentListener(object : ComponentAdapter() {
        override fun componentResized(evt: ComponentEvent) {
            val c: Component = evt.source as Component
            val width = c.size.width
            imagePanel.image = image.resize(width)
        }
    })*/
}

