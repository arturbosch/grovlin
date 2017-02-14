package io.gitlab.arturbosch.grovlin.ast.resolution

import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.parseFromTestResource
import io.gitlab.arturbosch.grovlin.ast.toAsT
import io.gitlab.arturbosch.grovlin.parser.parse
import org.junit.Test

/**
 * @author Artur Bosch
 */
class ResolveVariablesTest {

	@Test
	fun resolveVariableInPrintStatement() {
		val file = parseFromTestResource("example.grovlin")

		file.resolveSymbols()

		val varReference = file.collectByType<VarReference>().find { it.reference.name == "a" }
		assert(varReference!!.reference.referred!!.name == "a")
		assert(varReference.reference.referred is VarDeclaration)
	}

	@Test
	fun cantResolveInSameLine() {
		val code = "var a = 1 + a"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<VarReference>()[0].reference.referred == null)
	}


	@Test
	fun cantResolveIfDeclarationIsAfterReference() {
		val code = "a = 5\nvar a = 1"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<Assignment>()[0].reference.referred == null)
	}


	@Test
	fun cantResolveUnexistingValues() {
		val code = "var b = 5\nvar a = 1 + c"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<VarReference>()[0].reference.referred == null)
	}
}