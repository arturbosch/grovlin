package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ContextAnalysisEdgeCasesTest {

	@Test
	fun singleMainAllowed() {
		val grovlinFile = """
			def main(String args) {}
			def main(String args) {}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).hasSize(1)
		Assertions.assertThat(grovlinFile.errors[0].message).contains("main")
	}
}
