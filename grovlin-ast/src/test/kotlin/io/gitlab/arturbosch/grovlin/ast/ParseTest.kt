package io.gitlab.arturbosch.grovlin.ast

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

		val block = file.block!!

		assertEquals(block.statements.size, 2)
		assertTrue(block.statements[0] is TypeDeclaration)
		assertTrue(block.statements[1] is Program)
	}

}