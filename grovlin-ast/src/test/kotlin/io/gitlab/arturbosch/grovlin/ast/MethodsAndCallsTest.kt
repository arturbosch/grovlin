package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
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
}