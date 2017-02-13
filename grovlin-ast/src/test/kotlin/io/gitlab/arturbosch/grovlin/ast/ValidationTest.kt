package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.validation.validate
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ValidationTest {

	@Test
	fun noDoubleProgramStatements() {
		val grovlinFile = parseFromTestResource("DoubleProgramStatements.grovlin")
		val errors = grovlinFile.validate()

		assert(errors.isNotEmpty())
		assert(errors[0].message == "Only one program statement is allowed inside a file.")
	}

	@Test
	fun varDeclarationsBeforeReferences() {
		val grovlinFile = parseFromTestResource("ReferenceBeforeVarDeclaration.grovlin")
		val errors = grovlinFile.validate()

		assert(errors.size == 2) // x, y are referenced before declaration
	}
}