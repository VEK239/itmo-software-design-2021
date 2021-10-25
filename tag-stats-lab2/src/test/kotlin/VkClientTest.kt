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
        Mockito.`when`(vkService.getDatesInPeriod("tag", 3, getCurrentTimeSeconds()))
            .thenReturn(listOf())
        val result = vkClientImpl.getStatsPerTagForHours("tag", 3)
        Assert.assertArrayEquals(intArrayOf(0, 0, 0), result.toIntArray())
    }

    @Test
    fun testClientWithOnePost() {
        val currentTime = getCurrentTimeSeconds()
        for (i in 0..2) {

            Mockito.`when`(vkService.getDatesInPeriod("tag", 3, currentTime))
                .thenReturn(listOf(currentTime - (0.5 * hourToSeconds(1) + hourToSeconds(i)).toInt()))

            val result = vkClientImpl.getStatsPerTagForHours("tag", 3)

            val expectedResult = intArrayOf(0, 0, 0)
            expectedResult[2 - i] = 1
            println(result)
            Assert.assertArrayEquals(expectedResult, result.toIntArray())
        }
    }

    @Test
    fun testClientWithManyPosts() {
        val currentTime = getCurrentTimeSeconds()
        Mockito.`when`(vkService.getDatesInPeriod("tag", 3, currentTime))
            .thenReturn(listOf(
                currentTime - (0.5 * hourToSeconds(1) + hourToSeconds(0)).toInt(),
                currentTime - (0.5 * hourToSeconds(1) + hourToSeconds(1)).toInt(),
                currentTime - (0.5 * hourToSeconds(1) + hourToSeconds(2)).toInt(),
                currentTime - hourToSeconds(2),
                currentTime - hourToSeconds(1),
                ))
        val result = vkClientImpl.getStatsPerTagForHours("tag", 3)
        Assert.assertArrayEquals(intArrayOf(1, 2, 2), result.toIntArray())
    }

}