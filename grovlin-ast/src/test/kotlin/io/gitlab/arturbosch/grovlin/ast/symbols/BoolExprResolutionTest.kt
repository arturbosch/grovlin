package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class BoolExprResolutionTest {

	@Test
	fun boolEqualsIntTypeError() {
		val grovlinFile = """if 5 == true { }""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is IncompatibleTypes }
	}

	@Test
	fun userTypeAndStringTypeError() {
		val grovlinFile = """
			object A
			var a = A()
			if a == "" { }
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is IncompatibleTypes }
	}
}
