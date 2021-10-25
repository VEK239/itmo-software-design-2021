import com.xebialabs.restito.builder.stub.StubHttp
import com.xebialabs.restito.semantics.Action
import com.xebialabs.restito.semantics.Condition
import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.Method
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class VkClientTest {
    companion object {
        private const val PORT = 8888
    }

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

    @Test
    fun testServiceAndClientForManyPostsFound() {
        val currentTime = getCurrentTimeSeconds()
        withStubServer(PORT) { s ->
            StubHttp.whenHttp(s)
                .match(Condition.method(Method.GET), Condition.startsWithUri("/service-many-posts"))
                .then(
                    Action.stringContent(
                        """{
                              "response": {
                                "items": [
                                  {
                                    "id": 1,
                                    "date": $currentTime
                                  },
                                  {
                                    "id": 2,
                                    "date": ${currentTime - hourToSeconds(1)}
                                  },
                                  {
                                    "id": 3,
                                    "date": ${currentTime - hourToSeconds(3)}
                                  },
                                  {
                                    "id": 4,
                                    "date": ${currentTime - hourToSeconds(2)}
                                  }
                                ],
                                "total_count": 100
                              }
                            }""".format(
                            currentTime - 0.5 * hourToSeconds(1),
                            currentTime - 1.5 * hourToSeconds(1),
                            currentTime - 2.5 * hourToSeconds(1)
                        )
                    )
                )
            val path = "http://localhost:$PORT/service-many-posts"

            val client = VKClientImpl(VKServiceImpl(path))

            val res = client.getStatsPerTagForHours("tag", 4)
            Assert.assertEquals(4, res.size)
            Assert.assertArrayEquals(
                intArrayOf(0, 1, 1, 1), res.toIntArray()
            )
        }
    }

    private fun withStubServer(port: Int, callback: (StubServer?) -> Unit) {
        var stubServer: StubServer? = null
        try {
            stubServer = StubServer(port).run()
            callback(stubServer)
        } finally {
            stubServer?.stop()
        }
    }
}