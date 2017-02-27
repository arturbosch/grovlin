package io.gitlab.arturbosch.grovlin.compiler

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.ast.toAsT
import io.gitlab.arturbosch.grovlin.compiler.java.toJava
import io.gitlab.arturbosch.grovlin.parser.parse
import org.junit.Test

/**
 * @author Artur Bosch
 */
class CompileProgramWithTypesAndObjectsTest {

	@Test
	fun threeTypesOneObject() {
		val grovlinFile = parseFromTestResource("TypesAndObjects.grovlin")
		println(grovlinFile.asString())
		val cUnit = grovlinFile.toJava()
		val clazz = cUnit.mainClass
		println(clazz)
		cUnit.additionalUnits.forEach(::println)
		assertThat(clazz, present())
		assertThat(cUnit.additionalUnits, hasSize(equalTo(5)))
	}


	@Test
	fun correctResolutionOfPropertyGetter() {
		val grovlinFile = "type Box { Int data }\nobject BoxImpl as Box { override Int data }\nprogram { print(BoxImpl().data) }".parse()
				.root!!.toAsT()
		println(grovlinFile.asString())
		val java = grovlinFile.toJava()
		java.additionalUnits.forEach(::println)
		println(java.unit)
	}

}