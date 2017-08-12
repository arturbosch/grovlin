package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class MethodDeclarationTest {

	@Test
	fun signatureWithParametersWithoutReturn() {
		val main = MethodDeclaration("main", null, parameters = mutableListOf(
				ParameterDeclaration("args", StringType)))

		Assertions.assertThat(main.methodParameterSignature).isEqualTo("main(String args)")
		Assertions.assertThat(main.methodSignature).isEqualTo("main(String args)")

		main.parameters.add(ParameterDeclaration("oh", IntType))
		Assertions.assertThat(main.methodParameterSignature).isEqualTo("main(String args, Int oh)")
	}

	@Test
	fun fullSignature() {
		val method = MethodDeclaration("hello", null, ObjectOrTypeType("MyType"), mutableListOf(
				ParameterDeclaration("i", IntType), ParameterDeclaration("d", DecimalType)))

		val methodSignature = method.methodSignature
		val methodParameterSignature = method.methodParameterSignature
		Assertions.assertThat(methodSignature).isEqualTo("hello(Int i, Decimal d): MyType")
		Assertions.assertThat(methodParameterSignature).isEqualTo("hello(Int i, Decimal d)")
	}
}
