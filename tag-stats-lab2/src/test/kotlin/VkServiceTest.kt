import com.xebialabs.restito.builder.stub.StubHttp
import com.xebialabs.restito.semantics.Action
import com.xebialabs.restito.semantics.Condition
import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.Method
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class VkServiceTest {
    private lateinit var vkClientImplForStub: VKClientImpl

    companion object {
        private const val PORT = 8888
    }

    private fun transformHoursToSeconds(hours: Long) = hours * 3600

    @Test
    fun testServiceForOnePostFound() {
        val currentTime = System.currentTimeMillis() / 1000
        val postTime = currentTime - transformHoursToSeconds(1)
        withStubServer(PORT) { s ->
            StubHttp.whenHttp(s)
                .match(Condition.method(Method.GET), Condition.startsWithUri("/service-one-post"))
                .then(
                    Action.stringContent(
                        """{
                              "response": {
                                "items": [
                                  {
                                    "id": 170,
                                    "date": ${postTime}
                                  }
                                ],
                                "total_count": 100
                              }
                            }"""
                    )
                )
            val path = "http://localhost:$PORT/service-one-post"

            val service = VKServiceImpl(path)

            val res = service.getDatesInPeriod("tag", 3, currentTime)
            Assert.assertEquals(1, res.size)
            Assert.assertEquals(postTime, res[0])
        }
    }

    @Test
    fun testServiceForNoPostsFound() {
        val currentTime = System.currentTimeMillis() / 1000
        withStubServer(PORT) { s ->
            StubHttp.whenHttp(s)
                .match(Condition.method(Method.GET), Condition.startsWithUri("/service-no-posts"))
                .then(
                    Action.stringContent(
                        """{
                              "response": {
                                "items": [
                                ],
                                "total_count": 100
                              }
                            }"""
                    )
                )
            val path = "http://localhost:$PORT/service-no-posts"

            val service = VKServiceImpl(path)

            val res = service.getDatesInPeriod("tag", 3, currentTime)
            Assert.assertEquals(0, res.size)
        }
    }

    @Test
    fun testServiceForManyPostsFound() {
        val currentTime = System.currentTimeMillis() / 1000
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
                                    "date": ${currentTime}
                                  },
                                  {
                                    "id": 2,
                                    "date": ${currentTime - transformHoursToSeconds(1)}
                                  },
                                  {
                                    "id": 3,
                                    "date": ${currentTime - transformHoursToSeconds(3)}
                                  },
                                  {
                                    "id": 4,
                                    "date": ${currentTime - transformHoursToSeconds(2)}
                                  },
                                  {
                                    "id": 5,
                                    "date": ${currentTime - transformHoursToSeconds(2)}
                                  }
                                ],
                                "total_count": 100
                              }
                            }""".format(
                            currentTime,
                            currentTime - transformHoursToSeconds(1),
                            currentTime - transformHoursToSeconds(2),
                            currentTime - transformHoursToSeconds(2),
                            currentTime - transformHoursToSeconds(3),
                        )
                    )
                )
            val path = "http://localhost:$PORT/service-many-posts"

            val service = VKServiceImpl(path)

            val res = service.getDatesInPeriod("tag", 3, currentTime)
            Assert.assertEquals(5, res.size)
            Assert.assertArrayEquals(
                longArrayOf(
                    currentTime - transformHoursToSeconds(3),
                    currentTime - transformHoursToSeconds(2),
                    currentTime - transformHoursToSeconds(2),
                    currentTime - transformHoursToSeconds(1),
                    currentTime
                ), res.toLongArray()
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