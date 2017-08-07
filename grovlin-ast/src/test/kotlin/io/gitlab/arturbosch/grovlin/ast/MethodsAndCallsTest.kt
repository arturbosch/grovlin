package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class MethodsAndCallsTest {

	@Test
	fun parseThisMethodCalls() {
		val grovlinFile = "def method() {}\nthis.method()\nmethod()".asGrovlinFile()

		val calls = grovlinFile.collectByType<CallExpression>()

		assertThat(calls[0].name, equalTo("method"))
		assertThat(calls[1].name, equalTo("method"))
	}

	@Test
	fun parseMethodWithParametersAndArguments() {
		val grovlinFile = """
			def method(Int i, String s): Boolean {return true}
			def main() { method(5, "abc") }
		""".asGrovlinFile()

		val parameters = grovlinFile.collectByType<ParameterDeclaration>()
		val arguments = grovlinFile.findByType<CallExpression>()?.arguments

		Assertions.assertThat(parameters).hasSize(2)
		Assertions.assertThat(arguments).hasSize(2)
	}
}
