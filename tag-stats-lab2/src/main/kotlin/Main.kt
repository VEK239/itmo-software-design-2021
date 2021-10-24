fun main() {
    val currentTimeInSeconds = System.currentTimeMillis() / 1000
    println(VKClientImpl(VKServiceImpl()).getStatsPerTagForHours("weather", 12, currentTimeInSeconds))
}