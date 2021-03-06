package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser
import java.nio.file.Path

/**
 * @author Artur Bosch
 */

data class RawParsingResult(val root: GrovlinParser.GrovlinFileContext?,
							val path: Path?, val errors: List<SyntaxError>) {

	fun isValid() = errors.isEmpty() && root != null
}

interface FrontendError {
	fun formattedMessage(): String
}

data class SyntaxError(val message: String, val position: CodePoint) : FrontendError {

	override fun formattedMessage(): String = "$message:$position"
}

open class CodePoint(val line: Int, val column: Int) {

	override fun toString() = "$line/$column"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other?.javaClass != javaClass) return false

		other as CodePoint

		if (line != other.line) return false
		if (column != other.column) return false

		return true
	}

	override fun hashCode(): Int {
		var result = line
		result = 31 * result + column
		return result
	}
}
