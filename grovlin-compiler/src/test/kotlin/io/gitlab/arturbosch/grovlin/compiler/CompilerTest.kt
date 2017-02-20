package io.gitlab.arturbosch.grovlin.compiler

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.compiler.java.toJava
import org.junit.Test
import java.io.File

/**
 * @author Artur Bosch
 */
class CompilerTest {

	@Test
	fun parseProgram() {
		File("./out").mkdir()
		val file = parseFromTestResource("program.grovlin")
		println(file.asString())
		val unit = file.toJava()
		println(unit.toString())
		assertThat(unit.getClassByName("ProgramGv").isPresent, equalTo(true))
	}

	@Test
	fun parseProgramWithMethods() {
		File("./out").mkdir()
		val file = parseFromTestResource("programWithMethods.grovlin")
		println(file.asString())
		val unit = file.toJava()
		println(unit.toString())
		val clazz = unit.getClassByName("ProgramWithMethodsGv").get()
		assertThat(clazz, present())
		assertThat(clazz.methods, hasSize(equalTo(3))) // 2 defs + main

	}

	@Test
	fun parseProgramStatementWithinTypeDeclaration() {

	}

}