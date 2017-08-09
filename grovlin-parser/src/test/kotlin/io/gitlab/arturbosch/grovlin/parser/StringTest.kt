package io.gitlab.arturbosch.grovlin.parser

import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */
class StringTest {

	@Test
	fun test() {
		val code = "var s = \" ab\nc\\\\de \""

		val actual = tokens(code)
		val expected = listOf("VAR", "ID", "ASSIGN", "STRINGLIT")

		assertEquals(actual, expected)
	}

	@Test
	fun parseEmptyStrings() {
		val code = """var s = ", """"

		val actual = tokens(code)
		val expected = listOf("VAR", "ID", "ASSIGN", "STRINGLIT")

		assertEquals(actual, expected)
	}
}
