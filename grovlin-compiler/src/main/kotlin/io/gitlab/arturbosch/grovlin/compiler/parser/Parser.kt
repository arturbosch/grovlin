package io.gitlab.arturbosch.grovlin.compiler.parser

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.identify
import io.gitlab.arturbosch.grovlin.ast.resolve
import io.gitlab.arturbosch.grovlin.ast.visitors.asGrovlinFile
import io.gitlab.arturbosch.grovlin.parser.Error
import io.gitlab.arturbosch.grovlin.parser.parse
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
object Parser {

	fun parse(path: Path): ParsingResult {
		val fileName = path.fileName.toString()
		if (!fileName.endsWith(".gv") && !fileName.endsWith(".grovlin")) {
			throw IllegalArgumentException("Only grovlin files should be parsed!")
		}
		val parsingResult = path.parse()
		val root = parsingResult.root
		val syntaxErrors = parsingResult.errors
		val grovlinFile = if (parsingResult.isValid()) {
			root!!.asGrovlinFile(fileName.substring(0, fileName.lastIndexOf(".")))
		} else {
			null
		}

		if (syntaxErrors.isNotEmpty()) return ParsingResult(grovlinFile, path, syntaxErrors)

		grovlinFile?.identify()?.resolve()

		val semanticErrors = grovlinFile?.errors ?: mutableListOf()
		return ParsingResult(grovlinFile, path, semanticErrors)
	}

}

data class ParsingResult(val root: GrovlinFile?, val path: Path, val errors: List<Error>) {
	fun isValid() = root != null && errors.isEmpty()
}
