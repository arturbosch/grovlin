package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class LoopsTest {

	@Test
	fun `domain tests FOR`() {
		val grovlinFile = "for x : 1..10 {}".asGrovlinFile()

		val forStmt = grovlinFile.collectByType<ForStatement>()[0]

		Assertions.assertThat(forStmt.varDeclaration).isEqualTo("x")
		Assertions.assertThat(forStmt.expression).isInstanceOf(IntRangeExpression::class.java)
		Assertions.assertThat(forStmt.block.statements).isEmpty()
	}

	@Test
	fun `domain tests WHILE`() {
		val grovlinFile = "while 5 < 8 {}".asGrovlinFile()

		val whileStmt = grovlinFile.collectByType<WhileStatement>()[0]

		Assertions.assertThat(whileStmt.condition).isInstanceOf(RelationExpression::class.java)
		Assertions.assertThat(whileStmt.thenStatement.statements).isEmpty()
	}
}
