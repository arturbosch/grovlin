package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.parser.ast.process
import org.junit.Test

/**
 * @author Artur Bosch
 */
class PositionsTest {

	@Test
	fun `positions are parsed`() {
		val grovlinFile = parseFromResource("example.grovlin")
		grovlinFile.process {
			assert(it.position != null)
		}
	}
}