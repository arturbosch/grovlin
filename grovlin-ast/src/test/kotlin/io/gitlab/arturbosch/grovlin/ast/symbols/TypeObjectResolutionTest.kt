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
	fun illegalPropertyInitializationInTrait() {
		val grovlinFile = """
			trait Named {
				String name = "NAME"
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is IllegalPropertyInitialization }
	}

	@Test
	fun canOverrideTraitProperty() {
		val grovlinFile = """
			trait Named {
				String name
			}
			trait SuperNamed extends Named {
				override String name
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
	}

	@Test
	fun canOverrideTraitPropertyMustContainOverride() {
		val grovlinFile = """
			trait Named {
				String name
			}
			trait SuperNamed extends Named {
				String name
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is MissingOverrideKeyword }
	}

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

		Assertions.assertThat(grovlinFile.errors).hasSize(1)
		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is MissingOverrideKeyword }
	}

	@Test
	fun mustOverrideBothInheritedProperty() {
		val grovlinFile = """
			trait WithParent {
				Node parent
			}
			trait Named {
				String name
			}
			object Node as Named, WithParent {
				String name
				Node parent
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).hasSize(2)
		Assertions.assertThat(grovlinFile.errors).allSatisfy { it is MissingOverrideKeyword }
	}

	@Test
	fun mustOverridePropertiesFromInheritanceHierarchy() {
		val grovlinFile = """
			trait WithParent {
				Node parent
			}
			trait Named {
				String name
			}
			trait NamedWithParent extends WithParent, Named
			object Node as NamedWithParent {
				String name
				Node parent
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).hasSize(2)
		Assertions.assertThat(grovlinFile.errors).allSatisfy { it is MissingOverrideKeyword }
	}

	@Test
	fun mustOverrideAllInheritedProperties() {
		val grovlinFile = """
			trait NodeWithType {
				Type type
			}
			trait Type extends Named
			trait Named {
				String name
			}
			object IntType as Type {
				override String name = "Int"
			}
			object Node as Named {
				override String name
			}
			object Ast extends Node as NodeWithType, Named {
				override String name = "Car"
				override Type type = IntType()
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
	}

	@Test
	fun allThreeOverrideErrors() {
		val grovlinFile = """
			trait NodeWithType {
				Type type
			}
			trait Type extends Named
			trait Named {
				String name
			}
			object Ast extends Node as NodeWithType, Named {
				String name = "Car"
				override Bool nothingToOverride
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is MissingOverrideKeyword }
		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is PropertyNotOverridden }
		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is OverridesNothing }
	}
}

interface A {
	val name: String
}

interface B : A {
	override val name: String
}
