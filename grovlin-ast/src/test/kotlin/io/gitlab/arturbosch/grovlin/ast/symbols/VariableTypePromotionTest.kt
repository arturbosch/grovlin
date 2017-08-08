package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class VariableTypePromotionTest {


	@Test
	fun relationalOperationsResultInBoolType() {
		val grovlinFile = "var b = 5 > 4\nvar b2 = 5.0 > 4".asGrovlinFile().resolved()

		val vars = grovlinFile.collectByType<VarDeclaration>()
		
		vars.forEach { assertTrue(it.type is BoolType, "Should be BoolType but was ${it.type.javaClass}") }
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
}
