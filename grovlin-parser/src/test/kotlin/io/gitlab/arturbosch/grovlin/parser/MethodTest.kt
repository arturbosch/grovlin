package io.gitlab.arturbosch.grovlin.parser

import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class MethodTest {

	@Test
	fun declareMethodWithoutParameter() {
		val stmt = "def main() { }".parseMethod()

		Assertions.assertThat(stmt.parameterList()).isNull()
	}

	@Test
	fun declareMethodWithOneParameter() {
		val stmt = "def main(String arg) { }".parseMethod()

		Assertions.assertThat(stmt.parameterList().parameter()).hasSize(1)
	}

	@Test
	fun declareMethodWithTwoParameter() {
		val stmt = "def main(String arg, String arg2) { }".parseMethod()

		Assertions.assertThat(stmt.parameterList().parameter()).hasSize(2)
	}

	@Test
	fun methodCallZeroArguments() {
		val zeroArgs = "main()".parseMethodCall()
		val oneArgs = "main(a)".parseMethodCall()
		val twoArgs = "main(a, b)".parseMethodCall()

		Assertions.assertThat(zeroArgs.methodName.text).isEqualTo("main")
		Assertions.assertThat(zeroArgs.argumentList()).isNull()
		Assertions.assertThat(oneArgs.argumentList().argument()).hasSize(1)
		Assertions.assertThat(twoArgs.argumentList().argument()).hasSize(2)
	}
}
