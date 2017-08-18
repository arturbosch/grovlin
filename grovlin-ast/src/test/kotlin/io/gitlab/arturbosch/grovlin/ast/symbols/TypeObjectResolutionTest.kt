package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class TypeObjectResolutionTest {

	@Test
	fun cannotCreateTraitObjects() {
		val grovlinFile = """
			trait T
			def main(String args) {
				T()
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is TraitInstantiation }
	}

	@Test
	fun incompatibleDeclaredAndEvaluatedTypes() {
		val grovlinFile = """
			trait B {
				def magicNumber(): Int {
					return 42
				}
			}
			object TheB as B
			trait A {
				B b
			}
			object O as A {
				override B b = TheB().magicNumber()
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is IncompatibleDeclaredAndEvaluatedPropertyType }
	}

	@Test
	fun chainAccessOnObjectCreation() {
		val grovlinFile = """
			object B {
				def magicNumber(): Int {
					return 42
				}
			}
			object O {
				B b = B()
			}
			def main(String args) {
				val x = O().b.magicNumber()
			}
		""".asGrovlinFile().resolved()

		val xVar = grovlinFile.findMethodByName("main")?.findVariableByName("x")

		Assertions.assertThat(xVar?.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun chainAccessInVariables() {
		val grovlinFile = """
			object A {
				Int i = 5
			}
			object B {
				A a = A()
				def magicNumber(): Int {
					return 42
				}
			}
			object O {
				B b = B()
			}
			def main(String args) {
				val o = O()
				val x = o.b.magicNumber()
				o.b.a.i = x
				println(o.b.a.i)
			}
		""".asGrovlinFile().resolved()

		val xVar = grovlinFile.findMethodByName("main")?.findVariableByName("x")

		Assertions.assertThat(xVar?.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun chainAccessInVariableThroughTrait() {
		val grovlinFile = """
			trait T {
				def sayHello(): String {
					return "Hello"
				}
			}
			object A as T
			object B {
				A a = A()
			}
			object O {
				B b = B()
			}
			def main(String args) {
				val x = O().b.a.sayHello()
			}
		""".asGrovlinFile().resolved()

		val xVar = grovlinFile.findMethodByName("main")?.findVariableByName("x")

		Assertions.assertThat(xVar?.evaluationType).isEqualTo(StringType)
	}
}
