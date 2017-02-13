package io.gitlab.arturbosch.grovlin.compiler.parser

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.toAsT
import io.gitlab.arturbosch.grovlin.ast.validation.validate
import io.gitlab.arturbosch.grovlin.parser.Error
import io.gitlab.arturbosch.grovlin.parser.parse
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
object Parser {

	fun parse(path: Path): ParsingResult {
		val parsingResult = path.parse()
		val root = parsingResult.root
		val syntaxErrors = parsingResult.errors
		val grovlinFile = if (parsingResult.isValid()) root!!.toAsT() else null
		val semanticErrors = grovlinFile?.validate() ?: emptyList()
		return ParsingResult(grovlinFile, path, syntaxErrors + semanticErrors)
	}

}

data class ParsingResult(val root: GrovlinFile?, val path: Path, val errors: List<Error>) {
	fun isValid() = root != null && errors.isEmpty()
}
