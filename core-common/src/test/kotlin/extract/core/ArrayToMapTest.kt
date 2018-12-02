package extract.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayToMapTest {
    @Test
    fun testEmpty() {
        assertEquals(emptyMap(), arrayToMap(emptyArray()))
    }

    @Test
    fun test() {
        val result = arrayToMap(
                arrayOf(
                        "META-INF", "META-INF",
                        "stdlib", "Standard Library",
                        "cidr-plugin", "CIDR",
                        "clion-plugin", "CLion",
                        "idea-completion", "Completion",
                        "kotlin-gralde-plugin", "Gradle Plugin",
                        "expectactual", "Expect Actual",
                        "ir.psi2ir", "PSI to IR"
                )
        )

        assertEquals(
                mapOf(
                        "META-INF" to "META-INF",
                        "stdlib" to "Standard Library",
                        "cidr-plugin" to "CIDR",
                        "clion-plugin" to "CLion",
                        "idea-completion" to "Completion",
                        "kotlin-gralde-plugin" to "Gradle Plugin",
                        "expectactual" to "Expect Actual",
                        "ir.psi2ir" to "PSI to IR"
                ), result)
    }
}