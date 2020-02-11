import java.util.*

fun BufferedImagePicture.getVerticalSeam() : Array<Int> {
    val distances = Array(width) { DoubleArray(height + 2) { Double.MAX_VALUE } }

    class PixelNode(val x: Int, val y: Int, val distance: Double) : Comparable<PixelNode> {
        override fun compareTo(other: PixelNode): Int {
            return distance.compareTo(other.distance)
        }
    }

    val preceding = Array(width) { Array<PixelNode?>(height + 2) { null } }
    val processed = Array(width) { BooleanArray(height + 2) { false } }

    val source = PixelNode(0,0, 0.0)
    distances[0][0] = 0.0
    val q = PriorityQueue<PixelNode>(width * height)
    q.add(source)

    fun weight(x: Int, y: Int): Double = when (y) {
        0 -> 0.0
        in (1 .. height) -> pixelEnergy(x, y - 1)
        height + 1 -> 0.0
        else -> throw IndexOutOfBoundsException("y must be in [0 .. ${height + 1}]")
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
        val result = Array(height) { -1 }

        var x = width - 1
        var y = height + 1

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

        if (pixel.x == width - 1 && pixel.y == height + 1) {
            // woohoo!
            break
        }

        processed[pixel.x][pixel.y] = true

        //right for top/bottom rows
        if ((pixel.y == 0 || pixel.y == height + 1) && pixel.x < width - 1) {
            relaxPixel(pixel.x + 1, pixel.y, pixel)
        }

        //down
        if (pixel.y <= height) {
            relaxPixel(pixel.x, pixel.y + 1, pixel)

            // down-left
            if (pixel.x > 0) {
                relaxPixel(pixel.x - 1, pixel.y + 1, pixel)
            }

            // down-right
            if (pixel.x < width - 1) {
                relaxPixel(pixel.x + 1, pixel.y + 1, pixel)
            }
        }
    }

    return unwindPath()
}