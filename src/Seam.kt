private fun BufferedImagePicture.pixelIndex(x: Int, y: Int) = y * width + x

fun BufferedImagePicture.getVerticalSeamByDijkstra() : Array<Int> {
    val graphSize = width * (height + 2)
    val sink = pixelIndex(width - 1, height + 1)
    val source = pixelIndex(0, 0)

    val distances = DoubleArray(graphSize) { Double.MAX_VALUE }
    distances[pixelIndex(0, 0)] = 0.0

    val preceding = IntArray(graphSize) { - 1}

    val weights = DoubleArray(graphSize) {
        val x = it % width
        val y = it / width
        when (y) {
            0 -> 0.0
            in (1 .. height) -> pixelEnergy(x, y - 1)
            height + 1 -> 0.0
            else -> throw IndexOutOfBoundsException("y must be in [0 .. ${height + 1}]")
        }
    }

    val q = IndexPriorityQueue<Double>(graphSize)
    q.insert(source, 0.0)

    fun relaxPixel(index: Int, current: Int) {
        val newDistance = distances[current] + weights[index]
        if (newDistance < distances[index]) {
            distances[index] = newDistance
            preceding[index] = current
            if (q.contains(index)) {
                q.decreaseKey(index, newDistance)
            }
            else {
                q.insert(index, newDistance)
            }
        }
    }

    while (!q.isEmpty()) {
        val index = q.delMin();

        if (index == sink) {
            // woohoo!
            break
        }

        val y = index / width
        val x = index % width

        //right for top/bottom rows
        if ((y == 0 || y == height + 1) && x < width - 1) {
            relaxPixel(index + 1, index)
        }

        //down
        if (y <= height) {
            relaxPixel(index + width, index)

            // down-left
            if (x > 0) {
                relaxPixel(index + width - 1, index)
            }

            // down-right
            if (x < width - 1) {
                relaxPixel(index + width + 1, index)
            }
        }
    }

    fun unwindPath(): Array<Int> {
        val result = Array(height) { -1 }

        var i = sink
        while (i > width) {
            i = preceding[i]
            val y = i / width
            val x = i % width

            if (y - 1 in result.indices) {
                result[y - 1] = x
            }
        }

        return result
    }

    return unwindPath()
}


fun BufferedImagePicture.getVerticalSeam() : Array<Int> {
    val imageSize = width * height
    val preceding = IntArray(imageSize) { - 1}

    val weights = DoubleArray(imageSize) { index ->
        val x = index % width
        val y = index / width
        pixelEnergy(x, y)
    }

    for (y in height - 2 downTo 0) {
        for (x in  0 until width) {
            val index = y * width + x

            val bottomIndex = index + width
            var minIndex = bottomIndex
            if (x > 0 && weights[bottomIndex - 1] < weights[minIndex]) {
                minIndex = bottomIndex - 1
            }
            if (x < width - 1 && weights[bottomIndex + 1] < weights[minIndex]) {
                minIndex = bottomIndex + 1
            }

            weights[index] += weights[minIndex]
            preceding[index] = minIndex
        }
    }

    var minIndex = 0
    for (index in 0 until width) {
        if (weights[index] < weights[minIndex]) {
            minIndex = index
        }
    }

    return Array(height) {
        val current = minIndex
        minIndex = preceding[minIndex]
        current % width
    }
}

