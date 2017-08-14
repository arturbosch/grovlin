package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.identify
import io.gitlab.arturbosch.grovlin.ast.resolve
import io.gitlab.arturbosch.grovlin.compiler.backend.MainMethodNotFound
import io.gitlab.arturbosch.grovlin.compiler.backend.MainMethodRedeclaration
import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import io.gitlab.arturbosch.grovlin.compiler.java.interpret
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * @author Artur Bosch
 */
class MainRedeclarationTest {

	@Test
	fun missingMainMethod() {
		assertFailsWith<MainMethodNotFound> {
			"""
			object O {
				def main(String args) {}
			}
			trait T {
				def main(String args) {}
			}
		""".asGrovlinFile()
					.identify()
					.resolve()
					.asJavaFile()
					.interpret()
		}
	}

	@Test
	fun mainMethodRedeclaration() {
		assertFailsWith<MainMethodRedeclaration> {
			"""
				def main(String args) {}
				def main(String args) {}
		""".asGrovlinFile()
					.identify()
					.resolve()
					.asJavaFile()
					.interpret()
		}
	}
}
