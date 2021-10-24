interface VKClient {
    fun getStatsPerTagForHours(tag: String, hours: Int, currentTime: Long): List<Int>
}