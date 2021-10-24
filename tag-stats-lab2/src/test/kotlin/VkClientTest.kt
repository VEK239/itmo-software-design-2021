import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class VkClientTest {
    private lateinit var vkClientImpl: VKClientImpl

    @Mock
    var vkService: VKServiceImpl = VKServiceImpl()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        vkClientImpl = VKClientImpl(vkService)

    }

    @Test
    fun testClientWithNoPosts() {
        val currentTime = System.currentTimeMillis() / 1000
        Mockito.`when`(vkService.getDatesInPeriod("tag", 3, currentTime))
            .thenReturn(listOf())
        val result = vkClientImpl.getStatsPerTagForHours("tag", 3, currentTime)
        Assert.assertArrayEquals(intArrayOf(0, 0, 0), result.toIntArray())
    }

    @Test
    fun testClientWithOnePost() {
        val currentTime = System.currentTimeMillis() / 1000
        for (i in 0..2) {

            Mockito.`when`(vkService.getDatesInPeriod("tag", 3, currentTime))
                .thenReturn(listOf(currentTime - (0.5 * transformHoursToSeconds(1) + transformHoursToSeconds(i)).toInt()))

            val result = vkClientImpl.getStatsPerTagForHours("tag", 3, currentTime)

            val expectedResult = intArrayOf(0, 0, 0)
            expectedResult[2 - i] = 1
            println(result)
            Assert.assertArrayEquals(expectedResult, result.toIntArray())
        }
    }

    @Test
    fun testClientWithManyPosts() {
        val currentTime = System.currentTimeMillis()
        Mockito.`when`(vkService.getDatesInPeriod("tag", 3, currentTime))
            .thenReturn(listOf(
                currentTime - (0.5 * transformHoursToSeconds(1) + transformHoursToSeconds(0)).toInt(),
                currentTime - (0.5 * transformHoursToSeconds(1) + transformHoursToSeconds(1)).toInt(),
                currentTime - (0.5 * transformHoursToSeconds(1) + transformHoursToSeconds(2)).toInt(),
                currentTime - transformHoursToSeconds(2),
                currentTime - transformHoursToSeconds(1),
                ))
        val result = vkClientImpl.getStatsPerTagForHours("tag", 3, currentTime)
        Assert.assertArrayEquals(intArrayOf(1, 2, 2), result.toIntArray())
    }

    private fun transformHoursToSeconds(hours: Int) = hours * 3600

}