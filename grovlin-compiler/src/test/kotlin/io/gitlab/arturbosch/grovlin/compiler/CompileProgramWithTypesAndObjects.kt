package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.ast.operations.asString
import org.junit.Test

/**
 * @author Artur Bosch
 */
class CompileProgramWithTypesAndObjects {

	@Test
	fun threeTypesOneObject() {
		val grovlinFile = parseFromTestResource("TypesAndObjects.grovlin")
		println(grovlinFile.asString())
	}
}