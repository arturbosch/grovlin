package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.junit.Test

/**
 * @author Artur Bosch
 */
class BoolTest {

	@Test
	fun parseBooleans() {
		val grovlinFile = "val b = true\nval b2 = false".asGrovlinFile()

		assertThat(grovlinFile.collectByType<BoolLit>().size, equalTo(2))
	}

	@Test
	fun parseBooleanExpressions() {
		val grovlinFile = "val b = true && false\nval b2 = true || false\nval b3 = true ^ false".asGrovlinFile()

		assertThat(grovlinFile.collectByType<BinaryExpression>().size, equalTo(3))
	}

	@Test
	fun parseComplexBooleanExpression() {
		val grovlinFile = "val b = true && false || true ^ false".asGrovlinFile()
		val orExpression = grovlinFile.collectByType<OrExpression>()[0]

		assertThat(orExpression.left is AndExpression, equalTo(true))
		assertThat(orExpression.right is XorExpression, equalTo(true))
	}

}