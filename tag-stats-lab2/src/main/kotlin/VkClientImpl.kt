class VKClientImpl(private val vkService: VKService) : VKClient {

    override fun getStatsPerTagForHours(tag: String, hours: Int): List<Int> {
        val currentTime = getCurrentTimeSeconds()
        val dates = vkService.getDatesInPeriod(tag, hours, currentTime)
        return (hours downTo 1).map { getValueInHour(dates, currentTime, it) }
    }

    private fun getValueInHour(dates: List<Long>, currentTime: Long, hour: Int): Int {
        val leftTimeBorder = currentTime - hourToSeconds(hour)
        val rightTimeBorder = currentTime - hourToSeconds(hour - 1)
        return dates.filter { it in leftTimeBorder until rightTimeBorder }.size
    }

}
