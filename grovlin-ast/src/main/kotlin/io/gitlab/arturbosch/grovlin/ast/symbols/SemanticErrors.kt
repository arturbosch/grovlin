package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.INVALID_POSITION
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.parser.FrontendError

/**
 * @author Artur Bosch
 */
open class SemanticError(val message: String, val position: Position?) : FrontendError {

	override fun formattedMessage() = "$message: ${position ?: INVALID_POSITION}"
	override fun toString(): String = formattedMessage()
}

class MainMethodMissing(fileName: String) : SemanticError("Main method declaration missing in '$fileName'.", null) {

	override fun formattedMessage() = message
}

class IncompatibleArgumentTypes(fileName: String, methodCall: String,
								paramTypes: String, argumentTypes: String,
								positions: Position?)
	: SemanticError("$fileName:$positions: " +
		"Call to '$methodCall' with incompatible types '$argumentTypes', expected '$paramTypes'.", positions) {

	override fun formattedMessage() = message
}

class RedeclarationError(val id: String, vararg val positions: Position?) : SemanticError(id, positions[0]) {

	override fun formattedMessage() = "Redeclaration of '$id': ${positions
			.joinToString(", ") { it.toString() }}"
}
