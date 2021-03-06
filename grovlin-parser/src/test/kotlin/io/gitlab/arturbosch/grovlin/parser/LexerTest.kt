package io.gitlab.arturbosch.grovlin.parser

import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */

class LexerTest {

	@Test
	fun parseTypeDeclarations() {
		val actual = tokens("trait Node {}")
		val expected = listOf("TRAIT", "TYPEID", "LBRACE", "RBRACE")

		assertEquals(actual, expected)
	}

	@Test
	fun parsesVarDeclaration() {
		val actual = tokens("val a = 5")
		val expected = listOf("VAL", "ID", "ASSIGN", "INTLIT")

		assertEquals(actual, expected)
	}

	@Test
	fun parsesComplexVarDeclaration() {
		val actual = tokens("val a = (5 * 5) + (1 + (8 - (4 / 2)))")
		val expected = listOf("VAL", "ID", "ASSIGN", "LPAREN", "INTLIT", "MUL", "INTLIT", "RPAREN", "PLUS",
				"LPAREN", "INTLIT", "PLUS", "LPAREN", "INTLIT", "MINUS", "LPAREN", "INTLIT", "DIV", "INTLIT",
				"RPAREN", "RPAREN", "RPAREN")

		assertEquals(actual, expected)
	}

	@Test
	fun parsesDecimalAndIDs() {
		val actual = tokens("a - 5.12")
		val expected = listOf("ID", "MINUS", "DECLIT")

		assertEquals(actual, expected)
	}
}
