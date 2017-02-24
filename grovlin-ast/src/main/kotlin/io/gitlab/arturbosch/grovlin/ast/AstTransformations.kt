package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser.*
import java.util.ArrayList
import java.util.LinkedList

/**
 * @author Artur Bosch
 */
fun GrovlinFileContext.toAsT(fileName: String = "Program"): GrovlinFile = GrovlinFile(fileName,
		statements().statement().mapTo(ArrayList()) { it.toAst(fileName) }, toPosition())

fun StatementContext.toAst(fileName: String = "Program"): Statement = when (this) {
	is ExpressionStatementContext -> ExpressionStatement(expressionStmt().expression().toAst(), toPosition())
	is MemberDeclarationStatementContext -> memberDeclaration().toAst()
	is VarDeclarationStatementContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst(), toPosition())
	is AssignmentStatementContext -> Assignment(Reference(assignment().ID().text), assignment().expression().toAst(), toPosition())
	is PrintStatementContext -> Print(print().expression().toAst(), toPosition())
	is ProgramStatementContext -> Program(fileName, program().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is IfStatementContext -> transformToIfStatement()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyMemberDeclarationContext -> PropertyDeclaration(propertyDeclaration().assignment().ID().text,
			propertyDeclaration().assignment().expression().toAst(), toPosition())
	is TypeMemberDeclarationContext -> TypeDeclaration(ObjectOrTypeType(typeDeclaration().typeName.text),
			typeDeclaration().extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) },
			typeDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is ObjectMemberDeclarationContext -> ObjectDeclaration(ObjectOrTypeType(objectDeclaration().objectName.text),
			ObjectOrTypeType(objectDeclaration().extendObject.text),
			objectDeclaration().extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) },
			objectDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() }, toPosition())
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
	is ParenExpressionContext -> ParenExpression(expression().toAst(), toPosition())
	is ThisExpressionContext -> ThisReference(Reference("this"), toPosition())
	is CallExpressionContext -> CallExpression(container?.toAst(), methodName.text, toPosition())
	is BinaryOperationContext -> toAst()
	is MinusExpressionContext -> MinusExpression(expression().toAst(), toPosition())
	is NotExpressionContext -> NotExpression(expression().toAst(), toPosition())
	is IntLiteralContext -> IntLit(text, toPosition())
	is DecimalLiteralContext -> DecLit(text, toPosition())
	is BoolLiteralContext -> BoolLit(text.toBoolean(), toPosition())
	is VarReferenceContext -> VarReference(Reference(text), toPosition())
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst(), toPosition())
	"-" -> SubtractionExpression(left.toAst(), right.toAst(), toPosition())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst(), toPosition())
	"/" -> DivisionExpression(left.toAst(), right.toAst(), toPosition())
	"&&" -> AndExpression(left.toAst(), right.toAst(), toPosition())
	"||" -> OrExpression(left.toAst(), right.toAst(), toPosition())
	"^" -> XorExpression(left.toAst(), right.toAst(), toPosition())
	"==" -> EqualExpression(left.toAst(), right.toAst(), toPosition())
	"!=" -> UnequalExpression(left.toAst(), right.toAst(), toPosition())
	">=" -> GreaterEqualExpression(left.toAst(), right.toAst(), toPosition())
	"<=" -> LessEqualExpression(left.toAst(), right.toAst(), toPosition())
	">" -> GreaterExpression(left.toAst(), right.toAst(), toPosition())
	"<" -> LessExpression(left.toAst(), right.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	is BoolContext -> BoolType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

private fun IfStatementContext.transformToIfStatement(): IfStatement {
	return IfStatement(condition = ifStmt().expression().toAst(),
			thenStatement = transformThenBlock(),
			elifs = transformElifStatements(),
			elseStatement = transformElseBlock(),
			position = toPosition())
}

private fun IfStatementContext.transformThenBlock() = BlockStatement(ifStmt().statements().statement().mapTo(ArrayList()) { it.toAst() },
		ifStmt().statements().toPosition())

private fun IfStatementContext.transformElseBlock(): BlockStatement? {
	val elseStmt = ifStmt().elseStmt() ?: return null
	return BlockStatement(elseStmt.statements().statement().mapTo(ArrayList()) { it.toAst() }, elseStmt.statements().toPosition())
}

private fun IfStatementContext.transformElifStatements(): MutableList<ElifStatement> {
	val elifs = ifStmt().elifStmt()
	return elifs?.mapTo(LinkedList()) {
		ElifStatement(condition = it.expression().toAst(),
				thenStatement = BlockStatement(it.statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition()),
				position = it.statements().toPosition())
	} ?: mutableListOf()
}

