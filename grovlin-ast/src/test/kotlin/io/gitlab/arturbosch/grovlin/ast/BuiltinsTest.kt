package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.ReadLine
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class BuiltinsTest {

	@Test
	fun printPrintlnReadLineAreBuiltins() {
		val grovlinFile = """
			print(5)
			println(true)
			readline()
		""".asGrovlinFile()

		Assertions.assertThat(grovlinFile.findByType<Print>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<PrintLn>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<ReadLine>()).isNotNull()
	}
}
