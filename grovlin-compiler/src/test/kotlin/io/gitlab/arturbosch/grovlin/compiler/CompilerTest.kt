package io.gitlab.arturbosch.grovlin.compiler

import io.gitlab.arturbosch.grovlin.compiler.java.toFile
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
		println(file.print())
		val unit = file.toJava()
		println(unit.toString())
		unit.toFile(File("./out"), File("./out/ProgramGrovlin.java"))
	}

}