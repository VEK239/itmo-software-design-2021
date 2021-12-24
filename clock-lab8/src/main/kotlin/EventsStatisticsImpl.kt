class EventsStatisticsImpl(private val clock: Clock) : EventsStatistics {
    private val eventsToTimeMap: MutableMap<String, MutableList<Long>> = HashMap()

    override fun incEvent(name: String) {
        val now = clock.now()
        if (!eventsToTimeMap.containsKey(name)) {
            eventsToTimeMap[name] = ArrayList()
        }
        eventsToTimeMap[name]!!.add(now.epochSecond)
    }

    override fun getEventStatisticsByName(name: String): Double {
        if (!eventsToTimeMap.containsKey(name)) {
            return 0.0
        }
        return updateList(name) / MINUTES_IN_HOUR
    }

    override fun getAllEventStatistics(): Map<String, Double> {
        val result = eventsToTimeMap.keys.map { key -> key to getEventStatisticsByName(key) }.toMap()
        return result.filter { (_, value) -> value > 0 }.toMap()
    }

    override fun printStatistics() {
        getAllEventStatistics().forEach { (key, rpm) ->
            println("Event: $key, rpm: $rpm")
        }
    }

    private fun updateList(name: String): Int {
        val eventsByName: List<Long> = eventsToTimeMap[name]!!
        val now = clock.now().epochSecond
        val hourAgo = now - SECONDS_IN_HOUR
        eventsToTimeMap[name] = eventsByName.filter { it in hourAgo..now } as MutableList<Long>
        return eventsToTimeMap[name]!!.size
    }

    companion object {
        private const val MINUTES_IN_HOUR: Double = 60.0
        private const val SECONDS_IN_HOUR: Long = 3600
    }
}