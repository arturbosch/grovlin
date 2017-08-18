package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
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
}
