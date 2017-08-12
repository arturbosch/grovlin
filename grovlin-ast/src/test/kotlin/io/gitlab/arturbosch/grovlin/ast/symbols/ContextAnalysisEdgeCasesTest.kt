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

		Assertions.assertThat(grovlinFile.errors).hasSize(2) // redecl, 2xmain
		Assertions.assertThat(grovlinFile.errors[0].message).contains("main")
	}

	@Test
	fun methodRedeclaration() {
		val grovlinFile = """
			def method(String s, Int i) {}
			def method(String s, Int i) {}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).hasSize(1)
		Assertions.assertThat(grovlinFile.errors[0].message).contains("method(String s, Int i)")
	}

	@Test
	fun sameMethodSignatureAllowedInDifferentScopes() {
		val grovlinFile = """
			object O {
				def method(String s, Int i) {}
			}
			def method(String s, Int i) {}
		""".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors).hasSize(0)
	}
}
