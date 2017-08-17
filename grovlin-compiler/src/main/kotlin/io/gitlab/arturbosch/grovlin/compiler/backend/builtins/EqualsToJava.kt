package io.gitlab.arturbosch.grovlin.compiler.backend.builtins

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.EnclosedExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.UnaryExpr
import io.gitlab.arturbosch.grovlin.ast.EqualExpression
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import io.gitlab.arturbosch.grovlin.compiler.backend.toJava
import com.github.javaparser.ast.expr.Expression as JavaExpression

/**
 * @author Artur Bosch
 */
fun EqualExpression.builtinToJava(): JavaExpression {
	return binaryToEquals(left, right)
}

private fun binaryToEquals(left: Expression, right: Expression): JavaExpression {
	fun equalsCall(leftExpr: JavaExpression = left.toJava(), rightExpr: JavaExpression = right.toJava()) =
			MethodCallExpr(leftExpr, "equals", NodeList.nodeList(rightExpr))

	val leftType = left.evaluationType
	val rightType = right.evaluationType
	return when {
		leftType is PrimitiveType && rightType is PrimitiveType ->
			BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.EQUALS)
		leftType is ObjectOrTypeType && rightType is ObjectOrTypeType -> equalsCall()
		leftType is PrimitiveType && rightType is ObjectOrTypeType ->
			equalsCall(leftExpr = right.toJava(), rightExpr = left.toJava())
		leftType is ObjectOrTypeType && rightType is PrimitiveType -> equalsCall()
		else -> equalsCall()
	}
}

fun UnequalExpression.builtinToJava(): JavaExpression = when {
	left.evaluationType is PrimitiveType && right.evaluationType is PrimitiveType ->
		BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.NOT_EQUALS)
	else -> UnaryExpr(EnclosedExpr(binaryToEquals(left, right)), UnaryExpr.Operator.LOGICAL_COMPLEMENT)
}
