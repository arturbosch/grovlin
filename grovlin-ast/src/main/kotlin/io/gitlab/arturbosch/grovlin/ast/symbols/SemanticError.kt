package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Point
import io.gitlab.arturbosch.grovlin.parser.FrontendError

/**
 * @author Artur Bosch
 */
data class SemanticError(val message: String, val position: Point?) : FrontendError {

	override fun formattedMessage() = "$message:$position"
}
