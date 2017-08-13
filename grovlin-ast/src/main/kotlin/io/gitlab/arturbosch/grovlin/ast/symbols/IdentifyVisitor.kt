package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.VariableDeclaration
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor

/**
 * @author Artur Bosch
 */
class IdentifyVisitor(val grovlinFile: GrovlinFile) : TreeBaseVisitor<Any>() {

	val fileScope = FileScope(grovlinFile.name)
	private var currentScope: Scope = fileScope

	override fun visit(varReference: VarReference, data: Any) {
		varReference.resolutionScope = currentScope
	}

	override fun visit(thisReference: ThisReference, data: Any) {
		thisReference.resolutionScope = currentScope
	}

	override fun visit(objectOrTypeType: ObjectOrTypeType, data: Any) {
		objectOrTypeType.resolutionScope = currentScope
	}

	override fun visit(callExpression: CallExpression, data: Any) {
		super.visit(callExpression, data)
		callExpression.resolutionScope = currentScope
	}

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		currentScope.declare(varDeclaration, grovlinFile)
		identifyVariable(varDeclaration)
		super.visit(varDeclaration, data)
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: Any) {
		currentScope.declare(propertyDeclaration, grovlinFile)
		identifyVariable(propertyDeclaration)
		super.visit(propertyDeclaration, data)
	}

	override fun visit(parameterDeclaration: ParameterDeclaration, data: Any) {
		currentScope.declare(parameterDeclaration, grovlinFile)
		identifyVariable(parameterDeclaration)
		super.visit(parameterDeclaration, data)
	}

	private fun identifyVariable(variableDeclaration: VariableDeclaration) {
		val variableSymbol = VariableSymbol(variableDeclaration.name)
		variableSymbol.def = variableDeclaration
		variableSymbol.scope = currentScope
		currentScope.define(variableSymbol)
		variableDeclaration.resolutionScope = currentScope
		variableDeclaration.symbol = variableSymbol
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		currentScope.declare(methodDeclaration, grovlinFile)
		val methodSymbol = MethodSymbol(methodDeclaration.name, methodDeclaration.type, currentScope)
		currentScope.define(methodSymbol)
		methodSymbol.def = methodDeclaration
		methodSymbol.scope = currentScope
		methodDeclaration.resolutionScope = currentScope
		methodDeclaration.symbol = methodSymbol
		currentScope = methodSymbol.parameterScope
		super.visit(methodDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: Any) {
		currentScope.declare(objectDeclaration, grovlinFile)
		val classSymbol = ClassSymbol(objectDeclaration.name, objectDeclaration.objectType, currentScope)
		currentScope.define(classSymbol)
		classSymbol.def = objectDeclaration
		classSymbol.scope = currentScope
		objectDeclaration.resolutionScope = currentScope
		objectDeclaration.symbol = classSymbol
		currentScope = classSymbol
		super.visit(objectDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		currentScope.declare(typeDeclaration, grovlinFile)
		val classSymbol = ClassSymbol(typeDeclaration.name, typeDeclaration.typeType, currentScope)
		currentScope.define(classSymbol)
		classSymbol.def = typeDeclaration
		classSymbol.scope = currentScope
		typeDeclaration.resolutionScope = currentScope
		typeDeclaration.symbol = classSymbol
		currentScope = classSymbol
		super.visit(typeDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(blockStatement: BlockStatement, data: Any) {
		val notClassOrFileScope = currentScope !is FileScope && currentScope !is ClassSymbol
		if (notClassOrFileScope) {
			val localScope = LocalScope("<block>", currentScope)
			currentScope = localScope
		}
		super.visit(blockStatement, data)
		if (notClassOrFileScope) {
			currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
		}
	}

	private fun assertEnclosingScope(): Scope {
		throw AssertionError("Unexpected null enclosing scope of $currentScope")
	}
}
