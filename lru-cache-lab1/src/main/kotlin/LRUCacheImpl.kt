import java.util.ArrayDeque

class LRUCacheImpl<K, V>(private val maxSize: Int = 10) : LRUCache<K, V> {
    private val deque = ArrayDeque<K>(maxSize)
    private val map = HashMap<K, V>()

    init {
        require(maxSize > 0) { "capacity should be positive!" }
    }

    override fun set(key: K, value: V): LRUCache<K, V> {
        if (deque.contains(key)) {
            updateExistingKey(key)
        } else {
            addNewKey(key)
        }
        assert(deque.contains(key))
        map[key] = value
        assert(map.contains(key))
        return this
    }

    override fun get(key: K): V? {
        return map[key]
    }

    private fun updateExistingKey(key: K) {
        deque.remove(key)
        deque.add(key)
        assert(deque.contains(key))
    }

    private fun addNewKey(key: K) {
        if (deque.size == maxSize) {
            map.remove(deque.removeFirst())
            assert(deque.size == maxSize - 1)
        }
        deque.add(key)
        assert(deque.size <= maxSize)
    }

    override fun getElementsCount(): Int {
        return deque.size
    }
}