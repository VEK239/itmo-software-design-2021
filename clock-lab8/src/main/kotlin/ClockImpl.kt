import java.time.Instant
import java.time.temporal.TemporalAmount

class ClockImpl(private var now: Instant): Clock {
    override fun now() = now
    fun addAndGet(time: TemporalAmount): Instant {
        now += time
        return now
    }
}