package io.gitlab.arturbosch.grovlin.compiler

import org.junit.Test

/**
 * @author Artur Bosch
 */
class LoopsTest {

	@Test
	fun forLoop() {
		runFromResource("Loops.grovlin")
	}
}
