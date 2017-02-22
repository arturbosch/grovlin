package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.junit.Test

/**
 * @author Artur Bosch
 */
class RelationalOperatorsTest {

	@Test
	fun mixOfOperators() {
		val grovlinFile = "if (5 != 4 && 4 == 4 && 5 > 4 && 4 < 5 && 4 <= 5 && 5 >= 4) { print(-1)} ".asGrovlinFile()

		assertThat(grovlinFile.collectByType<BinaryExpression>(), hasSize(equalTo(11)))
	}
}