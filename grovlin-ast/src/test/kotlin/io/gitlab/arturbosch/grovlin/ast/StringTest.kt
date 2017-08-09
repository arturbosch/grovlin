package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class StringTest {

	@Test
	fun emptyStringWithoutQuotationMarks() {
		val grovlinFile = """
			print("")
		""".asGrovlinFile()

		val stringLit = grovlinFile.findByType<StringLit>()

		Assertions.assertThat(stringLit?.value).isEmpty()

	}

	@Test
	fun twoCharContent() {
		val grovlinFile = """
			print(", ")
		""".asGrovlinFile()

		val stringLit = grovlinFile.findByType<StringLit>()

		Assertions.assertThat(stringLit?.value).isEqualTo(", ")

	}

	@Test
	fun oneCharContent() {
		val grovlinFile = """
			print(",")
		""".asGrovlinFile()

		val stringLit = grovlinFile.findByType<StringLit>()

		Assertions.assertThat(stringLit?.value).isEqualTo(",")

	}
}
