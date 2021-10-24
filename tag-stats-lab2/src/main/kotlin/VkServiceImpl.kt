import java.io.InputStreamReader
import java.net.URL
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


open class VKServiceImpl(private val apiUrl: String = "https://api.vk.com/method/newsfeed.search") : VKService {
    val VK_ACCESS_TOKEN: String =
        "12a8ccbc490c5c2f321592b1391c9f30b36ca7da004b41e51b61c822dbaa08f70942b760ec04a4a4f915d"
    val API_VERSION = "5.131"
    val MAX_AVAILABLE_DOWNLOAD_COUNT = 200


    override fun getDatesInPeriod(tag: String, hours: Int, currentTime: Long): List<Long> {
        val response = sendRequest("$apiUrl?${makeRequestString(tag, hours, currentTime)}")
        return extractDatesFromResponse(response)
    }

    private fun makeRequestString(tag: String, hours: Int, currentTime: Long): String {
        return mutableMapOf(
            "access_token" to VK_ACCESS_TOKEN,
            "v" to API_VERSION,
            "q" to tag,
            "start_time" to currentTime - hourToSeconds(hours),
            "count" to MAX_AVAILABLE_DOWNLOAD_COUNT
        ).map { "${it.key}=${it.value}" }.joinToString("&")
    }

    private fun extractDatesFromResponse(response: String): List<Long> {
        val reader = JsonReader(InputStreamReader(response.byteInputStream()))
        val result = mutableListOf<Long>()

        reader.beginObject()
        reader.nextName()
        reader.beginObject()

        while (reader.peek() == JsonToken.NAME) {
            if (reader.nextName() == "items") {
                reader.beginArray()
                while (reader.peek() != JsonToken.END_ARRAY) {
                    reader.beginObject()
                    while (reader.peek() == JsonToken.NAME) {
                        if (reader.nextName() == "date") {
                            result.add(reader.nextLong())
                            continue
                        }
                        reader.skipValue()
                    }
                    reader.endObject()
                }
                reader.endArray()
                continue
            }
            reader.skipValue()
        }
        return result.sorted()
    }

    private fun hourToSeconds(hour: Int) = hour * 60L * 60

    private fun sendRequest(url: String) = URL(url).readText()
}
