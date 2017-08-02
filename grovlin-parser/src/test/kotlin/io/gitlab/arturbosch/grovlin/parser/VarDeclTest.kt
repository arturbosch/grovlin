package io.gitlab.arturbosch.grovlin.parser

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class VarDeclTest {

	@Test
	fun mixVarDeclsAndAssignsOnSameLine() {
		val statements = "var x = 5 var y = 10 var z = 0 z = 45".parse().root?.statements()?.statement()
		Assertions.assertThat(statements).hasSize(4)
	}
}
