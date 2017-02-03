package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.parser.parseFromResource
import org.junit.Test
import java.io.File

/**
 * @author Artur Bosch
 */
class CompilerTest {

	@Test
	fun parseProgram() {
		val file = parseFromResource("program.grovlin")
		println(file.print())
		val unit = file.toJava()
		println(unit.toString())
		unit.toFile(File("./out"), File("./out/ProgramGrovlin.java"))
	}

}