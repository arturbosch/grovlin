package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
class GrovlinFile(override var name: String,
				  override var block: BlockStatement?)
	: Node(), NodeWithBlock, NodeWithName {

	fun findTypeByName(name: String): TypeDeclaration? = block?.statements
			?.filterIsInstance<TypeDeclaration>()
			?.find { it.name == name }

	fun findObjectByName(name: String): ObjectDeclaration? = block?.statements
			?.filterIsInstance<ObjectDeclaration>()
			?.find { it.name == name }

	fun findMethodByName(name: String): MethodDeclaration? = block?.statements
			?.filterIsInstance<MethodDeclaration>()
			?.find { it.name == name }

	fun topLevelStatements(): List<Statement> = block?.statements ?: emptyList()
}

const val DEFAULT_GROVLIN_FILE_NAME = "Program"

fun GrovlinParser.GrovlinFileContext.toAsT(fileName: String = DEFAULT_GROVLIN_FILE_NAME): GrovlinFile {
	val statements = statements().statement().mapTo(ArrayList()) { it.toAst(fileName) }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply { position = toPosition() }
	} else null
	return GrovlinFile(fileName, blockStatement).apply { position = toPosition() }
}
