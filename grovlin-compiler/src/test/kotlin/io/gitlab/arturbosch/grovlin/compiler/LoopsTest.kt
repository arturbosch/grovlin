package io.gitlab.arturbosch.grovlin.compiler

import org.junit.Test

/**
 * @author Artur Bosch
 */
class LoopsTest {

	@Test
	fun forLoop() {
//		val stmt = JavaParser.parseStatement("for (int i = 0; i < 10; i++) {}") as ForStmt
//		stmt.initialization.forEach { println(it.javaClass) }
//		stmt.update.forEach { println(it.javaClass) }
//		println(stmt.compare.javaClass)
//		println(stmt.toString())
		runFromResource("Loops.grovlin")
	}
}
