package io.gitlab.arturbosch.grovlin.ast.validation

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.Point
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.operations.processNodesOfType
import io.gitlab.arturbosch.grovlin.parser.Error

/**
 * @author Artur Bosch
 */

data class SemanticError(override val message: String, override val position: Point?) : Error

fun GrovlinFile.validate(): List<SemanticError> {
	val errors = mutableListOf<SemanticError>()
	validateProgram(errors)
	return errors
}

private fun GrovlinFile.validateProgram(errors: MutableList<SemanticError>) {
	val programs = mutableListOf<Program>()
	processNodesOfType<Program> {
		programs.add(it)
		it.validate(errors)
	}
	if (programs.size > 1) programs.drop(1).forEach {
		errors.add(SemanticError("Only one program statement is allowed inside a file.", it.position!!.start))
	}
}

private fun Program.validate(errors: MutableList<SemanticError>) {
	validateVariables(errors)
}

