import java.util.*

class IndexPriorityQueue<Key : Comparable<Key>>(private val capacity : Int) {
    private val pq = IntArray(capacity + 1) { 0 } // binary heap using 1-based indexing
    private val qp = IntArray(capacity + 1) { -1 } // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private val keys = mutableListOf<Key?>()

    init {
        repeat(capacity + 1) {
            keys.add(null)
        }
    }

    private var size : Int = 0

    fun isEmpty() = (size == 0)

    operator fun contains(i: Int): Boolean {
        validateIndex(i)
        return qp[i] != -1
    }

    fun insert(i: Int, key: Key) {
        validateIndex(i)
        require(!contains(i)) { "index is already in the priority queue" }

        size++
        qp[i] = size
        pq[size] = i
        keys[i] = key
        swim(size)
    }

    fun minIndex(): Int {
        validateNotEmpty()
        return pq[1]
    }

    fun minKey(): Key? {
        validateNotEmpty()
        return keys[pq[1]]
    }

    fun delMin(): Int {
        validateNotEmpty()

        val min = pq[1]
        exchange(1, size--)
        sink(1)
        assert(min == pq[size + 1])
        qp[min] = -1 // delete
        keys[min] = null // to help with garbage collection
        pq[size + 1] = -1 // not needed
        return min
    }

    fun keyOf(i: Int): Key? {
        validateIndex(i)
        return if (contains(i)) keys[i] else throw NoSuchElementException("index is not in the priority queue")
    }

    fun changeKey(i: Int, key: Key) {
        validateIndex(i)
        validateContains(i)

        keys[i] = key
        swim(qp[i])
        sink(qp[i])
    }

    fun decreaseKey(i: Int, key: Key) {
        validateIndex(i)
        validateContains(i)

        require(key < keys[i]!!) { "Calling decreaseKey() with a key greater or equal to the key in the priority queue" }
        keys[i] = key
        swim(qp[i])
    }

    fun increaseKey(i: Int, key: Key) {
        validateIndex(i)
        validateContains(i)

        require(key > keys[i]!!) { "Calling increaseKey() with a key less or equal than the key in the priority queue" }
        keys[i] = key
        sink(qp[i])
    }

    fun delete(i: Int) {
        validateIndex(i)
        validateContains(i)

        val index = qp[i]
        exchange(index, size--)
        swim(index)
        sink(index)
        keys[i] = null
        qp[i] = -1
    }

    private fun validateIndex(i: Int) = require(i in 0 .. capacity ) {
        "index ($i) is ot of bounds [$0 .. ${capacity}]"
    }

    private fun validateContains(i: Int) = require(contains(i)) {
        "index is not in the priority queue"
    }

    private fun validateNotEmpty() = require(size > 0) { "Priority queue underflow" }

    private fun greater(i: Int, j: Int): Boolean {
        validateIndex(i)
        validateIndex(j)
        return keys[pq[i]]!! > keys[pq[j]]!!
    }

    private fun exchange(i: Int, j: Int) {
        val swap = pq[i]
        pq[i] = pq[j]
        pq[j] = swap
        qp[pq[i]] = i
        qp[pq[j]] = j
    }

    private fun swim(k: Int) {
        var k = k
        while (k > 1 && greater(k / 2, k)) {
            exchange(k, k / 2)
            k /= 2
        }
    }

    private fun sink(k: Int) {
        var k = k
        while (2 * k <= size) {
            var j = 2 * k
            if (j < size && greater(j, j + 1)) j++
            if (!greater(k, j)) break
            exchange(k, j)
            k = j
        }
    }


    companion object {
        fun test() {
            val strings =
                arrayOf("it", "was", "the", "best", "of", "times", "it", "was", "the", "worst")
            val pq = IndexPriorityQueue<String>(strings.size)
            for (i in strings.indices) {
                pq.insert(i, strings[i])
            }

            while (!pq.isEmpty()) {
                val i = pq.delMin()
                println(i.toString() + " " + strings[i])
            }

            println()

            for (i in strings.indices) {
                pq.insert(i, strings[i])
            }

            for (i in strings.indices) {
                println(i.toString() + " " + strings[i])
            }

            while (!pq.isEmpty()) {
                pq.delMin()
            }
        }
    }

}