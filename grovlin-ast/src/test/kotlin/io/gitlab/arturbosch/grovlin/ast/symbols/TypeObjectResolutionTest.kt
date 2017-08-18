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
	fun cannotCreateTraitObjects() {
		val grovlinFile = """
			trait T
			def main(String args) {
				T()
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is TraitInstantiation }
	}
}
