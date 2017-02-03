package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.parser.ast.Print
import io.gitlab.arturbosch.grovlin.parser.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.VarDeclaration
import org.junit.Test
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class ParserTest {

	@Test
	fun parseFromResource() {
		val file = parse("example.grovlin")

		assertEquals(file.statement.size, 3)
		assertTrue(file.statement[0] is TypeDeclaration)
		assertTrue(file.statement[1] is VarDeclaration)
		assertTrue(file.statement[2] is Print)
	}

	@Test
	fun parseFromPath() {
		val path = Paths.get(javaClass.getResource("/example.grovlin").path)
		val file = path.parse()

		assertEquals(file.statement.size, 3)
		assertTrue(file.statement[0] is TypeDeclaration)
		assertTrue(file.statement[1] is VarDeclaration)
		assertTrue(file.statement[2] is Print)
	}

}