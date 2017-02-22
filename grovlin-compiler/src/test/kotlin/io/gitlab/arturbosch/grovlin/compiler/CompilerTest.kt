package io.gitlab.arturbosch.grovlin.compiler

import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.IfStmt
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
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
		val cUnit = file.toJava()
		val clazz = cUnit.mainClass
		assertThat(clazz.nameAsString, equalTo("ProgramGv"))
	}

	@Test
	fun parseProgramWithMethods() {
		File("./out").mkdir()
		val file = parseFromTestResource("programWithMethods.grovlin")
		val cUnit = file.toJava()
		val clazz = cUnit.mainClass
		assertThat(clazz, present())
		assertThat(clazz.methods, hasSize(equalTo(3))) // 2 defs + main
	}

	@Test
	fun parseProgramWithBooleans() {
		File("./out").mkdir()
		val file = parseFromTestResource("Booleans.grovlin")
		val cUnit = file.toJava()
		val clazz = cUnit.mainClass
		assertThat(clazz.getNodesByType(VariableDeclarationExpr::class.java), hasSize(equalTo(1)))
	}

	@Test
	fun parseProgramWithIfElifElse() {
		File("./out").mkdir()
		val file = parseFromTestResource("IfElifElse.grovlin")
		val cUnit = file.toJava()
		val clazz = cUnit.mainClass
		val ifs = clazz.getNodesByType(IfStmt::class.java)
		assertThat(ifs, hasSize(equalTo(3)))
	}

	@Test
	fun parseProgramWithRelationalOperators() {
		File("./out").mkdir()
		val file = parseFromTestResource("RelationalOperators.grovlin")
		val cUnit = file.toJava()
		val clazz = cUnit.mainClass
		println(clazz)
		val expressions = clazz.getNodesByType(BinaryExpr::class.java)
		assertThat(expressions, hasSize(equalTo(11)))
	}

	@Test
	fun parseProgramStatementWithinTypeDeclaration() {

	}

}