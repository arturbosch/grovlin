package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import io.gitlab.arturbosch.grovlin.compiler.java.inmemory.GrovlinRuntimeError
import io.gitlab.arturbosch.grovlin.compiler.java.interpret
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * @author Artur Bosch
 */
class TopLevelTest {

	@Test
	fun topLevelVarDeclIsFinalStaticField() {
		"""
		var x = 5

		def stuff(): Int {
			return x
		}
		def main(String args) {
			println(stuff())
		}
	""".asGrovlinFile()
				.asJavaFile()
				.interpret()
	}

	@Test
	fun topLevelVarDeclCanCallStaticMethods() {
		"""
		var x = stuff()

		def stuff(): Int {
			return 5
		}
		def main(String args) {
			println(x)
		}
	""".asGrovlinFile()
				.asJavaFile()
				.interpret()
	}

	@Test
	fun topLevelValCannotBeOverridden() {
		assertFailsWith<GrovlinRuntimeError> {
			"""
				val x = 5
				def main(String args) {
					x = 7
					println(x)
				}
			""".asGrovlinFile().asJavaFile().interpret()
		}
	}

	@Test
	fun topLevelVarCanBeOverridden() {
		"""
			var x = 5
			def main(String args) {
				x = 7
				println(x)
			}
		""".asGrovlinFile().asJavaFile().interpret()
	}

	@Test
	fun canUseTopLevelDeclarationFromObjects() {
		"""
			var x = 5
			def main(String args) {
				x = 7
				val y = O().returnX()
				println(y)
			}
			object O {
				def returnX(): Int {
					return x
				}
			}
		""".asGrovlinFile().asJavaFile().interpret()
	}
}
