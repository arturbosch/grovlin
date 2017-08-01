package io.gitlab.arturbosch.grovlin.parser

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ForTest {

	val code = "for x : 1..10 {}"

	@Test
	fun lexer() {
		val actual = code.tokenize()
		val expected = listOf("FOR", "ID", "SEMICOLON", "INTLIT", "POINT", "POINT", "INTLIT", "LBRACE", "RBRACE")
		assertThat(actual, equalTo(expected))
	}

	@Test
	fun parser() {
		val statements = code.parse().root?.statements()
		val forStmt = statements?.statement()?.get(0)!!
		Assertions.assertThat(forStmt).isInstanceOf(GrovlinParser.ForStatementContext::class.java)
		val rangeExpr = (forStmt as GrovlinParser.ForStatementContext).forStmt().expression()
		Assertions.assertThat(rangeExpr).isInstanceOf(GrovlinParser.IntRangeExpressionContext::class.java)
	}
}
