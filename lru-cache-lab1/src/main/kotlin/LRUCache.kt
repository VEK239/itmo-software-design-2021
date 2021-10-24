interface LRUCache<K, V> {
    fun set(key: K, value: V): LRUCache<K, V>
    fun get(key: K): V?
    fun getElementsCount(): Int
}