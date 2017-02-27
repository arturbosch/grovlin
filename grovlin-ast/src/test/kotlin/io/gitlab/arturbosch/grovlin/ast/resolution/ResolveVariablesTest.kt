package io.gitlab.arturbosch.grovlin.ast.resolution

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.parseFromTestResource
import io.gitlab.arturbosch.grovlin.ast.resolved
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
		assert(varReference!!.reference.source!!.name == "a")
		assert(varReference.reference.source is VarDeclaration)
	}

	@Test
	fun cantResolveInSameLine() {
		val code = "var a = 1 + a"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<VarReference>()[0].reference.source == null)
	}


	@Test
	fun cantResolveIfDeclarationIsAfterReference() {
		val code = "a = 5\nvar a = 1"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<Assignment>()[0].reference.source == null)
	}


	@Test
	fun cantResolveUnExistingValues() {
		val code = "var b = 5\nvar a = 1 + c"
		val grovlinFile = code.parse().root!!.toAsT()

		grovlinFile.resolveSymbols()

		assert(grovlinFile.collectByType<VarReference>()[0].reference.source == null)
	}

	@Test
	fun resolveVarToPropertyDeclaration() {
		val grovlinFile = ("type Box { Int data def theData() { print(data) } }" +
				"\nobject BoxImpl as Box { override Int data }\nprogram { print(BoxImpl().data) }").asGrovlinFile().resolved()
		println(grovlinFile.asString())
		assertThat(grovlinFile.collectByType<VarReference>()[0].reference.source, present())
	}
}