package io.gitlab.arturbosch.grovlin.ast.resolution

import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.Node
import io.gitlab.arturbosch.grovlin.ast.NodeWithType
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.UnaryMinusExpression
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.operations.processNodesOfType

/**
 * @author Artur Bosch
 */

fun GrovlinFile.resolveTypes() = processNodesOfType<NodeWithType> {
	if (it.isUnsolved()) it.tryToSolve()
}

private fun NodeWithType.tryToSolve() = when (this) {
	is VarDeclaration -> type = resolveVarDeclaration()
	else -> throw UnsupportedOperationException("")
}

private fun VarDeclaration.resolveVarDeclaration(): Type {
	return if (!isUnsolved()) type else value.resolveType()
}

private fun Expression.resolveType(): Type = when (this) {
	is BinaryExpression -> {
		val leftType = left.resolveType()
		val rightType = right.resolveType()
		val type = if (rightType.name == leftType.name) {
			rightType
		} else if (rightType is PrimitiveType && leftType is PrimitiveType) {
			resolvePrimitiveType(leftType, rightType)
		} else {
			null
		}
		type ?: throw TypeMissMatchError(leftType, rightType, position)
	}
	is TypeConversion -> targetType
	is UnaryMinusExpression -> value.resolveType()
	is VarReference -> safeDeclaration().resolveVarDeclaration()
	is IntLit -> type
	is DecLit -> type
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

private fun VarReference.safeDeclaration() = this.reference.source ?: throw RelationsNotResolvedError(this)

fun resolvePrimitiveType(leftType: Type, rightType: Type): Type? = when {
	leftType is IntType && rightType is DecimalType -> rightType
	leftType is DecimalType && rightType is IntType -> leftType
	else -> null
}

class TypeMissMatchError(leftType: Type, rightType: Type, point: Position?) :
		IllegalStateException("Type mismatch at ${point?.start} - left=${leftType.name} and right=${rightType.name}")

class RelationsNotResolvedError(node: Node) :
		IllegalStateException("References not resolved for ${node.javaClass.simpleName} on ${node.position?.start}")