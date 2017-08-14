package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class TypeObjectResolutionTest {

	@Test
	fun mustOverrideProperty() {
		val grovlinFile = """
			trait Named {
				String name
			}
			object Car as Named {
				String name = "Car"
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
		//.anySatisfy { it is MissingOverride }
	}

	@Test
	fun mustOverrideAllInheritedProperties() {
		val grovlinFile = """
			trait NodeWithType {
				Type type
			}
			trait Type extends Named
			trait Named {
				override String name
			}
			object IntType as Type {
				override String name = "Int"
			}
			object Node as Named
			object Ast extends Node as NodeWithType, Named {
				override String name = "Car"
				override Type type = IntType()
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
	}
}
