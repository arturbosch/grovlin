package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser
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
	fun declareMethodWithZeroParameterAndReturnType() {
		val stmt = "def main(): String { }".parseMethod()

		Assertions.assertThat(stmt.parameterList()).isNull()
		Assertions.assertThat(stmt.TYPEID()).isNotNull()
		Assertions.assertThat(stmt.TYPEID().text).isEqualTo("String")
	}

	@Test
	fun declareMethodParametersAndReturnType() {
		val stmt = "def main(String a, String b, Int c): Boolean { }".parseMethod()

		Assertions.assertThat(stmt.parameterList().parameter()).hasSize(3)
		Assertions.assertThat(stmt.TYPEID().text).isEqualTo("Boolean")
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

	@Test
	fun argumentWithString() {
		val call = """print(", ")""".parseMethodCall()

		Assertions.assertThat(call.argumentList().argument()).hasSize(1)
		Assertions.assertThat(call.argumentList().argument()[0].expression())
				.isInstanceOf(GrovlinParser.StringLiteralContext::class.java)
	}
}
