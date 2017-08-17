package io.gitlab.arturbosch.grovlin.compiler.backend.builtins

import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.EqualExpression
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class EqualsToJavaKtTest {

	@Test
	fun objectEqualsObject() {
		val equalExpression = EqualExpression(StringLit("hello"), StringLit("world"))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo(""""hello".equals("world")""")
	}

	@Test
	fun intEqualsObject() {
		val equalExpression = EqualExpression(IntLit("5"), StringLit("world"))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo(""""world".equals(5)""")
	}

	@Test
	fun boolEqualsObject() {
		val equalExpression = EqualExpression(IntLit("true"), StringLit("world"))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo(""""world".equals(true)""")
	}

	@Test
	fun objectUnequalsObject() {
		val equalExpression = UnequalExpression(StringLit("hello"), StringLit("world"))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo("""!("hello".equals("world"))""")
	}

	@Test
	fun intUnequalsObject() {
		val equalExpression = UnequalExpression(IntLit("6"), StringLit("world"))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo("""!("world".equals(6))""")
	}

	@Test
	fun intUnequalsBool() {
		val equalExpression = UnequalExpression(IntLit("6"), BoolLit(false))
		val javaEquals = equalExpression.builtinToJava()

		Assertions.assertThat(javaEquals.toString()).isEqualTo("""6 != false""")
	}
}
