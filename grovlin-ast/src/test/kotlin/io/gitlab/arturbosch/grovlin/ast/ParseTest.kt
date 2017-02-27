package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.asString
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
		println(file.asString())
		assertEquals(file.statements.size, 2)
		assertTrue(file.statements[0] is TypeDeclaration)
		assertTrue(file.statements[1] is Program)
	}

}