package io.gitlab.arturbosch.grovlin.ast.symbols

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.parseFromTestResource
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class VariableResolutionTest {

	@Test
	fun resolveVariableInPrintStatement() {
		val file = parseFromTestResource("example.grovlin").resolved()

		val varReference = file.findVariableReferencesByName("a")[0]

		Assertions.assertThat(varReference.symbol?.def?.name == "a")
		Assertions.assertThat(varReference.symbol?.def).isInstanceOf(VarDeclaration::class.java)
	}

	@Test
	fun cantResolveInSameLine() {
		val grovlinFile = "var a = 1 + a".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors.find { "a" in it.message }).isNotNull()
	}


	@Test
	fun cantResolveIfDeclarationIsAfterReference() {
		val grovlinFile = "a = 5\nvar a = 1".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors.find { "a" in it.message }).isNotNull()
	}


	@Test
	fun cantResolveUnExistingValues() {
		val grovlinFile = "var b = 5\nvar a = 1 + c".asGrovlinFile().resolved()

		Assertions.assertThat(grovlinFile.errors.find { "c" in it.message }).isNotNull()
	}

	@Test
	fun resolveAssignment() {
		val grovlinFile = "var a = 5\na = 10".asGrovlinFile().resolved()

		assertThat(grovlinFile.collectByType<Assignment>()[0].symbol?.def, present())
	}

	@Test
	fun resolveAssignmentInMethod() {
		val grovlinFile = "object Node { def method() { var a = 5\na = 10 } }".asGrovlinFile().resolved()

		assertThat(grovlinFile.collectByType<Assignment>()[0].symbol?.def, present())
	}

	@Test
	fun resolveVarToPropertyDeclaration() {
		val grovlinFile = """
			type Box { Int data def theData() { print(data) } }
			object BoxImpl as Box { override Int data }
			program { print(BoxImpl().data) }
		""".asGrovlinFile().resolved()

		val reference = grovlinFile.findVariableReferencesByName("data").first()

		Assertions.assertThat(reference.symbol?.def).isInstanceOf(PropertyDeclaration::class.java)
	}
}
