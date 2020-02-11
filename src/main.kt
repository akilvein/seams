import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel


fun getSeam(picture: BufferedImagePicture) : Array<Int> {
    val distances = Array(picture.width) { DoubleArray(picture.height + 2) { Double.MAX_VALUE } }

    class PixelNode(val x: Int, val y: Int, val distance: Double) : Comparable<PixelNode> {
        override fun compareTo(other: PixelNode): Int {
            return distance.compareTo(other.distance)
        }
    }

    val preceding = Array(picture.width) { Array<PixelNode?>(picture.height + 2) { null } }
    val processed = Array(picture.width) { BooleanArray(picture.height + 2) { false } }

    val source = PixelNode(0,0, 0.0)
    distances[0][0] = 0.0
    val q = PriorityQueue<PixelNode>(picture.width * picture.height)
    q.add(source)

    fun weight(x: Int, y: Int): Double = when (y) {
        0 -> 0.0
        in (1 .. picture.height) -> picture.pixelEnergy(x, y - 1)
        picture.height + 1 -> 0.0
        else -> throw IndexOutOfBoundsException("y must be in [0 .. ${picture.height + 1}]")
    }

    fun relaxPixel(x: Int, y: Int, current: PixelNode) {
        val newDistance = distances[current.x][current.y] + weight(x, y)
        if (newDistance < distances[x][y]) {
            distances[x][y] = newDistance
            preceding[x][y] = current
            q.add(PixelNode(x, y, newDistance))
        }
    }

    fun unwindPath(): Array<Int> {
        val result = Array(picture.height) { -1 }

        var x = picture.width - 1
        var y = picture.height + 1

        while (y > 0) {
            val p = preceding[x][y]!!
            x = p.x
            y = p.y

            if (y - 1 in result.indices) {
                result[y - 1] = x
            }
        }

        return result
    }

    while (q.isNotEmpty()) {
        val pixel = q.poll()

        // we can have duplicates in queue
        if (processed[pixel.x][pixel.y]) {
            continue
        }

        if (pixel.x == picture.width - 1 && pixel.y == picture.height + 1) {
            // woohoo!
            break
        }

        processed[pixel.x][pixel.y] = true

        //right for top/bottom rows
        if ((pixel.y == 0 || pixel.y == picture.height) && pixel.x < picture.width - 1) {
            relaxPixel(pixel.x + 1, pixel.y, pixel)
        }

        //down
        if (pixel.y <= picture.height) {
            relaxPixel(pixel.x, pixel.y + 1, pixel)

            // down-left
            if (pixel.x > 0) {
                relaxPixel(pixel.x - 1, pixel.y + 1, pixel)
            }

            // down-right
            if (pixel.x < picture.width - 1) {
                relaxPixel(pixel.x + 1, pixel.y + 1, pixel)
            }
        }
    }

    return unwindPath()
}

fun pictureWithSeam(filename: String): BufferedImagePicture {
    val image = BufferedImagePicture(filename)
    val seam = getSeam(image)

    for (y in seam.indices) {
        val x = seam[y]
        image[x, y] = Color.RED
    }

    return image
}

class ImagePanel : JPanel() {
    //private val image = BufferedImagePicture("dog.jpg").energyImage()
    private val image = pictureWithSeam("surf.png")
    //private val image = pictureWithSeam("dog.jpg")

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

fun main() {
    val frame = JFrame()
    val imagePanel = ImagePanel()
    frame.contentPane.add(imagePanel)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true

}

