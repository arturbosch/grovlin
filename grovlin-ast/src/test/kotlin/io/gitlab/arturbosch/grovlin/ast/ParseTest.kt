package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.resolution.resolveSymbols
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class ParseTest {

	@Test
	fun parseFromResource() {
		val file = parseFromTestResource("example.grovlin")

		assertEquals(file.statements.size, 3)
		assertTrue(file.statements[0] is TypeDeclaration)
		assertTrue(file.statements[1] is VarDeclaration)
		assertTrue(file.statements[2] is Print)
	}

	@Test
	fun resolveVariableInPrintStatement() {
		val file = parseFromTestResource("example.grovlin")

		file.resolveSymbols()

		val varReference = file.collectByType<VarReference>().find { it.reference.name == "a" }
		assert(varReference!!.reference.referred!!.name == "a")
		assert(varReference.reference.referred is VarDeclaration)
	}

}