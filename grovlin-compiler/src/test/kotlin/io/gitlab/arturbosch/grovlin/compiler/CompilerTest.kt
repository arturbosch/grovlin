package io.gitlab.arturbosch.grovlin.compiler

import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.IfStmt
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * @author Artur Bosch
 */
class CompilerTest {

	@Before
	fun createOutDir() {
		File("./out").mkdir()
	}

	@Test
	fun parseProgram() {
		val file = parseFromTestResource("program.grovlin")
		val cUnit = file.asJavaFile().main
		val clazz = cUnit.mainClass
		assertThat(clazz.nameAsString, equalTo("MainGv"))
	}

	@Test
	fun parseProgramWithMethods() {
		val file = parseFromTestResource("programWithMethods.grovlin")
		val cUnit = file.asJavaFile().main
		val clazz = cUnit.mainClass
		assertThat(clazz, present())
		assertThat(clazz.methods, hasSize(equalTo(3))) // 2 defs + main
	}

	@Test
	fun parseProgramWithBooleans() {
		val file = parseFromTestResource("Booleans.grovlin")
		val cUnit = file.asJavaFile().main
		val clazz = cUnit.mainClass
		assertThat(clazz.getChildNodesByType(VariableDeclarationExpr::class.java), hasSize(equalTo(1)))
	}

	@Test
	fun parseProgramWithIfElifElse() {
		val file = parseFromTestResource("IfElifElse.grovlin")
		val cUnit = file.asJavaFile().main
		val clazz = cUnit.mainClass
		val ifs = clazz.getChildNodesByType(IfStmt::class.java)
		assertThat(ifs, hasSize(equalTo(3)))
	}

	@Test
	fun parseProgramWithRelationalOperators() {
		val file = parseFromTestResource("RelationalOperators.grovlin")
		val cUnit = file.asJavaFile().main
		val clazz = cUnit.mainClass
		val expressions = clazz.getChildNodesByType(BinaryExpr::class.java)
		assertThat(expressions, hasSize(equalTo(11)))
	}

	@Test
	fun parseProgramWithObjectAccesses() {
		runFromResource("ObjectAccess.grovlin")
	}
}
