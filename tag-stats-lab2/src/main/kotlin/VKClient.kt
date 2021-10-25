interface VKClient {
    fun getStatsPerTagForHours(tag: String, hours: Int): List<Int>
}