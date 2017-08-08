package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.process
import org.junit.Test

/**
 * @author Artur Bosch
 */
class PositionsTest {

	@Test
	fun `positions are parsed`() {
		val grovlinFile = parseFromTestResource("example.grovlin")
		grovlinFile.process {
			assert(it.position != null) {
				"${it.javaClass.simpleName} has no positions, parent was ${it.parent?.javaClass?.simpleName}"
			}
			println(it.javaClass.simpleName)
		}
	}

}
