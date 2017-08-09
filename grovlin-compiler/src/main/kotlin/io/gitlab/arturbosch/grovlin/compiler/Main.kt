package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.compiler.args.Args
import io.gitlab.arturbosch.grovlin.compiler.args.failWithErrorMessage
import io.gitlab.arturbosch.grovlin.compiler.args.jCommander
import io.gitlab.arturbosch.grovlin.compiler.args.parseArguments
import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import io.gitlab.arturbosch.grovlin.compiler.frontend.Parser
import io.gitlab.arturbosch.grovlin.compiler.java.interpret
import io.gitlab.arturbosch.grovlin.compiler.java.writeToDisk
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {

	val arguments = parseArguments(args)

	val parsingResult = Parser.parse(arguments.input!!)

	if (!parsingResult.isValid()) {
		parsingResult.errors.forEach { println(" * L${it.position?.line}: ${it.message}") }
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
	val java = grovlinFile.asJavaFile()
	if (Args.showTree) {
		CompletableFuture.runAsync { java.all().forEach { println(it.unit) } }
	}
	java.interpret()
}

private fun runCompiler(grovlinFile: GrovlinFile) {
	if (Args.output == null) jCommander.failWithErrorMessage("For compilation specify an output path!")
	val outputFile: File = Args.output!!.toFile()
	if (!outputFile.exists()) outputFile.mkdir()
	val javaFile = grovlinFile.asJavaFile()
	writeToDisk(outputFile, javaFile)
}
