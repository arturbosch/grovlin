package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.AstNode
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.VariableDeclaration
import io.gitlab.arturbosch.grovlin.ast.validation.SemanticError
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor

/**
 * @author Artur Bosch
 */
class ResolutionVisitor(val grovlinFile: GrovlinFile,
						val fileScope: FileScope) : TreeBaseVisitor() {

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		super.visit(varDeclaration, data)
		resolveVariableSymbolType(varDeclaration)
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: Any) {
		super.visit(propertyDeclaration, data)
		resolveVariableSymbolType(propertyDeclaration)
	}

	override fun visit(parameterDeclaration: ParameterDeclaration, data: Any) {
		super.visit(parameterDeclaration, data)
		resolveVariableSymbolType(parameterDeclaration)
	}

	private fun resolveVariableSymbolType(variableDeclaration: VariableDeclaration) {
		val scope = variableDeclaration.resolutionScope ?: assertScopeResolved(variableDeclaration)
		val varType = variableDeclaration.type
		val symbol = scope.resolve(varType.name)
		symbol?.type = varType
		variableDeclaration.symbol?.type = symbol?.type
		varType.symbol = symbol
	}

	override fun visit(varReference: VarReference, data: Any) {
		super.visit(varReference, data)
		val scope = varReference.resolutionScope ?: assertScopeResolved(varReference)
		val symbol = scope.resolve(varReference.varName)
		varReference.symbol = symbol
		symbol?.type = symbol?.def?.type
		if (symbol?.def == null) {
			grovlinFile.addError(SemanticError(
					"Declaration for ${varReference.varName} not found.", varReference.position?.start))
		} else {
			varReference.reference.source = symbol.def as VariableDeclaration
		}
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		super.visit(methodDeclaration, data)
		val scope = methodDeclaration.resolutionScope ?: assertScopeResolved(methodDeclaration)
		val returnType = methodDeclaration.type
		val symbol = scope.resolve(returnType.name)
		returnType.symbol = symbol
	}

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		super.visit(typeDeclaration, data)
		val scope = typeDeclaration.resolutionScope ?: assertScopeResolved(typeDeclaration)
		for (extendedType in typeDeclaration.extendedTypes) {
			val symbol = scope.resolve(extendedType.name)
			extendedType.symbol = symbol
		}
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: Any) {
		super.visit(objectDeclaration, data)
		val scope = objectDeclaration.resolutionScope ?: assertScopeResolved(objectDeclaration)
		val extendedObject = objectDeclaration.extendedObject
		if (extendedObject != null) {
			val symbol = scope.resolve(extendedObject.name)
			extendedObject.symbol = symbol
		}
		for (extendedType in objectDeclaration.extendedTypes) {
			val symbol = scope.resolve(extendedType.name)
			extendedType.symbol = symbol
		}
	}

	override fun visit(thisReference: ThisReference, data: Any) {
		thisReference.symbol = thisReference.resolutionScope?.getEnclosingClass()
	}

	override fun visit(callExpression: CallExpression, data: Any) {
		super.visit(callExpression, data)
		val scopeSym = callExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(callExpression.name)
		callExpression.symbol = memberSym
	}

	override fun visit(getterAccessExpression: GetterAccessExpression, data: Any) {
		super.visit(getterAccessExpression, data)
		val scopeSym = getterAccessExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(getterAccessExpression.name)
		getterAccessExpression.symbol = memberSym
	}

	override fun visit(setterAccessExpression: SetterAccessExpression, data: Any) {
		super.visit(setterAccessExpression, data)
		val scopeSym = setterAccessExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(setterAccessExpression.name)
		setterAccessExpression.symbol = memberSym
	}

	private fun assertScopeResolved(node: AstNode): Scope =
			throw AssertionError("Scope of ${node.javaClass.simpleName} is not resolved!")

	// Computing static expression types
	// Literals have default builtin types


}
