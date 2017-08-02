package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ReturnTest {

	@Test
	fun simpleReturnStmt() {
		val returnStmt = "return 5".parseStatement() as GrovlinParser.ReturnStatementContext

		Assertions.assertThat(returnStmt.returnStmt().expression())
				.isInstanceOf(GrovlinParser.IntLiteralContext::class.java)
	}
}
