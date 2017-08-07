package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor

/**
 * @author Artur Bosch
 */
class IdentifyVisitor(val grovlinFile: GrovlinFile) : TreeBaseVisitor() {

	val fileScope = FileScope(grovlinFile.name)
	var currentScope: Scope = fileScope

	override fun visit(varReference: VarReference, data: Any) {
		varReference.resolutionScope = currentScope
		super.visit(varReference, data)
	}

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		val type = if (varDeclaration.type != UnknownType) varDeclaration.type else null
		val variableSymbol = VariableSymbol(varDeclaration.name, type)
		variableSymbol.def = varDeclaration
		variableSymbol.scope = currentScope
		currentScope.define(variableSymbol)
		varDeclaration.resolutionScope = currentScope
		super.visit(varDeclaration, data)
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		val methodSymbol = MethodSymbol(methodDeclaration.name, methodDeclaration.type, currentScope)
		currentScope.define(methodSymbol)
		methodSymbol.def = methodDeclaration
		methodDeclaration.resolutionScope = currentScope
		currentScope = methodSymbol.parameterScope
		super.visit(methodDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	private fun assertEnclosingScope(): Scope {
		throw AssertionError("Unexpected null enclosing scope of $currentScope")
	}

	override fun visit(blockStatement: BlockStatement, data: Any) {
		val notFileScope = currentScope !is FileScope
		if (notFileScope) {
			val localScope = LocalScope("<block>", currentScope)
			currentScope = localScope
		}
		super.visit(blockStatement, data)
		if (notFileScope) {
			currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
		}
	}
}
