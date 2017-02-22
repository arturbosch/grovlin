package io.gitlab.arturbosch.grovlin.compiler.parser

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.resolution.resolveSymbols
import io.gitlab.arturbosch.grovlin.ast.resolution.resolveTypes
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
		val fileName = path.fileName.toString()
		if (!fileName.endsWith(".gv") && !fileName.endsWith(".grovlin")) {
			throw IllegalArgumentException("Only grovlin files should be parsed!")
		}
		val parsingResult = path.parse()
		val root = parsingResult.root
		val syntaxErrors = parsingResult.errors
		val grovlinFile = if (parsingResult.isValid()) root!!.toAsT(fileName.substring(0, fileName.lastIndexOf("."))) else null
		val semanticErrors = grovlinFile?.validate() ?: emptyList()
		grovlinFile?.resolveSymbols()
		grovlinFile?.resolveTypes()
		return ParsingResult(grovlinFile, path, syntaxErrors + semanticErrors)
	}

}

data class ParsingResult(val root: GrovlinFile?, val path: Path, val errors: List<Error>) {
	fun isValid() = root != null && errors.isEmpty()
}
