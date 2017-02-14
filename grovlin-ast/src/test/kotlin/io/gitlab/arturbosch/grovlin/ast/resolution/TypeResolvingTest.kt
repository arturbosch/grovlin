package io.gitlab.arturbosch.grovlin.ast.resolution

import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.parseFromTestResource
import io.gitlab.arturbosch.grovlin.ast.resolved
import org.junit.Test

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

		println(grovlinFile.asString())
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
}