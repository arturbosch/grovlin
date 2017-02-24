package io.gitlab.arturbosch.grovlin.ast.resolution

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.parseFromTestResource
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class TypeResolvingTest {

	@Test
	fun resolveTypes() {
		val grovlinFile = "var a = 5\nprint(a)".asGrovlinFile().resolved()

		assert(grovlinFile.collectByType<VarDeclaration>()[0].type is IntType)
	}

	@Test
	fun resolveMixedIntAndDecimalTypes() {
		val grovlinFile = "var a = 5\nvar b = 5.0\n var c = a + b".asGrovlinFile().resolved()
		val vars = grovlinFile.collectByType<VarDeclaration>()

		assert(vars[0].type is IntType)
		assert(vars[1].type is DecimalType)
		assert(vars[2].type is DecimalType)
	}

	@Test
	fun varReferencesToDeclarationType() {
		val grovlinFile = "var a = 5\nvar b = 5\nprint(a + b)".asGrovlinFile().resolved()

		val references = grovlinFile.collectByType<VarReference>()

		assert(references.size == 2)
		assert(references.all { it.reference.source!!.type is IntType })
	}

	@Test
	fun resolveMoreTypes() {
		val grovlinFile = parseFromTestResource("example.grovlin")
		val varDeclaration = grovlinFile.collectByType<VarDeclaration>()[0]

		assert(varDeclaration.type is UnknownType)

		grovlinFile.resolved()

		assert(varDeclaration.type is IntType)
	}

	@Test
	fun resolutionOfUnknownTypeResultsInSemanticError() {
		val grovlinFile = "var a = Unknown".asGrovlinFile()
		val errors = grovlinFile.resolveTypes()

		assertThat(errors, hasSize(equalTo(1)))
	}

	@Test
	fun resolveRelationOperations() {
		val grovlinFile = "var b = 5 > 4\nvar b2 = 5.0 > 4".asGrovlinFile().resolved()

		val vars = grovlinFile.collectByType<VarDeclaration>()
		vars.forEach { assertTrue(it.type is BoolType, "Should be BoolType but was ${it.type.javaClass}") }
	}
}