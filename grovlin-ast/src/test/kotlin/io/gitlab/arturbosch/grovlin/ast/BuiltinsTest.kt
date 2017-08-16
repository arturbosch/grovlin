package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.RandomNumber
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
			rand(5)
		""".asGrovlinFile()

		Assertions.assertThat(grovlinFile.findByType<Print>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<PrintLn>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<ReadLine>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<RandomNumber>()).isNotNull()
	}

	@Test
	fun randReadline() {
		val grovlinFile = """
			def main(String args) {
				println("Guess my number (0-9)!")
				var number = rand(10)
				var input = readline() as Int
				println("My number was " + number + " and your number is " + input)
			}
		""".asGrovlinFile()

		Assertions.assertThat(grovlinFile.errors).isEmpty()
	}
}
