package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class PrintIsCallTest {

	@Test
	fun printTokensAreCallTokens() {
		val tokens = "print(5)".tokenize()

		Assertions.assertThat(tokens).contains("ID", "LPAREN", "INTLIT", "RPAREN")
	}

	@Test
	fun printIsCallExpression() {
		val expr = "print(5)".parseExpression()

		Assertions.assertThat(expr).isInstanceOf(GrovlinParser.CallExpressionContext::class.java)
	}
}
