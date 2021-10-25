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
    companion object {
        private const val PORT = 8888
    }

    @Test
    fun testServiceForOnePostFound() {
        val currentTime = getCurrentTimeSeconds()
        val postTime = currentTime - hourToSeconds(1)
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
                                    "date": $postTime
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
        val currentTime = getCurrentTimeSeconds()
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
                                  },
                                  {
                                    "id": 5,
                                    "date": ${currentTime - hourToSeconds(2)}
                                  }
                                ],
                                "total_count": 100
                              }
                            }""".format(
                            currentTime,
                            currentTime - hourToSeconds(1),
                            currentTime - hourToSeconds(2),
                            currentTime - hourToSeconds(2),
                            currentTime - hourToSeconds(3),
                        )
                    )
                )
            val path = "http://localhost:$PORT/service-many-posts"

            val service = VKServiceImpl(path)

            val res = service.getDatesInPeriod("tag", 3, currentTime)
            Assert.assertEquals(5, res.size)
            Assert.assertArrayEquals(
                longArrayOf(
                    currentTime - hourToSeconds(3),
                    currentTime - hourToSeconds(2),
                    currentTime - hourToSeconds(2),
                    currentTime - hourToSeconds(1),
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