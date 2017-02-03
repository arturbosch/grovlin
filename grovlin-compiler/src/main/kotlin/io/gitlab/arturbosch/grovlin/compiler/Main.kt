package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.parse
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {

	if (args.size < 2) throw IllegalArgumentException("Usage: [compile|run] path/to/grovlin/file [path/to/java/class]?")

	val type = args[0]
	val input = args[1]

	val grovlinFile = Paths.get(input).parse()
	if (type == "compile") {
		runCompiler(args, grovlinFile)
	} else if (type == "run") {
		runJvm(grovlinFile)
	}

}

private fun runJvm(grovlinFile: GrovlinFile) {
	val tempFile = Files.createTempDirectory("grovlin_run")
	grovlinFile.toJava().toFile(tempFile.toFile())
	val process = ProcessBuilder("java", "-classpath", tempFile.toString(), "ProgramGrovlin").start()
	process.waitFor()
	println(String(process.inputStream.buffered().readBytes()))
	println(String(process.errorStream.buffered().readBytes()))
}

private fun runCompiler(args: Array<String>, grovlinFile: GrovlinFile) {
	if (args.size < 3) throw IllegalArgumentException("Usage: [compile|run] path/to/grovlin/file [path/to/java/class]?")
	val output = File(args[2])
	if (!output.exists()) output.mkdir()
	grovlinFile.toJava().toFile(output)
}