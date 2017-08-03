package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class TypeResolutionVisitorTest {

	private val resolveVisitor = TypeResolutionVisitor()
	private val assertVisitor = TypeAssertionVisitor()

	@Test
	fun resolveMethod() {
		val file = """
			type Node {}
			object Leaf as Node {}
			def main() {}
			def gimmeBool(): Bool { return true }
			def gimmeNode(): Node { return Node() }
		""".asGrovlinFile()
		resolveVisitor.visit(file, Unit)
		assertVisitor.visit(file, Unit)
	}
}

class TypeAssertionVisitor : TreeBaseVisitor() {

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		when (typeDeclaration.name) {
			"Node" -> Assertions.assertThat(typeDeclaration.type.name).isEqualTo("Node")
		}
		super.visit(typeDeclaration, data)
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		val name = methodDeclaration.name
		when (name) {
			"main" -> Assertions.assertThat(methodDeclaration.type).isEqualTo(VoidType)
			"gimmeBool" -> Assertions.assertThat(methodDeclaration.type).isEqualTo(BoolType)
			"gimmeNode" -> Assertions.assertThat(methodDeclaration.type.name).isEqualTo("Node")
		}
		super.visit(methodDeclaration, data)
	}

	override fun visit(returnStatement: ReturnStatement, data: Any) {
		super.visit(returnStatement, data)
	}
}
