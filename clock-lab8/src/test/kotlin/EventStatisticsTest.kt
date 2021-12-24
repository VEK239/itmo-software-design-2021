import junit.framework.Assert.assertTrue
import org.junit.Test
import java.lang.Math.abs
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import kotlin.test.assertEquals


private const val EPSILON = 1e-4
private const val MINUTES_IN_HOUR = 60.0

class EventStatisticTest {
    private fun equals(left: Double, right: Double): Boolean {
        return abs(left - right) <= EPSILON
    }

    private fun createStatisticsInstance(clock: Clock = ClockImpl(now())): EventsStatistics {
        return EventsStatisticsImpl(clock)
    }

    @Test
    fun getStatsIfEmpty() {
        val statistics = createStatisticsInstance()

        assertTrue(equals(statistics.getEventStatisticsByName("anotherTestName"), 0.0))
        assertEquals(0, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatsIfNameNotExists() {
        val statistics = createStatisticsInstance()

        statistics.incEvent("testName")

        assertTrue(equals(statistics.getEventStatisticsByName("anotherTestName"), 0.0))
        assertEquals(1, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatsIfNameExists() {
        val statistics = createStatisticsInstance()

        statistics.incEvent("testName")

        assertTrue(equals(statistics.getEventStatisticsByName("testName"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(1, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatisticsByDifferentNames() {
        val statistics = createStatisticsInstance()

        statistics.incEvent("test1")
        statistics.incEvent("test2")
        statistics.incEvent("test1")

        assertTrue(equals(statistics.getEventStatisticsByName("test1"), 2.0 / MINUTES_IN_HOUR))
        assertTrue(equals(statistics.getEventStatisticsByName("test2"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(2, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatisticsWithManySameEvents() {
        val statistics = createStatisticsInstance()
        for (i in 1..500) {
            statistics.incEvent("test")

            assertTrue(equals(statistics.getEventStatisticsByName("test"), i / MINUTES_IN_HOUR))
            assertEquals(1, statistics.getAllEventStatistics().size)
        }
    }

    @Test
    fun getStatisticsWithManyDifferentEvents() {
        val statistics = createStatisticsInstance()
        for (i in 1..100) {
            for (j in 1..50) {
                statistics.incEvent("test$i")

                assertTrue(equals(statistics.getEventStatisticsByName("test$i"), j / MINUTES_IN_HOUR))
                assertEquals(i, statistics.getAllEventStatistics().size)
            }
        }
    }


    @Test
    fun getStatsIfEmptyClockChanging() {
        val clock = ClockImpl(now())
        val statistics = createStatisticsInstance(clock)

        assertTrue(equals(statistics.getEventStatisticsByName("anotherTestName"), 0.0))
        assertEquals(0, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(30))
        assertTrue(equals(statistics.getEventStatisticsByName("anotherTestName"), 0.0))
        assertEquals(0, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(60))
        assertTrue(equals(statistics.getEventStatisticsByName("anotherTestName"), 0.0))
        assertEquals(0, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatsIfNameExistsClockChanging() {
        val clock = ClockImpl(now())

        val statistics = createStatisticsInstance(clock)
        statistics.incEvent("testName")

        assertTrue(equals(statistics.getEventStatisticsByName("testName"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(1, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(30))
        assertTrue(equals(statistics.getEventStatisticsByName("testName"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(1, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(31))
        assertTrue(equals(statistics.getEventStatisticsByName("testName"), 0.0))
        assertEquals(0, statistics.getAllEventStatistics().size)
    }

    @Test
    fun getStatisticsByDifferentNamesClockChanging() {
        val clock = ClockImpl(now())

        val statistics = createStatisticsInstance(clock)
        statistics.incEvent("test1")
        statistics.incEvent("test2")
        statistics.incEvent("test1")

        assertTrue(equals(statistics.getEventStatisticsByName("test1"), 2.0 / MINUTES_IN_HOUR))
        assertTrue(equals(statistics.getEventStatisticsByName("test2"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(2, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(30))
        assertTrue(equals(statistics.getEventStatisticsByName("test1"), 2.0 / MINUTES_IN_HOUR))
        assertTrue(equals(statistics.getEventStatisticsByName("test2"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(2, statistics.getAllEventStatistics().size)
        statistics.incEvent("test2")
        assertEquals(2, statistics.getAllEventStatistics().size)

        clock.addAndGet(Duration.ofMinutes(31))
        assertTrue(equals(statistics.getEventStatisticsByName("test1"), 0.0))
        assertTrue(equals(statistics.getEventStatisticsByName("test2"), 1.0 / MINUTES_IN_HOUR))
        assertEquals(1, statistics.getAllEventStatistics().size)
    }

}