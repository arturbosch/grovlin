package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.symbols.IdentifyVisitor
import io.gitlab.arturbosch.grovlin.ast.symbols.ResolutionVisitor
import io.gitlab.arturbosch.grovlin.ast.symbols.SemanticError

/**
 * @author Artur Bosch
 */
class GrovlinFile(override var name: String,
				  override var block: BlockStatement?)
	: Node(), NodeWithBlock, NodeWithName {

	val errors: MutableList<SemanticError> = mutableListOf()

	fun addError(error: SemanticError) {
		errors.add(error)
	}
}

const val DEFAULT_GROVLIN_FILE_NAME = "MainGv"

fun GrovlinFile.identify(): GrovlinFile {
	val visitor = IdentifyVisitor(this)
	visitor.visit(this, Unit)
	this.resolutionScope = visitor.fileScope
	return this
}

fun GrovlinFile.resolve(): GrovlinFile {
	val visitor = ResolutionVisitor(this)
	visitor.visit(this, Unit)
	return this
}
