package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Point
import io.gitlab.arturbosch.grovlin.parser.Error

/**
 * @author Artur Bosch
 */
data class SemanticError(override val message: String, override val position: Point?) : Error
