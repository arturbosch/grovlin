package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class VariableTypePromotionTest {

	@Test
	fun relationalOperationsResultInBoolType() {
		val grovlinFile = "var b = 5 > 4\nvar b2 = 5.0 > 4".asGrovlinFile().resolved()

		val vars = grovlinFile.collectByType<VarDeclaration>()

		Assertions.assertThat(vars).allMatch { it.type == BoolType }
		Assertions.assertThat(vars).allMatch { it.evaluationType == BoolType }
	}

	@Test
	fun relationsOnStrings() {
		val grovlinFile = """
			var b = "Hello World" >= ""
			var b2 = "Hello" == "World"
			var b3 = "Stuff" != "Stuff"
			var b4 = "Hello" < "World"
		""".asGrovlinFile().resolved()

		val varDecls = grovlinFile.collectByType<VarDeclaration>()

		Assertions.assertThat(varDecls).allMatch { it.type == BoolType }
		Assertions.assertThat(varDecls).allMatch { it.type == BoolType }
		Assertions.assertThat(varDecls).allMatch { (it.value as BinaryExpression).left.evaluationType == StringType }
		Assertions.assertThat(varDecls).allMatch { (it.value as BinaryExpression).right.evaluationType == StringType }
	}

	@Test
	fun promoteIntToDecimalType() {
		val grovlinFile = """
			var a = 5
			var b = 5.0
			var c = a + b
		""".asGrovlinFile().resolved()

		val vars = grovlinFile.collectByType<VarDeclaration>()

		Assertions.assertThat(vars[0].type).isEqualTo(IntType)
		Assertions.assertThat(vars[0].evaluationType).isEqualTo(IntType)
		Assertions.assertThat(vars[1].type).isEqualTo(DecimalType)
		Assertions.assertThat(vars[1].evaluationType).isEqualTo(DecimalType)
		Assertions.assertThat(vars[2].type).isEqualTo(DecimalType)
		Assertions.assertThat(vars[2].evaluationType).isEqualTo(DecimalType)
	}

	@Test
	fun binaryOperationsOnlyForBooleans() {
		Assertions.assertThat("var b = true && false".asGrovlinFile().resolved().errors).isEmpty()
		Assertions.assertThat("var b = true || false".asGrovlinFile().resolved().errors).isEmpty()
		Assertions.assertThat("var b = true ^ false".asGrovlinFile().resolved().errors).isEmpty()
		Assertions.assertThat("var b = 1 && 2".asGrovlinFile().resolved().errors).hasSize(1)
		Assertions.assertThat("var b = 1.0 || 2".asGrovlinFile().resolved().errors).hasSize(1)
		Assertions.assertThat("var b = 1.0 ^ true".asGrovlinFile().resolved().errors).hasSize(1)
	}
}
