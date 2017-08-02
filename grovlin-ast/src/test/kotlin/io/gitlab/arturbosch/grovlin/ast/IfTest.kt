package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class IfTest {

	@Test
	fun parseEasyIf() {
		val grovlinFile = "if true { print(5) }".asGrovlinFile()

		val ifStatement = grovlinFile.collectByType<IfStatement>()[0]

		assertTrue(ifStatement.condition is BoolLit)
		assertTrue(ifStatement.elseStatement == null)
		assertTrue(ifStatement.thenStatement.statements.size == 1)
	}

	@Test
	fun parseIfElse() {
		val grovlinFile = "if true { print(5) } else {}".asGrovlinFile()

		val ifStatement = grovlinFile.collectByType<IfStatement>()[0]

		assertThat(ifStatement.elseStatement, present())
		assertTrue(ifStatement.thenStatement.statements.size == 1)
	}

	@Test
	fun parseIfElifElifElse() {
		val grovlinFile = "if true { print(5) } elif true {} elif false {} else {}".asGrovlinFile()

		val ifStatement = grovlinFile.collectByType<IfStatement>()[0]

		assertThat(ifStatement.elseStatement, present())
		assertThat(ifStatement.elifs, hasSize(equalTo(2)))
		assertTrue(ifStatement.thenStatement.statements.size == 1)
	}

	@Test
	fun parseNestedIfs() {
		val grovlinFile = "if true { if true { if true {}} } elif true { if true {} elif false {}} else {}"
				.asGrovlinFile()

		val ifStatements = grovlinFile.collectByType<IfStatement>()

		assertThat(ifStatements, hasSize(equalTo(4)))
	}


}
