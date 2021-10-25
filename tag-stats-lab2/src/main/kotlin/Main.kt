fun main() {
    println(VKClientImpl(VKServiceImpl()).getStatsPerTagForHours("weather", 12))
}