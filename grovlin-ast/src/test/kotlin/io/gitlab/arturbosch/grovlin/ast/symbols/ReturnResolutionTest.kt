package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ReturnResolutionTest {

	@Test
	fun singleReturnAllowed() {
		val grovlinFile = """
			def test(): Boolean {
				if 5 > 1 {
					return true
				}
				return false
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is SingleReturnAllowed }
	}

	@Test
	fun returnEvaluationTypeMustMeetMethodReturnType() {
		val grovlinFile = """
			def test(): Int {
				var b = false
				if 5 > 1 {
					b = true
				}
				return b
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is IncompatibleReturnType }
	}

	@Test
	fun returnMustBeLastStatement() {
		val grovlinFile = """
			def test(): Boolean {
				if 5 > 1 {
					return true
				}
			}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).anySatisfy { it is ReturnMustBeLast }
	}
}
