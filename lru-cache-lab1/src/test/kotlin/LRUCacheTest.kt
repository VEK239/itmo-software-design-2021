import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class LRUCacheImplTest {
    private lateinit var cache: LRUCacheImpl<Int, Int>

    @Before
    fun initLRUCacheObject() {
        cache = LRUCacheImpl(5)
    }

    @Test
    fun testEmptyCache() {
        Assert.assertEquals(0, cache.getElementsCount())
    }

    @Test
    fun testSetOperation() {
        cache
            .set(1, 1)
        Assert.assertEquals(1, cache.getElementsCount())
        Assert.assertEquals(1, cache.get(1))

        cache
            .set(2, 2)
        Assert.assertEquals(2, cache.getElementsCount())
        Assert.assertEquals(2, cache.get(2))

        cache
            .set(4, 4)
        Assert.assertEquals(3, cache.getElementsCount())
        Assert.assertEquals(4, cache.get(4))
    }

    @Test
    fun testGetForNotExistingElement() {
        cache
            .set(1, 3)
            .set(2, 4)
            .set(4, 4)
        Assert.assertNull(cache.get(3))
    }


    @Test
    fun testInsertionsOverMaximumSize() {
        cache
            .set(1, 1)
            .set(2, 2)
            .set(3, 3)
            .set(4, 4)
            .set(5, 5)
            .set(6, 6)
            .set(7, 7)
        Assert.assertEquals(5, cache.getElementsCount())
    }

    @Test
    fun testMultipleKeySettingValues() {
        cache
            .set(1, 1)
            .set(1, 2)
        Assert.assertEquals(2, cache.get(1))
        cache
            .set(3, 3)
        Assert.assertEquals(3, cache.get(3))

        cache
            .set(3, 4)
        Assert.assertEquals(4, cache.get(3))
        cache
            .set(1, 5)
            .set(3, 6)
            .set(3, 7)
        Assert.assertEquals(7, cache.get(3))
        Assert.assertEquals(5, cache.get(1))

    }

    @Test
    fun testMultipleKeySettingSizes() {
        cache
            .set(1, 1)
            .set(1, 2)
        Assert.assertEquals(1, cache.getElementsCount() )
        cache
            .set(3, 3)
        Assert.assertEquals(2, cache.getElementsCount())

        cache
            .set(3, 4)
        Assert.assertEquals(2, cache.getElementsCount())
        cache
            .set(1, 5)
            .set(3, 6)
            .set(3, 7)
        Assert.assertEquals(2, cache.getElementsCount())
    }

    @Test
    fun testNullForOverMaximumElements() {
        cache
            .set(1, 1)
            .set(2, 1)
            .set(3, 3)
            .set(4, 10)
            .set(5, 8)
            .set(6, 3)
            .set(7, 7)
        Assert.assertNull(cache.get(1))
        Assert.assertNull(cache.get(2))
    }

    @Test
    fun testExceptionNegativeSize() {
        assertFailsWith<java.lang.IllegalArgumentException> { LRUCacheImpl<Int, Int>(-1) }
    }
}