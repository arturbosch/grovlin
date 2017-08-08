package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.MainDeclaration
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ParseTest {

	@Test
	fun parseFromResource() {
		val file = parseFromTestResource("example.grovlin")

		val block = file.block!!

		Assertions.assertThat(block.statements).hasSize(2)
		Assertions.assertThat(block.statements[0]).isInstanceOf(TypeDeclaration::class.java)
		Assertions.assertThat(block.statements[1]).isInstanceOf(MainDeclaration::class.java)
	}
}
