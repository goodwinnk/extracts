package extract.cli

import java.io.File
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import extract.core.ConfigureGitException
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.awt.Desktop

data class Options(
        @set:Option(
                name = "-extracts", aliases = ["-f"],
                usage = "Path to extracts file. \"repository/.extracts\" path is used by default.")
        var extracts: File? = null,

        @set:Option(name = "-number", aliases = ["-n"], usage = "Limit the number of commits to output. Default is 50.")
        var number: Int = 50,

        @set:Option(name = "-open", usage = "Is result history should be open automatically. Default is TRUE.")
        var autoOpen: Boolean = true,

        @set:Option(
                name = "-html-log",
                usage = "Output html file. System temporary directory is used by default.")
        var output: File? = null,

        @set:Option(name = "-help", help = true, usage = "Get the program options help")
        var help: Boolean = false,

        @set:Option(name = "-repository", aliases = ["-r"])
        var repository: File = File(""),

        @set:Option(name = "-revision", usage = "Revision where log should start. HEAD commit is used by default.")
        var revision: String? = null
)

class ParserException(message: String?, val usage: String, cause: Throwable) : Exception(message, cause)

object Runner {
    @JvmStatic
    fun main(args: Array<String>) {
        val (options, usage) = try {
            parseArguments(args)
        } catch (ex: ParserException) {
            System.err.println(ex.message)
            System.err.println(ex.usage)
            return
        }

        if (options.help) {
            println(usage)
            return
        }

        val repository = options.repository

        val extractsFile = options.extracts ?: childFile(repository, ".extracts")
        if (!extractsFile.exists()) {
            System.err.println("Can't find file with extracts at ${extractsFile.canonicalPath}")
            return
        }

        val repositoryDirName = repository.canonicalFile.name

        val logToHtml = try {
            logToHtml(GenerateOptions(
                    repositoryName = repositoryDirName,
                    gitPath = repository.path,
                    extractsFilePath = extractsFile.path,
                    numberOfCommits = options.number,
                    revision = options.revision))
        } catch (cge: ConfigureGitException) {
            System.err.println(cge.message)
            return
        }

        val outputFile = options.output ?: File.createTempFile(repositoryDirName, ".html")
        outputFile.createNewFile()
        outputFile.writeText(logToHtml.htmlText)

        if (options.autoOpen) {
            Desktop.getDesktop().browse(outputFile.toURI())
        }
    }

    private fun childFile(parent: File, child: String): File {
        if (parent.path.isEmpty()) {
            return File(child)
        }

        return File(parent, child)
    }

    data class OptionsParseResult(val options: Options, val usage: String)

    @Throws(exceptionClasses = [(ParserException::class)])
    fun parseArguments(args: Array<String>): OptionsParseResult {
        val options = Options()
        val cmdLineParser = CmdLineParser(options)

        val usage = ByteOutputStream().use { outStream ->
            cmdLineParser.printUsage(outStream)
            outStream.toString()
        }

        try {
            cmdLineParser.parseArgument(*args)
            return OptionsParseResult(options, usage)
        } catch (ex: CmdLineException) {
            throw ParserException(ex.message, usage, ex)
        }
    }
}