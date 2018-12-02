package extract.core

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitTest {
    @Test
    fun split() {
        assertEquals("Some Word UI", "SomeWordUI".removeCamelCase())
        assertEquals("a B", "aB".removeCamelCase())
        assertEquals("AB", "AB".removeCamelCase())
        assertEquals("Some Word", "SomeWord".removeCamelCase())
        assertEquals("Some Word U", "SomeWordU".removeCamelCase())
        assertEquals("", "".removeCamelCase())
        assertEquals("a", "a".removeCamelCase())
        assertEquals("A", "A".removeCamelCase())
        assertEquals("macos64 Main", "macos64Main".removeCamelCase())
    }
}