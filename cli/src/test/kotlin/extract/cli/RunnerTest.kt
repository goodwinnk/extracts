package extract.cli

import org.junit.Assert
import org.junit.Test

class RunnerTest {
    @Test
    fun defaults() {
        doTest(listOf(), Options())
    }

    @Test
    fun help() {
        doTest(listOf("-help"), Options(help = true))
        doTest(listOf("-help=true"), Options(help = true), errorMessage = "No argument is allowed: true")
        doTest(listOf("-n", "-help"), Options(help = true), errorMessage = "\"-help\" is not a valid value for \"-n\"")
        doTest(listOf("-help", "-n"), Options(help = true), errorMessage = "Option \"-number (-n)\" takes an operand")
    }

    @Test
    fun number() {
        doTest(listOf("-number=30"), Options(number = 30))
        doTest(listOf("-n=25"), Options(number = 25))
        doTest(listOf("-number", "5"), Options(number = 5))
        doTest(listOf("-n", "6"), Options(number = 6))
        doTest(listOf("-n", "6.21"), Options(number = 6), errorMessage = "\"6.21\" is not a valid value for \"-n\"")
    }

    fun doTest(args: List<String>, expectedOptions: Options, errorMessage: String? = null) {
        try {
            val (options, _) = Runner.parseArguments(args.toTypedArray())
            Assert.assertEquals(expectedOptions, options)

            if (errorMessage != null) {
                Assert.fail("Parse error was expected")
            }
        } catch (ex: ParserException) {
            if (errorMessage == null) {
                throw ex
            }

            Assert.assertEquals("Bad parse error", errorMessage, ex.message)
            Assert.assertEquals("Bad usage", USAGE, ex.usage.replace("\r", ""))
        }
    }

    companion object {
        val USAGE = """
             | -extracts (-f) FILE : Path to extracts file. "repository/.extracts" path is
             |                       used by default.
             | -help               : Get the program options help
             | -html-log FILE      : Output html file. System temporary directory is used by
             |                       default.
             | -number (-n) N      : Limit the number of commits to output. Default is 50.
             | -open               : Is result history should be open automatically. Default
             |                       is TRUE.
             | -revision VAL       : Revision where log should start. HEAD commit is used by
             |                       default.
             |
            """.trimMargin()
    }
}
