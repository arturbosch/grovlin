package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.compiler.args.Args
import io.gitlab.arturbosch.grovlin.compiler.args.failWithErrorMessage
import io.gitlab.arturbosch.grovlin.compiler.args.jCommander
import io.gitlab.arturbosch.grovlin.compiler.args.parseArguments
import io.gitlab.arturbosch.grovlin.compiler.java.toFile
import io.gitlab.arturbosch.grovlin.compiler.java.toJava
import io.gitlab.arturbosch.grovlin.compiler.parser.Parser
import java.io.File
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {

	val arguments = parseArguments(args)

	val parsingResult = Parser.parse(arguments.input!!)

	if (!parsingResult.isValid()) {
		parsingResult.errors.forEach { println(" * L${it.position.line}: ${it.message}") }
		return
	}

	val grovlinFile = parsingResult.root!!

	if (arguments.showTree) {
		CompletableFuture.runAsync { println(grovlinFile.asString()) }
	}

	if (arguments.mode == "compile") {
		runCompiler(grovlinFile)
	} else if (arguments.mode == "run") {
		runJvm(grovlinFile)
	}

}

private fun runJvm(grovlinFile: GrovlinFile) {
	val tempFile = Files.createTempDirectory("grovlin_run")
	val cUnit = grovlinFile.toJava()
	cUnit.toFile(tempFile.toFile())
	val process = ProcessBuilder("java", "-classpath", tempFile.toString(), cUnit.fileName).start()
	process.waitFor()
	println(String(process.inputStream.buffered().readBytes()))
	println(String(process.errorStream.buffered().readBytes()))
}

private fun runCompiler(grovlinFile: GrovlinFile) {
	if (Args.output == null) jCommander.failWithErrorMessage("For compilation specify an output path!")
	val output: File = Args.output!!.toFile()
	if (!output.exists()) output.mkdir()
	grovlinFile.toJava().toFile(output)
}