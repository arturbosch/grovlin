package io.gitlab.arturbosch.grovlin.parser

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class WhileTest {

	val code = """
		while true {

		}
	"""

	@Test
	fun lexer() {
		val actual = code.tokenize().filterNot { it == "NL" }
		val expected = listOf("WHILE", "BOOLLIT", "LBRACE", "RBRACE")
		assertThat(actual, equalTo(expected))
	}

	@Test
	fun parser() {
		val statements = code.parse().root?.statements()
		val whileStmt = statements?.statement()?.get(0)!!
		Assertions.assertThat(whileStmt).isInstanceOf(GrovlinParser.WhileStatementContext::class.java)
	}
}


