package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class CallExpressionsResolutionTest {

	@Test
	fun absentParametersObjectCreation() {
		val grovlinFile = """
			object O {
				def hello(): String {
					return "Hello World!"
				}
			}
			def main(String args) {
				val result = O().hello()
				println(result)
			}
		""".asGrovlinFile().resolved()

		val helloMethod = grovlinFile.findObjectByName("O")?.findMethodByName("hello")
		val variable = grovlinFile.findMethodByName("main")?.findVariableByName("result")

		Assertions.assertThat(grovlinFile.errors).isEmpty()
		Assertions.assertThat(variable?.value?.symbol?.def).isEqualTo(helloMethod)
	}

	@Test
	fun absentScopeWithParameters() {
		val grovlinFile = """
			def hello(Int i, String s): String {
				return "Hello World!" + i + s
			}
			def main(String args) {
				val result = hello(5, "x")
				println(result)
			}
		""".asGrovlinFile().resolved()

		val helloMethod = grovlinFile.findMethodByName("hello")
		val variable = grovlinFile.findMethodByName("main")?.findVariableByName("result")
		val helloCall = variable?.findByType<CallExpression>()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
		Assertions.assertThat(helloCall?.arguments?.joinToString(",") { it.evaluationType.toString() })
				.isEqualTo(helloMethod?.parameters?.joinToString(",") { it.type.toString() })
	}

	@Test
	fun absentScopeWithParametersWithinObject() {
		val grovlinFile = """
			object O {
				def hello(Int i, String s): String {
					return "Hello World!" + i + s
				}
				def main(String args) {
					val result = hello(5, "x")
					println(result)
				}
			}
		""".asGrovlinFile().resolved()

		val objectDecl = grovlinFile.findObjectByName("O")
		val helloMethod = objectDecl?.findMethodByName("hello")
		val variable = objectDecl?.findMethodByName("main")?.findVariableByName("result")
		val helloCall = variable?.findByType<CallExpression>()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
		Assertions.assertThat(helloCall?.arguments?.joinToString(",") { it.evaluationType.toString() })
				.isEqualTo(helloMethod?.parameters?.joinToString(",") { it.type.toString() })
	}
}
