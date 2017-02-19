package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser.*
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
fun GrovlinFileContext.toAsT(fileName: String = "Program"): GrovlinFile = GrovlinFile(fileName,
		statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())

fun StatementContext.toAst(): Statement = when (this) {
	is MemberDeclarationStatementContext -> memberDeclaration().toAst()
	is VarDeclarationStatementContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst(), toPosition())
	is AssignmentStatementContext -> Assignment(Reference(assignment().ID().text), assignment().expression().toAst(), toPosition())
	is PrintStatementContext -> Print(print().expression().toAst(), toPosition())
	is ProgramStatementContext -> Program(program().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyMemberDeclarationContext -> PropertyDeclaration(propertyDeclaration().assignment().ID().text,
			propertyDeclaration().assignment().expression().toAst(), toPosition())
	is TypeMemberDeclarationContext -> TypeDeclaration(typeDeclaration().ID().text,
			typeDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is DefMemberDeclarationContext -> defDeclaration().toAst()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun DefDeclarationContext.toAst(): Statement = when (this) {
	is MethodDefinitionContext -> MethodDeclaration(methodDeclaration().ID().text,
			methodDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is LambdaDefinitionContext -> LambdaDeclaration(lambdaDeclaration().ID().text,
			lambdaDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun ExpressionContext.toAst(): Expression = when (this) {
	is BinaryOperationContext -> toAst()
	is IntLiteralContext -> IntLit(text, toPosition())
	is DecimalLiteralContext -> DecLit(text, toPosition())
	is ParenExpressionContext -> expression().toAst()
	is VarReferenceContext -> VarReference(Reference(text), toPosition())
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst(), toPosition())
	"-" -> SubtractionExpression(left.toAst(), right.toAst(), toPosition())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst(), toPosition())
	"/" -> DivisionExpression(left.toAst(), right.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

