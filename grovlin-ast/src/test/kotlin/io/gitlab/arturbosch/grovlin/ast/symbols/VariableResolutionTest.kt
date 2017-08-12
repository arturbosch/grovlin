package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class VariableResolutionTest {

	@Test
	fun varDeclInferredFromIntLit() {
		val grovlinFile = "var a = 5\nprint(a)".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.findVariableByName("a")?.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun varReferencesEvaluationTypeInferredFromVarDecl() {
		val grovlinFile = "var a = 5\nvar b = 5\nprint(a + b)".asGrovlinFile().resolved()

		val references = grovlinFile.collectByType<VarReference>()

		Assertions.assertThat(references).hasSize(2)
		Assertions.assertThat(references).allMatch { it.evaluationType == IntType }
	}

	@Test
	fun varReferencesEvaluationTypeInferredFromPropertyDecl() {
		val grovlinFile = """
			type Example {
				Int age = 5
				def speak() {
					print(age)
				}
			}
		""".asGrovlinFile()

		val varDeclaration = grovlinFile.findByType<VarReference>()!!

		Assertions.assertThat(varDeclaration.evaluationType).isNull()

		grovlinFile.resolved()

		Assertions.assertThat(varDeclaration.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun unknownTypesResultsInSemanticError() {
		val grovlinFile = "var a = b".asGrovlinFile().resolved()

		val errors = grovlinFile.errors

		Assertions.assertThat(errors).anySatisfy { "'a'" in it.message }
		Assertions.assertThat(errors).anySatisfy { "'b'" in it.message }
	}

	@Test
	fun varDeclsTypeInferredFromIntRange() {
		val grovlinFile = "for i : 1..10 {}".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.findByType<VarDeclaration>()?.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun varReferenceTypeInferredFromVarDeclOutsideOfWhileLoop() {
		val grovlinFile = """
			var i = 5
			while i > 0 {
				println(i)
				i = i - 1
			}
		""".asGrovlinFile().resolved()

		val assignment = grovlinFile.findByType<Assignment>()

		Assertions.assertThat(assignment?.evaluationType).isEqualTo(IntType)
	}

	@Test
	fun varReferenceInAssignmentInferredFromVarDeclOutsideOfWhileLoop() {
		val grovlinFile = """
			def loop(Int n): String {
				var current = n
				while current > 0 {
					print(".")
					current = current - 1
				}
				println()
				return "I 'have' finished!"
			}
		""".asGrovlinFile().resolved()

		val assignment = grovlinFile.findByType<Assignment>()
		val currentDecl = grovlinFile.findByType<VarDeclaration>()

		Assertions.assertThat(assignment?.evaluationType).isEqualTo(IntType)
		Assertions.assertThat(assignment?.varReference?.symbol?.def).isEqualTo(currentDecl)
	}
}
