interface VKService {
    fun getDatesInPeriod(tag: String, hours: Int, currentTime: Long = System.currentTimeMillis()): List<Long>
}