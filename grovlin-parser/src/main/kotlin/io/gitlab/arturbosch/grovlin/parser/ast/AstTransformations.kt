package io.gitlab.arturbosch.grovlin.parser.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser.AssignmentContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.BinaryOperationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DecimalContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DecimalLiteralContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DefDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.ExpressionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.GrovlinFileContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.IntLiteralContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.IntegerContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.LambdaDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.MemberDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.MethodDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.ParenExpressionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.PrintContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.PropertyDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.StatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeConversionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.VarDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.VarReferenceContext
import java.util.ArrayList

/**
 * @author Artur Bosch
 */

fun GrovlinFileContext.toAsT(): GrovlinFile = GrovlinFile(line().mapTo(ArrayList()) { it.statement().toAst() })

private fun StatementContext.toAst(): Statement = when (this) {
	is MemberDeclarationContext -> memberDeclaration().toAst()
	is VarDeclarationContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst())
	is AssignmentContext -> Assignment(assignment().ID().text, assignment().expression().toAst())
	is PrintContext -> Print(print().expression().toAst())
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

private fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyDeclarationContext -> PropertyDeclaration(propertyDeclaration().assignment().ID().text,
			propertyDeclaration().assignment().expression().toAst())
	is TypeDeclarationContext -> TypeDeclaration(typeDeclaration().ID().text,
			typeDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() })
	is DefDeclarationContext -> defDeclaration().toAst()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

private fun DefDeclarationContext.toAst(): Statement = when (this) {
	is MethodDeclarationContext -> MethodDeclaration(methodDeclaration().ID().text,
			methodDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() })
	is LambdaDeclarationContext -> LambdaDeclaration(lambdaDeclaration().ID().text,
			lambdaDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() })
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun ExpressionContext.toAst(): Expression = when (this) {
	is BinaryOperationContext -> toAst()
	is IntLiteralContext -> IntLit(text)
	is DecimalLiteralContext -> DecLit(text)
	is ParenExpressionContext -> expression().toAst()
	is VarReferenceContext -> VarReference(text)
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst())
	"-" -> SubtractionExpression(left.toAst(), right.toAst())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst())
	"/" -> DivisionExpression(left.toAst(), right.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

