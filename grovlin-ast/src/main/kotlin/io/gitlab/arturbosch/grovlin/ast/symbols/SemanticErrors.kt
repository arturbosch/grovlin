package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.INVALID_POSITION
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.parser.FrontendError

/**
 * @author Artur Bosch
 */
open class SemanticError(val message: String, val position: Position?) : FrontendError {

	override fun formattedMessage() = "$message ${position ?: INVALID_POSITION}"
	override fun toString(): String = formattedMessage()
}

class IncompatibleArgumentTypes(fileName: String, methodCall: String,
								paramTypes: String, argumentTypes: String,
								positions: Position?)
	: SemanticError("$fileName:$positions: " +
		"Call to '$methodCall' with incompatible types '$argumentTypes', expected '$paramTypes'.", positions) {

	override fun formattedMessage() = message
}

class RedeclarationError(val id: String, vararg val positions: Position?) : SemanticError(id, positions[0]) {

	override fun formattedMessage() = "Redeclaration of '$id': ${positions
			.joinToString(", ") { it.toString() }}"
}

class ThisReferenceOutsideOfObjectScope(positions: Position?) : SemanticError(
		"This reference is used outside of object or type scope.", positions)

class MutationOfFinalVariable(varName: String, positions: Position?) : SemanticError(
		"Variable '$varName' is declared final and cannot be mutated.", positions)

class MissingOverride(varName: String, positions: Position?) : SemanticError(
		"Inherited member '$varName' is not declared as 'override'.", positions)

class IncompatibleReturnType(evaluationType: Type?, returnType: Type, positions: Position?) : SemanticError(
		"Return type '$evaluationType' does not match method return typ '$returnType'.", positions)

class SingleReturnAllowed(methodDeclaration: MethodDeclaration) : SemanticError(
		"Only a single return statement is allowed for method declaration " +
				"'${methodDeclaration.name}'.", methodDeclaration.position)

class ReturnMustBeLast(methodDeclaration: MethodDeclaration, positions: Position?) : SemanticError(
		"Return statement must be the last statement in '${methodDeclaration.name}'.", positions)
