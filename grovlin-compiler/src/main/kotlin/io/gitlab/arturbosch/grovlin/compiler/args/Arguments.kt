package io.gitlab.arturbosch.grovlin.compiler.args

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import java.nio.file.Path

object Args {

	@Parameter(
			names = arrayOf("--mode", "-m"),
			description = "Specifies if the compiler should only 'compile' or also 'run' the file.")
	var mode: String = "run"
	@Parameter(
			required = true,
			names = arrayOf("--input", "-i"),
			description = "The input grovlin file.",
			converter = ExistingPathConverter::class)
	var input: Path? = null
	@Parameter(
			names = arrayOf("--output", "-o"),
			description = "The output directory for the java classes.",
			converter = ExistingPathConverter::class)
	var output: Path? = null
	@Parameter(
			names = arrayOf("--show-tree", "-st"),
			description = "Prints the grovlin file tree.")
	var showTree = false
	@Parameter(
			names = arrayOf("--help", "-h"),
			help = true,
			description = "Prints the help message.")
	var help = false

}

val jCommander = JCommander()

fun parseArguments(args: Array<String>): Args {
	jCommander.setProgramName("grovlin-compiler")
	jCommander.addObject(Args)

	try {
		jCommander.parse(*args)
	} catch (ex: ParameterException) {
		jCommander.failWithErrorMessage(ex.message)
	}

	if (Args.help) {
		jCommander.usage()
		System.exit(-1)
	}

	return Args
}

fun JCommander.failWithErrorMessage(message: String?) {
	println(message)
	println()
	usage()
	System.exit(-1)
}
